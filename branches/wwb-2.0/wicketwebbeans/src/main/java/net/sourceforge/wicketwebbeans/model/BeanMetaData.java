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
package net.sourceforge.wicketwebbeans.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.sourceforge.wicketwebbeans.actions.BeanSubmitButton;
import net.sourceforge.wicketwebbeans.annotations.Action;
import net.sourceforge.wicketwebbeans.annotations.Bean;
import net.sourceforge.wicketwebbeans.annotations.Beans;
import net.sourceforge.wicketwebbeans.annotations.Property;
import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.containers.BeanGridPanel;
import net.sourceforge.wicketwebbeans.fields.EmptyField;
import net.sourceforge.wicketwebbeans.fields.Field;
import net.sourceforge.wicketwebbeans.model.api.JBeans;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;


/**
 * Represents the metadata for a bean. 
 * TODO this is really component configuration. Only some components deal with the bean, others deal with other
 * components. Maybe beanprops files should become .wwb files.
 * <p/>
 *  
 * @author Dan Syrstad
 */
public class BeanMetaData extends MetaData implements Serializable
{
    private static final long serialVersionUID = -4705317346444856939L;

    private static Logger logger = Logger.getLogger(BeanMetaData.class.getName());
    /** Cache of beanprops files, already parsed. Key is the beanprops name, value is a List of Beans. */
    private static final Map<String, CachedBeanProps> cachedBeanProps = new HashMap<String, CachedBeanProps>();
    
    private String componentName;
    private URL url;
    private ComponentRegistry componentRegistry;

    /**
     * Construct a BeanMetaData. If componentName is null, metadata is derived from a component
     * named "ROOT". 
     *
     * @param componentName the componentName to be used for metadata. May be null.
     * @param url the URL of the WWB configuration.
     * @param context specifies a context to use when looking up beans in beanprops. May be null to not
     *  use a context.
     * @param componentRegistry the ComponentRegistry used to determine visual components. May be null to use the default.
     */
    public BeanMetaData(String componentName, URL url, ComponentRegistry componentRegistry)
    {
        assert url != null;

        if (componentName != null) {
            this.componentName = componentName;
        }
        else {
            this.componentName = "ROOT";
        }
        
        this.url = url;
        
        if (componentRegistry == null) {
            this.componentRegistry = new ComponentRegistry();
        }
        else {
            this.componentRegistry = componentRegistry;
        }
        
        collectFromBeanProps();
    }

    /**
     * Determines if all parameters specified have been consumed.
     * 
     * @param unconsumedMsgs a set of messages that are returned with the parameter keys that were specified but not consumed.
     * 
     * @return true if all parameters specified have been consumed.
     */
    public boolean areAllParametersConsumed(Set<String> unconsumedMsgs)
    {
        if (!super.areAllParametersConsumed("Component " + componentName, unconsumedMsgs)) {
            return false;
        }

        // Make sure all elements have their parameters consumed.
        for (ElementMetaData element : getDisplayedElements()) {
            if (!element.areAllParametersConsumed("Property " + element.getPropertyName(), unconsumedMsgs)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Logs a warning if any parameter specified have not been consumed.
     */
    public void warnIfAnyParameterNotConsumed()
    {
        Set<String> msgs = new HashSet<String>();
        if (!areAllParametersConsumed(msgs)) {
            for (String msg : msgs) {
                logger.warning(msg);
            }
        }
    }
    
    /**
     * Collection Beans from the beanprops file, if any.
     */
    private void collectFromBeanProps()
    {
        long timestamp = 0;
        if (url.getProtocol().equals("file")) {
            try {
                timestamp = new File(url.toURI()).lastModified();
            }
            catch (URISyntaxException e) { /* Ignore - treat as zero */ }
        }
        
        String cacheKey = beanClass.getName() + ':' + propFileName;
        CachedBeanProps beanprops = cachedBeanProps.get(cacheKey);
        if (beanprops == null || beanprops.getModTimestamp() != timestamp) {
            if (beanprops != null) {
                logger.info("File changed: " + propFileName + " re-reading.");
            }
            
            // It's OK not to have a beanprops file. We can deduce the parameters by convention. 
            InputStream propsStream = component.getClass().getResourceAsStream(propFileName);
            if (propsStream != null) {
                try {
                    JBeans beans = new BeanPropsParser(propFileName, propsStream).parseToJBeans(this);
                    beanprops = new CachedBeanProps(beans, timestamp);
                    cachedBeanProps.put(cacheKey, beanprops);
                }
                finally {
                    try { propsStream.close(); } catch (IOException e) { /* Ignore */ }
                }
            }
        }
        
        if (beanprops != null) {
            collectBeansAnnotation(beanprops.getBeans(), false);
        }
    }
    
    /**
     * Derive metadata from standard annotations such as JPA and FindBugs.
     *
     * @param descriptor
     * @param elementMetaData
     */
    private void deriveElementFromAnnotations(PropertyDescriptor descriptor, ElementMetaData elementMetaData)
    {
        // NOTE: !!! The annotation classes must be present at runtime, otherwise getAnnotations() doesn't 
        // return the annotation.
        Method readMethod = descriptor.getReadMethod();
        if (readMethod != null) {
            processElementAnnotations(elementMetaData, readMethod.getAnnotations());
        }

        Method writeMethod = descriptor.getWriteMethod();
        if (writeMethod != null) {
            processElementAnnotations(elementMetaData, writeMethod.getAnnotations());
        }
    }
    
    /**
     * Process annotations for {@link #deriveElementFromAnnotations(PropertyDescriptor, ElementMetaData)}.
     *
     * @param elementMetaData
     * @param annotations
     */
    private void processElementAnnotations(ElementMetaData elementMetaData, Annotation[] annotations)
    {
        if (annotations == null) {
            return;
        }
        
        // Note: We only reference the annotations using their string name, not the class.
        // If we referenced the class, we'd have a dependency on those classes.
        // We also have to access the values by reflection so we don't depend on the class.
        for (Annotation annotation : annotations) {
            Class<?> annotationType = annotation.annotationType();
            String name = annotationType.getName();
            
            if (name.equals("javax.persistence.Column")) {
                elementMetaData.setMaxLength( (Integer)invokeAnnotationMethod(annotation, "length") );
                elementMetaData.setRequired( !(Boolean)invokeAnnotationMethod(annotation, "nullable") );
            }
            else if (name.equals("javax.jdo.annotations.Column")) {
                elementMetaData.setMaxLength( (Integer)invokeAnnotationMethod(annotation, "length") );
                elementMetaData.setRequired( "false".equals((String)invokeAnnotationMethod(annotation, "allowsNull")) );
                elementMetaData.setDefaultValue( (String)invokeAnnotationMethod(annotation, "defaultValue") );
            }
            else if (annotationType == Property.class) {
                processPropertyAnnotation((Property)annotation, elementMetaData);
            }
        }
    }
    
    /**
     * Invokes an annotation method to get a value, possibly returning null if no value or if the method doesn't exist.
     */
    private Object invokeAnnotationMethod(Annotation annotation, String methodName)
    {
        try {
            return MethodUtils.invokeExactMethod(annotation, methodName, null);
        }
        catch (Exception e) {
            // Ignore.
            return null;
        }
    }

    /**
     * Find action methods for a class. 
     *
     * @param aClass the class.
     * 
     * @return an List of sorted action methods, possibly empty.
     */
    private List<Method> getActionMethods(Class<? extends Component> aClass)
    {
        List<Method> result = new ArrayList<Method>();
        for (Method method : aClass.getMethods()) {
            Class<?>[] params = method.getParameterTypes();
            Class<?> returnType = method.getReturnType();
            if (returnType.equals(Void.TYPE) && params.length == 3 &&
                params[0] == AjaxRequestTarget.class &&
                params[1] == Form.class &&
                (params[2] == beanClass || params[2] == Object.class)) {
                result.add(method);
            }
        }
        
        Collections.sort(result, new Comparator<Method>() {
            public int compare(Method o1, Method o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
            
        });
        return result;
    }

    /**
     * Gets the base class name of a Class.
     * 
     * @param aClass the class.
     *
     * @return the base class name (the name without the package name).
     */
    static String getBaseClassName(Class<?> aClass)
    {
        String baseClassName = aClass.getName();
        int idx = baseClassName.lastIndexOf('.');
        if (idx >= 0) {
            baseClassName = baseClassName.substring(idx + 1);
        }

        return baseClassName;
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
            elements.add(prop);
        }
        else {
            prop = findElement(propertyName);
            if (prop == null) {
                throw new RuntimeException("Property: " + propertyName
                                + " does not exist in exposed list of properties.");
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
    
    @SuppressWarnings("unchecked")
    public Class<? extends Panel> getContainerClass()
    {
        String container = getParameter(PARAM_CONTAINER);
        if (container == null) {
            return null;
        }

        try {
            return (Class<? extends Panel>)Class.forName(container);
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot load container class " + container);
        }
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
            if (elem.isAction() && !elem.isActionSpecifiedInProps()) {
                elems.add(elem);
            }
        }

        return elems;
    }

    /**
     * @return the bean class.
     */
    public Class<?> getBeanClass()
    {
        return beanClass;
    }

    /**
     * Gets the external metadata Class supplied to the constructor.
     *
     * @return a Class<?>, or null if not defined.
     */
    public Class<?> getMetaDataClass()
    {
        return metaDataClass;
    }

    /**
     * Gets the beansMetaData.
     *
     * @return a Beans.
     */
    public Beans getBeansMetaData()
    {
        return beansMetaData;
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
     * Adds a property change listener to the bean if it supports it. If it doesn't support
     * addition property change listeners, nothing happens.
     *
     * @param beanModel the bean's IModel.
     * @param listener the {@link PropertyChangeListener}.
     */
    public void addPropertyChangeListener(BeanPropertyModel beanModel, PropertyChangeListener listener)
    {
        if (!hasAddPropertyChangeListenerMethod) {
            return;
        }

        Object bean = beanModel.getBean();
        if (bean != null) {
            try {
                getAddPropertyChangeListenerMethod().invoke(bean, new Object[] { listener });
            }
            catch (Exception e) {
                throw new RuntimeException("Error adding PropertyChangeListener: ", e);
            }
        }
    }

    /**
     * Removes a property change listener to the bean if it supports it. If it doesn't support
     * removal of property change listeners, nothing happens.
     *
     * @param beanModel the bean's IModel.
     * @param listener the {@link PropertyChangeListener}.
     */
    public void removePropertyChangeListener(IModel beanModel, PropertyChangeListener listener)
    {
        if (!hasRemovePropertyChangeListenerMethod) {
            return;
        }

        Object bean = beanModel.getObject();
        if (bean != null) {
            try {
                getRemovePropertyChangeListenerMethod().invoke(bean, new Object[] { listener });
            }
            catch (Exception e) {
                throw new RuntimeException("Error removing PropertyChangeListener: ", e);
            }
        }
    }
    
    /**
     * Applies any metadata-based CSS classes for the given bean or property to the component.
     */
    public void applyCss(Object bean, MetaData metaData, Component applyToComponent)
    {
        String css = metaData.getParameter(PARAM_CSS);
        
        if (!Strings.isEmpty(css)) {
            applyToComponent.add( new AttributeAppender("class", new Model(css), " ") );
        }
        
        String dynamicCssMethod = metaData.getParameter(PARAM_DYNAMIC_CSS);
        if (!Strings.isEmpty(dynamicCssMethod)) {
            try {
                Method method = component.getClass().getMethod(dynamicCssMethod, new Class[] { beanClass, metaData.getClass() } );
                String cssReturn = (String)method.invoke(component, new Object[] { bean, metaData });
                if (!Strings.isEmpty(cssReturn)) {
                    applyToComponent.add( new AttributeAppender("class", new Model(cssReturn), " ") );
                }
            }
            catch (Exception e) {
                throw new RuntimeException("dynamicCss method " + dynamicCssMethod + "(" + beanClass.getName() + ", " + metaData.getClass().getName() + ") is not defined in " + applyToComponent.getClass());
            }            
        }
    }
    
    /**
     * A Cached Beanprops file.
     */
    @SuppressWarnings("serial")
    private static final class CachedBeanProps implements Serializable
    {
        private JBeans beans;
        private long modTimestamp;
        
        CachedBeanProps(JBeans beans, long modTimestamp)
        {
            this.beans = beans;
            this.modTimestamp = modTimestamp;
        }
        
        JBeans getBeans()
        {
            return beans;
        }
        
        long getModTimestamp()
        {
            return modTimestamp;
        }
    }
}
