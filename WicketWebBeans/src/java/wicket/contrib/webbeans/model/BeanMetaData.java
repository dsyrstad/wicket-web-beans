/*---
   Copyright 2006-2007 Visual Systems Corporation.
   http://www.vscorp.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
        http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
---*/
package wicket.contrib.webbeans.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;

import wicket.Component;
import wicket.contrib.webbeans.fields.EmptyField;

/**
 * Represents the metadata for a bean properties and actions. All beans must be Serializable by Wicket
 * convention.<p/>
 * 
 * By default, the metadata originates by convention:<p/>
 * <ul>
 * <li>Label names for properties are derived from the JavaBean spec "displayName" or 
 *     from the property name. E.g.,
 *      "customerName" becomes "Customer Name"; "address2" becomes "Address 2".
 * </li>
 * <li>Field components for the Java primitive/wrapper types, enum types,
 *      java.util.Date and those classes deriving from it, and java.util.Lists are pre-configured.
 * </li>
 * <li>All JavaBean properties are displayed.
 * </li>
 * <li>All fields are editable if viewOnly (see constructor) is false. 
 *      Otherwise they are all view-only.
 * </li>
 * <li>If a property is not writable, it is displayed view-only.
 * </li>
 * <li>All fields are displayed in the order the are reflected.
 * </li>
 * <li>All fields are displayed in a single default tab.
 * </li>
 * </ul> 
 * <p/>
 * 
 * The Field types for classes can be specified/overridden in the given ComponentRegistry.  
 * <p/>
 * 
 * Normally, this class does what you would expect. 
 * However, you can override the default conventions by specifying the exceptions in 
 * the given Component's ".beanprops" file (see constructor). 
 * <p/>
 * 
 * If no tabs are specified, a default tab with an id of "DEFAULT_TAB" and a label based on the bean class name. 
 * Property-level settings override Bean-level settings. 
 * <p/>
 * 
 * Certain components that use BeanMetaData listen to PropertyChangeEvents. If your bean implements
 * addPropertyChangeListener/removePropertyChangeListener bean methods, the component can listen for 
 * property changes and update components on the page dynamically.
 * <p/>
 *  
 * @author Dan Syrstad
 */
public class BeanMetaData extends MetaData implements Serializable
{
    private static final long serialVersionUID = -4705317346444856939L;

    private static Logger logger = Logger.getLogger(BeanMetaData.class.getName());

    private static final Class[] PROP_CHANGE_LISTENER_ARG = new Class[] { PropertyChangeListener.class };
    /** Cache of beanprops files, already parsed. Key is the beanprops name, value is a List of Bean ASTs. */ 
    private static final Map<String, List<Bean>> cachedBeanProps = new HashMap<String, List<Bean>>(); 
    
    public static final String PARAM_VIEW_ONLY = "viewOnly";
    public static final String PARAM_DISPLAYED = "displayed";
    public static final String PARAM_TABS = "tabs";
    public static final String PARAM_PROPS = "props";
    public static final String PARAM_ACTIONS = "actions";
    public static final String PARAM_LABEL = "label";

    public static final String ACTION_PROPERTY_PREFIX = "action.";
    public static final String DEFAULT_TAB_ID = "DEFAULT_TAB";
    
    private Class beanClass;
    private String context;
    private Component component;
    private ComponentRegistry componentRegistry;
    
    // List of all properties.
    private List<ElementMetaData> elements = new ArrayList<ElementMetaData>();
    private List<TabMetaData> tabs = new ArrayList<TabMetaData>();
    
    private boolean hasAddPropertyChangeListenerMethod;
    private boolean hasRemovePropertyChangeListenerMethod;
    
    
    /**
     * Construct a BeanMetaData. See class documentation for a description of the Localizer
     * properties used.
     *
     * @param beanClass the bean's class.
     * @param context specifies a context to use when looking up beans in beanprops. May be null to not
     *  use a context.
     * @param component the component used to get the Localizer.
     * @param componentRegistry the ComponentRegistry used to determine visual components. May be null.
     * @param viewOnly if true, specifies that the entire bean is view-only. This can be overridden by the
     *  Localizer configuration.
     */
    public BeanMetaData(Class beanClass, String context, Component component, ComponentRegistry componentRegistry, boolean viewOnly)
    {
        if (beanClass.isAssignableFrom(Serializable.class)) {
            throw new IllegalArgumentException("bean must be Serializable. It is " + beanClass);
        }
        
        this.beanClass = beanClass;
        this.context = context;
        this.component = component;
        if (componentRegistry == null) {
            this.componentRegistry = new ComponentRegistry();
        }
        else {
            this.componentRegistry = componentRegistry;
        }
        
        setParameter(PARAM_VIEW_ONLY, String.valueOf(viewOnly));
        setParameter(PARAM_DISPLAYED, "true");
        setParameter(PARAM_LABEL, createLabel(beanClass.getSimpleName()) );

        init();
        
        consumeParameter(PARAM_LABEL);
        consumeParameter(PARAM_ACTIONS);
        consumeParameter(PARAM_PROPS);
        consumeParameter(PARAM_TABS);
        consumeParameter(PARAM_DISPLAYED);
        consumeParameter(PARAM_VIEW_ONLY);
    }

    /**
     * Determines if all parameters specified have been consumed for a specific tab, or all tabs.
     * 
     * @param unconsumedMsgs messages that report the parameter keys that were specified but not consumed.
     * @param tabMetaData the tab to be checked. If null, all elements and tabs are checked.
     * 
     * @return true if all parameters specified have been consumed.
     */
    public boolean areAllParametersConsumed(Set<String> unconsumedMsgs, TabMetaData tabMetaData)
    {
        if (!super.areAllParametersConsumed("Bean " + beanClass.getSimpleName(), unconsumedMsgs)) {
            return false;
        }
        
        // Make sure all elements and tabs have their parameters consumed.
        for (ElementMetaData element : tabMetaData == null ? getDisplayedElements() : getTabElements(tabMetaData)) {
            if (!element.areAllParametersConsumed("Property " + element.getPropertyName(), unconsumedMsgs)) {
                return false;
            }
        }
        
        for (TabMetaData tab : tabMetaData == null ? tabs : Collections.singletonList(tabMetaData)) {
            if (!tab.areAllParametersConsumed("Tab " + tab.getId(), unconsumedMsgs)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Logs a warning if any parameter specified have not been consumed for a specific tab, or all tabs.
     * 
     * @param tabMetaData the tab to be checked. If null, all elements and tabs are checked.
     */
    public void warnIfAnyParameterNotConsumed(TabMetaData tabMetaData)
    {
        Set<String> msgs = new HashSet<String>();
        if (!areAllParametersConsumed(msgs, tabMetaData)) {
            for (String msg : msgs) {
                logger.warning(msg);
            }
        }
    }
    
    private Method getAddPropertyChangeListenerMethod()
    {
        try {
            return beanClass.getMethod("addPropertyChangeListener", PROP_CHANGE_LISTENER_ARG);
        }
        catch (Exception e) {
            // Assume we don't have it.
            return null;
        }
    }

    private Method getRemovePropertyChangeListenerMethod()
    {
        try {
            return beanClass.getMethod("removePropertyChangeListener", PROP_CHANGE_LISTENER_ARG);
        }
        catch (Exception e) {
            // Assume we don't have it.
            return null;
        }
    }

    private void init()
    {
        // Check if bean supports PropertyChangeListeners.
        hasAddPropertyChangeListenerMethod = getAddPropertyChangeListenerMethod() != null;
        hasRemovePropertyChangeListenerMethod = getRemovePropertyChangeListenerMethod() != null;
        
        // Create defaults based on the bean itself.
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(beanClass);
        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            
            // Skip getClass() and methods that are not readable or hidden.
            if (name.equals("class") || descriptor.getReadMethod() == null || descriptor.isHidden()) {
                continue;
            }
            
            String label = descriptor.getDisplayName();
            if (label.equals(name)) {
                label = createLabel(name);
            }

            ElementMetaData propertyMeta = new ElementMetaData(this, name, label, descriptor.getPropertyType());
            propertyMeta.setViewOnly( isViewOnly() );
            elements.add(propertyMeta);

            if (descriptor.getWriteMethod() == null) {
                propertyMeta.setViewOnly(true);
            }
        }

        String propFileName = component.getClass().getSimpleName() + ".beanprops";
        List<Bean> beanSpecs = cachedBeanProps.get(propFileName);
        if (beanSpecs == null) {
            // It's OK not to have a beanprops file. We can deduce the parameters by convention. 
            InputStream propsStream = component.getClass().getResourceAsStream(propFileName);
            if (propsStream != null) {
                try {
                    beanSpecs = new BeanPropsParser(propFileName, propsStream).parse();
                    cachedBeanProps.put(propFileName, beanSpecs);
                }
                finally {
                    try { propsStream.close(); } catch (IOException e) { /* Ignore */ }
                }
            }
        }
        
        if (beanSpecs != null) {
            processBeanSpecs(beanSpecs);
        }

        // Process Bean-level parameters
        if (!getBooleanParameter(PARAM_DISPLAYED)) {
            elements.clear();
            tabs.clear();
        }
        
        Boolean beanViewOnly = (getParameter(PARAM_VIEW_ONLY) == null ? null : isViewOnly());
        
        // Configure tabs
        if (tabs.isEmpty()) {
            // Create single default tab.
            tabs.add( new TabMetaData(this, DEFAULT_TAB_ID, getParameter(PARAM_LABEL) ) );
        }
        
        String defaultTabId = tabs.get(0).getId();
        
        // Post-process each property based on bean parameters
        for (ElementMetaData elementMeta : elements) {
            // If bean specified view only, but element didn't, set it to the bean's value.
            if (beanViewOnly != null && !elementMeta.isViewOnlySetExplicitly()) {
                // Don't override default view-only (based on bean) if bean is not viewOnly.
                if (!elementMeta.isViewOnly()) {
                    elementMeta.setViewOnly(beanViewOnly);
                }
            }
            
            // If element is not on a tab, add it to the first. If it's an action, it must have been assigned an order to
            // appear on a tab. Otherwise it is a global action.
            if (elementMeta.getTabId() ==  null &&
                (!elementMeta.isAction() || 
                 (elementMeta.isAction() && elementMeta.getOrder() != ElementMetaData.DEFAULT_ORDER))) {
                elementMeta.setTabId(defaultTabId);
            }
        }

        Collections.sort(elements, new Comparator<ElementMetaData>() {
            public int compare(ElementMetaData o1, ElementMetaData o2)
            {
                return (o1.getOrder() > o2.getOrder() ? 1 : (o1.getOrder() < o2.getOrder() ? -1 : 0));
            }
        });
    }
    
    /**
     * Process bean ASTs that apply to this bean. 
     *
     * @param beans the Bean ASTs.
     */
    private void processBeanSpecs(List<Bean> beans)
    {
        // Determine the hierarchy of Bean ASTs. I.e., the default Bean is always processed first, followed by those that
        // extend it, etc.
        // This acts as a stack.
        List<Bean> beansHier = new ArrayList<Bean>();
        String currContext = context;
        // Note: Limit cyclical specs (e.g., A extends B, B extends A). This also limits the maximum hierarchy depth to the same 
        // amount, which should be plenty.
        for (int limit = 0; limit < 20; ++limit) { 
            Bean bean = getBean(beans, currContext);
            beansHier.add(0, bean);
            if (currContext == null) {
                // Just processed the default context, so stop.
                break;
            }
            
            currContext = bean.getExtendsContext();
        }
        
        // Apply beans in order from highest to lowest. The default context will always be first.
        for (Bean bean : beansHier) {
            applyBean(bean);
        }
    }
    
    /**
     * Applies a Bean AST to this meta data.
     *
     * @param bean
     */
    private void applyBean(Bean bean)
    {
        // Process actions first.
        for (Parameter param :  bean.getParameters()) {
            if (param.getName().equals(PARAM_ACTIONS)) {
                applyActions(param.getValues());
            }
        }
        
        for (Parameter param :  bean.getParameters()) {
            String name = param.getName();
            if (name.equals(PARAM_PROPS)) {
                applyProps(param.getValues(), null);
            }
            else if (name.equals(PARAM_TABS)) {
                // Ignore - processed below.
            }
            else if (name.equals(PARAM_ACTIONS)) {
                // Ignore - already processed above.
            }
            else {
                // Just handle a regular single-valued parameter.
                List<ParameterValue> values = param.getValues();
                if (values.size() != 1) {
                    throw new RuntimeException("Parameter " + name + " on bean " + bean.getName() + " does not specify exactly one value.");
                }
                
                String value = values.get(0).getValue(component);
                setParameter(name, value);
            }
        }

        // Process tabs last.
        for (Parameter param :  bean.getParameters()) {
            if (param.getName().equals(PARAM_TABS)) {
                applyTabs(param.getValues());
            }
        }
    }
    
    /**
     * Applies a Bean's "props" to each ElementMetaData.
     *
     * @param values
     * @param tabId the tab id to apply to the properties. May be null, in which case the tab id is not affected.
     */
    void applyProps(List<ParameterValue> values, String tabId)
    {
        int order = 1;
        for (ParameterValue value : values) {
            String elementName = value.getValue();
            boolean removeElement = false;
            if (elementName.startsWith("-") && elementName.length() > 1) {
                elementName = elementName.substring(1);
                removeElement = true;
            }
            
            ElementMetaData element = findElementAddPseudos(elementName);
            if (removeElement) {
                elements.remove(element);
            }
            else {
                List<Parameter> elementParams = value.getParameters();
                element.applyBeanProps(elementParams);
                if (element.getOrder() == ElementMetaData.DEFAULT_ORDER) {
                    element.setOrder(order++);
                }
                
                if (tabId != null) {
                    element.setTabId(tabId);
                }
            }
        }
    }
    
    /**
     * Applies a Bean's "actions" by adding ElementMetaData.
     *
     * @param values
     */
    private void applyActions(List<ParameterValue> values)
    {
        // Add action to the list of elements
        for (ParameterValue value : values) {
            String elementName = value.getValue();
            boolean removeElement = false;
            if (elementName.startsWith("-") && elementName.length() > 1) {
                elementName = elementName.substring(1);
                removeElement = true;
            }
            
            String actionName = ACTION_PROPERTY_PREFIX + elementName;
            ElementMetaData element = findElement(actionName);
            if (removeElement) {
                if (element == null) {
                    throw new RuntimeException("Action " + actionName + " does not exist in exposed list of actions.");
                }
                
                elements.remove(element);
            }
            else {
                if (element == null) {
                    element = new ElementMetaData(this, actionName, createLabel(elementName), null);
                    element.setAction(true);
                    elements.add(element);
                }
                
                List<Parameter> elementParams = value.getParameters();
                element.applyBeanProps(elementParams);
            }
        }
    }
    
    /**
     * Applies a Bean's "tabs" by adding ElementMetaData.
     *
     * @param values
     */
    private void applyTabs(List<ParameterValue> values)
    {
        // Add tab to the list of tabs
        for (ParameterValue value : values) {
            String tabName = value.getValue();
            boolean removeTab = false;
            if (tabName.startsWith("-") && tabName.length() > 1) {
                tabName = tabName.substring(1);
                removeTab = true;
            }
            
            TabMetaData foundTab = null;
            for (TabMetaData tab : tabs) {
                if (tab.getId().equals(tabName)) {
                    foundTab = tab;
                    break;
                }
            }
            
            if (removeTab) {
                if (foundTab == null) {
                    throw new RuntimeException("Tab " + tabName + " does not exist in exposed list of tabs.");
                }
                
                tabs.remove(foundTab);
            }
            else {
                if (foundTab == null) {
                    foundTab = new TabMetaData(this, tabName, createLabel(tabName));
                    tabs.add(foundTab);
                }
                
                List<Parameter> tabParams = value.getParameters();
                foundTab.applyBeanProps(tabParams);
            }
        }
    }
    
    /**
     * Gets the Bean from the list with the specified context.
     *
     * @param beans
     * @param context the context. May be null for the default context.
     * 
     * @return the Bean.
     * @throws RuntimeException if the context doesn't exist. Note that the default context
     *  does not need to explicitly exist in beans.
     */
    private Bean getBean(List<Bean> beans, String context)
    {
        String fullName = beanClass.getName();
        String shortName = beanClass.getSimpleName();
        for (Bean bean : beans) {
            String beanName = bean.getName();
            if (shortName.equals(beanName) || fullName.equals(beanName)) {
                String beanContext = bean.getContext();
                if ((context == null && beanContext == null) ||
                    (context != null && context.equals(beanContext))) {
                    return bean;
                }
            }
        }
        
        // Default context implicitly exists.
        if (context == null) {
            return new Bean("", null, null, Collections.EMPTY_LIST);
        }
        
        throw new RuntimeException("Bean context [" + context + "] does not exist.");
    }
    
    /**
     * Finds the specified element in the list of all elements. Handles special
     * Pseudo property names (e.g., "EMPTY") by adding a new one to the list.
     * 
     * @param propertyName
     * 
     * @return the ElementMetaData.
     * 
     * @throws RuntimeException if property is not found.
     */
    private ElementMetaData findElementAddPseudos(String propertyName)
    {
        ElementMetaData prop;
        if (propertyName.equals("EMPTY")) {
            prop = new ElementMetaData(this, "EMPTY:" + elements.size(), "", Object.class);
            prop.setFieldType(EmptyField.class.getName());
            prop.setViewOnly(true);
            elements.add(prop);
        }
        else {
            prop = findElement(propertyName);
            if (prop == null) {
                throw new RuntimeException("Property: " + propertyName + " does not exist in exposed list of properties.");
            }
        }
        
        return prop;
    }

    /**
     * Finds the specified element in the list of all elements.
     * 
     * @param propertyName
     * 
     * @return the ElementMetaData or null if not found.
     */
    public ElementMetaData findElement(String propertyName)
    {
        for (ElementMetaData prop : elements) {
            if (prop.getPropertyName().equals(propertyName)) {
                return prop;
            }
        }
        
        return null;
    }
    
    /**
     * Creates a human readable label from a Java identifier.
     * 
     * @param identifier the Java identifier.
     * 
     * @return the label.
     */
    private static String createLabel(String identifier)
    {
        // Check for a complex property.
        int idx = identifier.lastIndexOf('.');
        if (idx < 0) {
            idx = identifier.lastIndexOf('$'); // Java nested classes.
        }
        
        if (idx >= 0 && identifier.length() > 1) {
            identifier = identifier.substring(idx + 1);
        }

        if (identifier.length() == 0) {
            return "";
        }
        
        char[] chars = identifier.toCharArray();
        StringBuffer buf = new StringBuffer(chars.length + 10);

        // Capitalize the first letter.
        buf.append(Character.toUpperCase(chars[0]));
        boolean lastLower = false;
        for (int i = 1; i < chars.length; ++i) {
            if (!Character.isLowerCase(chars[i])) {
                // Lower to upper case transition -- add space before it
                if (lastLower) {
                    buf.append(' ');
                }
            }

            buf.append(chars[i]);
            lastLower = Character.isLowerCase(chars[i]) || Character.isDigit(chars[i]);
        }

        return buf.toString();
    }
    
    public String getLabel()
    {
        return getParameter(PARAM_LABEL);
    }
    
    /**
     * @return the tabs defined for this bean. There will always be at least one tab.
     */
    public List<TabMetaData> getTabs()
    {
        return tabs;
    }

    /**
     * @return a list of all displayed elements for a tab.
     */
    public List<ElementMetaData> getTabElements(TabMetaData tab)
    {
        List<ElementMetaData> elems = new ArrayList<ElementMetaData>();
        for (ElementMetaData elem : elements) {
            if (elem.getTabId() != null && elem.getTabId().equals(tab.getId())) {
                elems.add(elem);
            }
        }
        
        return elems;
    }
    
    /**
     * @return a list of all displayed elements for a bean.
     */
    public List<ElementMetaData> getDisplayedElements()
    {
        return elements;
    }
    
    /**
     * Gets a list of actions that are not assigned to any particular placement within the bean.
     *
     * @return the list of global actions.
     */
    public List<ElementMetaData> getGlobalActions()
    {
        List<ElementMetaData> elems = new ArrayList<ElementMetaData>();
        for (ElementMetaData elem : elements) {
            if (elem.isAction() && elem.getOrder() == ElementMetaData.DEFAULT_ORDER) {
                elems.add(elem);
            }
        }
        
        return elems;
    }
    
    /**
     * @return the bean class.
     */
    public Class getBeanClass()
    {
        return beanClass;
    }
    
    /**
     * @return the component.
     */
    public Component getComponent()
    {
        return component;
    }
    
    /**
     * @return the componentRegistry.
     */
    public ComponentRegistry getComponentRegistry()
    {
        return componentRegistry;
    }
    
    /**
     * @return the context.
     */
    public String getContext()
    {
        return context;
    }

    /**
     * @return the viewOnly flag.
     */
    public boolean isViewOnly()
    {
        return getBooleanParameter(PARAM_VIEW_ONLY);
    }

    /**
     * @return the displayed flag.
     */
    public boolean isDisplayed()
    {
        return getBooleanParameter(PARAM_DISPLAYED);
    }

    /**
     * Adds a property change listener to the bean if it supports it. If it doesn't support
     * addition property change listeners, nothing happens.
     *
     * @param bean a bean corresponding to this BeanMetaData.
     * @param listener the {@link PropertyChangeListener}.
     */
    public void addPropertyChangeListener(Object bean, PropertyChangeListener listener)
    {
        if (!hasAddPropertyChangeListenerMethod) {
            return;
        }
        
        try {
            getAddPropertyChangeListenerMethod().invoke(bean, new Object[] { listener } );
        }
        catch (Exception e) {
            throw new RuntimeException("Error adding PropertyChangeListener: ", e);
        }
    }

    /**
     * Removes a property change listener to the bean if it supports it. If it doesn't support
     * removal of property change listeners, nothing happens.
     *
     * @param bean a bean corresponding to this BeanMetaData.
     * @param listener the {@link PropertyChangeListener}.
     */
    public void removePropertyChangeListener(Object bean, PropertyChangeListener listener)
    {
        if (!hasRemovePropertyChangeListenerMethod) {
            return;
        }
        
        try {
            getRemovePropertyChangeListenerMethod().invoke(bean, new Object[] { listener } );
        }
        catch (Exception e) {
            throw new RuntimeException("Error removing PropertyChangeListener: ", e);
        }
    }
}
