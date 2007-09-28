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

import wicket.Component;
import wicket.contrib.webbeans.containers.BeanForm;
import wicket.model.PropertyModel;
import wicket.util.string.Strings;


/**
 * An extension of PropertyModel so that we can get the backing bean and check for
 * modifications. We only set the bean property if the value from the form has changed with respect
 * to the value that was retrieved from the model when the form was rendered. 
 * This is important because setting one property on a bean via this model may cause other properties to be
 * set indirectly. Wicket dumps the whole form back everytime and we do not want to wipe out those
 * properties that were indirectly set.<p>
 * 
 * @author Dan Syrstad
 */
public class BeanPropertyModel extends PropertyModel
{
    private ElementMetaData elementMetaData;
    // This value is tracked from onGetObject to see if the value changes. The assumption is that
    // Wicket calls getObject() when populating the HTML form on the way out, so this is the last value
    // that Wicket got. It is then used for comparison purposes when setObject() is called to see
    // if the value has changed. 
    private Object lastValueGot = null;
    // This flag tracks whether lastValueGot is valid.
    private boolean getObjectCalled = false;

    /**
     * Construct a BeanPropertyModel. 
     *
     * @param modelObject
     * @param expression
     * @param propertyType
     */
    public BeanPropertyModel(Object modelObject, ElementMetaData elementMetaData)
    {
        super(modelObject, elementMetaData.getPropertyName(), elementMetaData.getPropertyType());
        this.elementMetaData = elementMetaData;
    }
    
    /**
     * Gets the bean from which the property will be accessed.
     *
     * @return the bean.
     */
    public Object getBean()
    {
        return modelObject(null);
    }
    
    public ElementMetaData getElementMetaData()
    {
        return elementMetaData;
    }

    /** 
     * {@inheritDoc}
     * @see wicket.model.AbstractPropertyModel#onGetObject(wicket.Component)
     */
    @Override
    protected Object onGetObject(Component someComponent)
    {
        Object value = super.onGetObject(someComponent);
        if (!BeanForm.isInSubmit(someComponent)) {
            // Only set these if we're not in submit processing.
            lastValueGot = value;
            getObjectCalled = true;
        }
        
        return value;
    }

    /**
     * {@inheritDoc}
     * Only sets the object if it is different from what getObject() returns. 
     * 
     * @see wicket.model.AbstractPropertyModel#onSetObject(wicket.Component, java.lang.Object)
     */
    @Override
    protected void onSetObject(Component component, Object object)
    {
        Object newValue = object;
        // This, unfortunately, comes in as a String in most cases, so convert it.
        if (newValue instanceof String)
        {
            final String string = (String)newValue;
            if (!Strings.isEmpty(string))
            {
                // and there is a non-null property type for the component
                final Class propertyType = propertyType(component);
                if (propertyType != null)
                {
                    // convert the String to the right type
                    newValue = component.getConverter().convert(string, propertyType);
                }
            }
        }

        // Below in parens is an equality expression that is inverted to say "not (equal)".
        if ( !(getObjectCalled                        // If lastValueGot is valid.
               && lastValueGot == newValue            // If they're the same object, or both null
               && (lastValueGot == null || lastValueGot.equals(newValue))) ) {
            super.onSetObject(component, object);
        }
        
        getObjectCalled = false;
    }

}
