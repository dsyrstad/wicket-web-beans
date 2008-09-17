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

import java.io.Serializable;
import java.util.HashMap;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;

/**
 * Registers components for corresponding types. 
 * The components must implement the following constructor, at minimum:<p>
 *    XyzComponent(String wicketId) 
 *    
 * When registering an array type, use Object[].class as the type, and specify an element type.
 * 
 * @author Dan Syrstad
 */
public class ComponentRegistry implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Key is Target Type's class name (e.g., java.util.Date). Value is the
    // Wicket Component (Field) class name. If the mapping contains an element type, the key has a suffix of
    // '[' followed by the element type name.
    private HashMap<String, String> registry;

    /**
     * Construct a ComponentRegistry with the default component mappings. 
     */
    public ComponentRegistry()
    {
        registry = new HashMap<String, String>();
        register(String.class, TextField.class);
        /*
                register(Object.class, BeanGridField.class);
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

                register(Collection.class, BeanTableField.class);

                // Register the following so that they're available for findMatchingFieldClass(), but not really available otherwise.
                register(BeanGridField.class, BeanGridField.class);
                register(BeanInCollapsibleField.class, BeanInCollapsibleField.class);
                register(BeanInlineField.class, BeanInlineField.class);
                register(BeanTableField.class, BeanTableField.class);
                register(BeanWithParentLabelField.class, BeanWithParentLabelField.class);
                register(EmptyField.class, EmptyField.class);
                register(TextAreaField.class, TextAreaField.class);
                register(PasswordField.class, PasswordField.class);
                */
    }

    /**
     * Construct a ComponentRegistry from anotherRegistry. The other registry will not be affected. 
     *
     * @param anotherRegistry
     */
    @SuppressWarnings("unchecked")
    public ComponentRegistry(ComponentRegistry anotherRegistry)
    {
        registry = (HashMap<String, String>)anotherRegistry.registry.clone();
    }

    /**
     * Registers an component in a non-type-safe fashion.
     *
     * @param targetTypeClassName
     * @param elemenTypeName the element type name. May be null.
     * @param componentClassName
     */
    public void register(String targetTypeClassName, String elemenTypeName, String componentClassName)
    {
        if (elemenTypeName != null) {
            targetTypeClassName += '[' + elemenTypeName;
        }

        registry.put(targetTypeClassName, componentClassName);
    }

    public void register(Class<?> targetType, Class<? extends Component> componentClass)
    {
        registry.put(targetType.getClass().getName(), componentClass.getClass().getName());
    }

    /**
     * Attempts to find the component class name for a given type and elementType.
     * 
     * @param type
     * @param elementType the element type (e.g., for an array or collection), which may be null.
     * 
     * @return the class, or null if not found.
     */
    // TODO Test. Entire Class.
    @SuppressWarnings("unchecked")
    public Class<? extends Component> getComponentClass(Class<?> type, Class<?> elementType)
    {
        String baseKey = type.getName();

        String className = null;
        for (; elementType != null && className == null; elementType = elementType.getSuperclass()) {
            String elementBaseKey = baseKey + '[';
            // Search up class hierarchy for matching type.
            String componentClassName = registry.get(elementBaseKey + elementType.getName());
            if (componentClassName != null) {
                className = componentClassName;
                break;
            }

            Class<?>[] intfs = elementType.getInterfaces();
            for (int i = 0; i < intfs.length; i++) {
                componentClassName = registry.get(elementBaseKey + intfs[i].getName());
                if (componentClassName != null) {
                    className = componentClassName;
                    break;
                }
            }
        }

        if (className == null) {
            className = registry.get(baseKey);
        }

        if (className != null) {
            try {
                return (Class<? extends Component>)Class.forName(className);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}
