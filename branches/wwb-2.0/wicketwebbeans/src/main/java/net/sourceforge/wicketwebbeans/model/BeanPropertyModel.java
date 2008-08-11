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

import net.sourceforge.wicketwebbeans.containers.BeanForm;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;
import org.apache.wicket.model.PropertyModel;



/**
 * An extension of PropertyModel so that we can get the backing bean and check for
 * modifications. We only set the bean property if it has changed. 
 * This is important because setting one property on a bean via this model may cause other properties to be
 * set indirectly. Wicket dumps the whole form back every time and we do not want to wipe out those
 * properties that were indirectly set.<p>
 * 
 * @author Dan Syrstad
 */
public class BeanPropertyModel extends PropertyModel implements IComponentAssignedModel, IWrapModel
{
    private static final long serialVersionUID = 5558211992175238860L;
    
    private ElementMetaData elementMetaData;
    // If this model is registered with a BeanForm, this is it.
    private BeanForm beanForm = null;
    private Component component = null;
    
    private transient boolean attached = false;
    
    /**
     * Construct a BeanPropertyModel. 
     *
     * @param modelObject
     * @param expression
     * @param propertyType
     */
    public BeanPropertyModel(Object modelObject, ElementMetaData elementMetaData)
    {
        super(modelObject, elementMetaData.getPropertyName());
        this.elementMetaData = elementMetaData;
    }
    
    public IWrapModel wrapOnAssignment(Component component)
    {
        this.component = component;
        return this;
    }
    
    public IModel getWrappedModel()
    {
        return this;
    }
    
    /**
     * Gets the bean from which the property will be accessed.
     *
     * @return the bean.
     */
    public Object getBean()
    {
        return getTarget();
    }
    
    public ElementMetaData getElementMetaData()
    {
        return elementMetaData;
    }

    /** 
     * {@inheritDoc}
     * @see org.apache.wicket.model.AbstractPropertyModel#onGetObject(wicket.Component)
     */
    @Override
    public Object getObject()
    {
        attach();
        return super.getObject();
    }

    /**
     * {@inheritDoc}
     * Only sets the object if it is different from what getObject() returns. 
     * 
     * @see org.apache.wicket.model.AbstractPropertyModel#onSetObject(wicket.Component, java.lang.Object)
     */
    @Override
    public void setObject(Object object)
    {
        attach();
        if (beanForm != null && component instanceof FormComponent) {
            FormComponent formComponent = (FormComponent)component;
            String fieldName = beanForm.getSubmitFieldName();
            if (fieldName != null && !formComponent.getInputName().equals(fieldName)) {
                // Not the field being set - don't set it.
                return;
            }
            System.out.println("Setting " + fieldName);
        }

        super.setObject(object);
    }

    public BeanForm getBeanForm()
    {
        return beanForm;
    }

    public void setBeanForm(BeanForm beanForm)
    {
        this.beanForm = beanForm;
    }

    private void attach()
    {
        if ( ! attached ) {
            attached = true;
            if (beanForm != null) {
                // Re-register listener when the bean is being re-attached.
                elementMetaData.getBeanMetaData().addPropertyChangeListener(this, beanForm.getListener());
            }
        }
    }

    /** 
     * {@inheritDoc}
     * @see org.apache.wicket.model.AbstractPropertyModel#detach()
     */
    @Override
    public void detach()
    {
        super.detach();
        attached = false;
    }
}
