
package wicket.contrib.webbeans.model;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import wicket.Component;
import wicket.MarkupContainer;
import wicket.Component.IVisitor;
import wicket.contrib.webbeans.fields.BeanGridField;
import wicket.contrib.webbeans.fields.BeanInCollapsibleField;
import wicket.contrib.webbeans.fields.BeanInlineField;
import wicket.contrib.webbeans.fields.BeanTableField;
import wicket.contrib.webbeans.fields.BeanWithParentLabelField;
import wicket.contrib.webbeans.fields.BooleanField;
import wicket.contrib.webbeans.fields.DateTimeField;
import wicket.contrib.webbeans.fields.EmptyField;
import wicket.contrib.webbeans.fields.Field;
import wicket.contrib.webbeans.fields.InputField;
import wicket.contrib.webbeans.fields.JavaEnumField;
import wicket.contrib.webbeans.fields.MultiSelectEnumField;
import wicket.contrib.webbeans.fields.TextAreaField;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.FormComponent;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * Registers field and viewer components. 
 * The components must implement the following constructor, at minimum:<p>
 *    XyzComponent(String wicketId, IModel model, PropertyMetaData metaData) 
 *    
 * When registering an array type, use Object[].class as the type, and specify an element type.
 * 
 * @author Dan Syrstad
 */
public class ComponentRegistry implements Serializable
{
    private Class[] constructorArgs = new Class[] { String.class, IModel.class, ElementMetaData.class, Boolean.TYPE };
    
    // Key is Target Type's class name (e.g., java.util.Date). Value is the
    // Wicket Component (Field) class name. If the mapping contains an element type, the key has a suffix of
    // '[' followed by the element type name.
    private HashMap<String,String> registry;

    /**
     * Construct a ComponentRegistry with the default component mappings. 
     *
     */
    public ComponentRegistry()
    {
        registry = new HashMap<String, String>();

        register(Object.class, BeanInCollapsibleField.class);
        register(String.class, InputField.class);
        register(Boolean.class, BooleanField.class);
        register(Boolean.TYPE, BooleanField.class);
        register(Character.class, InputField.class);
        register(Character.TYPE, InputField.class);
        register(Byte.class, InputField.class);
        register(Byte.TYPE, InputField.class);
        register(Short.class, InputField.class);
        register(Short.TYPE, InputField.class);
        register(Integer.class, InputField.class);
        register(Integer.TYPE, InputField.class);
        register(Long.class, InputField.class);
        register(Long.TYPE, InputField.class);
        register(Float.class, InputField.class);
        register(Float.TYPE, InputField.class);
        register(Double.class, InputField.class);
        register(Double.TYPE, InputField.class);
        register(BigInteger.class, InputField.class);
        register(BigDecimal.class, InputField.class);
        register(Date.class, DateTimeField.class);
        register(java.sql.Date.class, DateTimeField.class);
        register(Time.class, DateTimeField.class);
        register(Timestamp.class, DateTimeField.class);
        register(Calendar.class, DateTimeField.class);
        register(Enum.class, JavaEnumField.class);
        register(Object[].class, Enum.class, MultiSelectEnumField.class);
        register(List.class, Enum.class, MultiSelectEnumField.class);

        register(List.class, BeanTableField.class);
        
        // Register the following so that they're available for findMatchingFieldClass(), but not really available otherwise.
        register(BeanGridField.class, BeanGridField.class);
        register(BeanInlineField.class, BeanInlineField.class);
        register(BeanTableField.class, BeanTableField.class);
        register(BeanWithParentLabelField.class, BeanWithParentLabelField.class);
        register(EmptyField.class, EmptyField.class);
        register(TextAreaField.class, TextAreaField.class);
    }
    
    /**
     * Construct a ComponentRegistry from anotherRegistry. The other registry will not be affected. 
     *
     * @param anotherRegistry
     */
    public ComponentRegistry(ComponentRegistry anotherRegistry)
    {
        registry = (HashMap<String,String>)anotherRegistry.registry.clone();
    }
    
    /**
     * Registers an Field in a type-safe fashion.
     *
     * @param targetType
     * @param fieldComponent
     */
    public void register(Class targetType, Class<? extends Field> fieldComponent)
    {
        register(targetType.getName(), null, fieldComponent.getName());
    }

    /**
     * Registers an Field with an element type in a type-safe fashion.
     *
     * @param targetType
     * @param fieldComponent
     */
    public void register(Class targetType, Class elementType, Class<? extends Field> fieldComponent)
    {
        register(targetType.getName(), elementType.getName(), fieldComponent.getName());
    }

    /**
     * Registers an field in a non-type-safe fashion.
     *
     * @param targetTypeClassName
     * @param elemenTypeName the element type name. May be null.
     * @param fieldComponentClassName
     */
    public void register(String targetTypeClassName, String elemenTypeName, String fieldComponentClassName)
    {
        if (elemenTypeName != null) {
            targetTypeClassName += '[' + elemenTypeName;
        }
        
        registry.put(targetTypeClassName, fieldComponentClassName);
    }
    
    /**
     * Given a shortened version of a Field class name (e.g., TextAreaField), try to find a matching full class name in the
     * registry. The first one found is returned.
     *
     * @param shortName the short class name.
     * 
     * @return the full class name, or null if not found.
     */
    public String findMatchingFieldClass(String shortName)
    {
        String matchStr = '.' + shortName;
        for (String fieldClassName : registry.values()) {
            if (fieldClassName.endsWith(matchStr)) {
                return fieldClassName;
            }
        }
        
        return null;
    }
    
    /**
     * Attempts to find the component class name for a given type and elementType.
     * 
     * @param type
     * @param elementType the element type, which may be null.
     * 
     * @return the class name, or null if not found.
     */
    private String getComponentClassName(Class type, Class elementType)
    {
        String baseKey = type.getName();
        
        for (; elementType != null; elementType = elementType.getSuperclass()) {
            String elementBaseKey = baseKey + '[';
            // Search up class hierarchy for matching type.
            String componentClassName = registry.get(elementBaseKey + elementType.getName());
            if (componentClassName != null) {
                return componentClassName;
            }
            
            Class[] intfs = elementType.getInterfaces();
            for (int i = 0; i < intfs.length; i++) {
                componentClassName = registry.get(elementBaseKey + intfs[i].getName());
                if (componentClassName != null) {
                    return componentClassName;
                }
            }
        }
        
        // If we didn't have an elementType or couldn't find a match using elementType, try 
        // without.
        return registry.get(baseKey);
    }
    
    /**
     * Gets the Component for the given type and context. The component is created with a 
     * BeanPropertyModel, reflecting the given property on the bean. BeanPropertyModel allows
     * access to the underlying bean.
     *
     * @param bean the bean, which may or may not implement IModel.
     * @param wicketId the Wicket component id.
     * @param propertyMeta the PropertyMetaData for the property. 
     * 
     * @return the viewer Component. 
     */
    public Component getComponent(Object bean, String wicketId, ElementMetaData propertyMeta)
    {
        boolean viewOnly = propertyMeta.isViewOnly();
        String componentClassName = propertyMeta.getFieldType();
        if (componentClassName == null) {
            Class type = propertyMeta.getPropertyType();
            // For arrays, treat type as Object[]
            if (type.isArray()) {
                type = Object[].class;
            }
            
            Class elementType = propertyMeta.getElementType(null);
            
            // Work up class hierarchy until we find a more generalized component. Also check interface types.
            for (; type != null && componentClassName == null; type = type.getSuperclass()) {
                componentClassName = getComponentClassName(type, elementType);
                Class[] intfs = type.getInterfaces();
                for (int i = 0; componentClassName == null && i < intfs.length; i++) {
                    componentClassName = getComponentClassName(intfs[i], elementType);
                }
            }
        }
        
        if (componentClassName != null) {
            try {
                Class componentClass = Class.forName(componentClassName);
                Constructor xtor = componentClass.getConstructor(constructorArgs);
                
                IModel model = new BeanPropertyModel(bean, propertyMeta);
                Component component = (Component)xtor.newInstance( new Object[] { wicketId, model, propertyMeta, viewOnly } );
                associateLabelToFormComponents(propertyMeta, component);
                return component;
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Component class not found", e);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException("Component class does not implement constructor (String wicketId, IModel model)", e);
            }
            catch (Exception e) {
                throw new RuntimeException("Error instantiating component " + componentClassName, e);
            }
        }
        
        // The Object.class registry entry should have caught this, but just in case.
        return new Label(wicketId, "<No " + (viewOnly ? "Viewer" : "Field") + " for " + propertyMeta.getPropertyType().getName() + ">");
    }
    
    

    /**
     * Associate label with FormComponents that are descendants of component. 
     *
     * @param propertyMeta
     * @param component
     */
    private void associateLabelToFormComponents(final ElementMetaData propertyMeta, Component component)
    {
        if (component instanceof MarkupContainer) {
            MarkupContainer container = (MarkupContainer)component;
            container.visitChildren(FormComponent.class, new IVisitor() {
                public Object component(Component component) 
                {
                    FormComponent formComponent = (FormComponent)component;
                    formComponent.setLabel( new Model(propertyMeta.getLabel()) );
                    formComponent.add( new ErrorHighlightingBehavior() );
                    return IVisitor.CONTINUE_TRAVERSAL;
                }                
            });
        }
    }
}
