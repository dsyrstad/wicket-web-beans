/*
 * 
 */
package wicket.contrib.webbeans.fields;

import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.model.BeanPropertyModel;
import wicket.contrib.webbeans.model.ElementMetaData;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;

import java.util.Properties;


/**
 * Base class for Fields. In general, Fields should <em>not</em> set renderBodyOnly to true
 * if they want to be refreshed via Ajax.
 *
 * @author Dan Syrstad
 */
abstract public class AbstractField extends Panel implements Field
{
    private static final long serialVersionUID = -5452855853289381110L;
    
    private ElementMetaData elementMetaData;

    /**
     * Construct a AbstractField. 
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     * @param viewOnly true if the component should be view-only.
     */
    public AbstractField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model);
        this.elementMetaData = metaData;
        
        // Allow for refreshing of the field via Ajax.
        setOutputMarkupId(true);
        setRenderBodyOnly(false);
    }
    
    /**
     * {@inheritDoc}
     * @see wicket.Component#onAttach()
     */
    @Override
    protected void onAttach()
    {
        super.onAttach();
        // If we're part of a BeanForm, register ourself with it.
        BeanForm beanForm  = (BeanForm)findParent(BeanForm.class);
        if (beanForm != null) {
            Object bean = null;
            if (getModel() instanceof BeanPropertyModel) {
                bean = ((BeanPropertyModel)getModel()).getBean();
            }
            
            beanForm.registerComponent(this, bean, elementMetaData);
        }
    }

    /**
     * Gets the elementMetaData.
     *
     * @return a ElementMetaData.
     */
    public ElementMetaData getElementMetaData()
    {
        return elementMetaData;
    }

    /**
     * Returns the ElementMetaData related to the specified propertyName.
     * @param metaData metaData for this field.
     * @param propertyName the propertyName to lookup
     * @param propertyClass the class type the property should resolve to
     * @return ElementMetaData
     */
    protected ElementMetaData getDependentProperty(ElementMetaData metaData, String propertyName, Class propertyClass) {
        Properties config = metaData.getParameters();
        ElementMetaData property = null;
        String propStr = config.getProperty(propertyName);
        if (propStr != null) {
            property = metaData.getBeanMetaData().findElement(propStr);
            if (property == null) {
                throw new RuntimeException("'" + propStr + "' is not defined on " + metaData.getBeanMetaData().getBeanClass());
            }

            if (!propertyClass.isAssignableFrom( property.getPropertyType() )) {
                throw new RuntimeException(propertyName + "'" + propStr + "' must return a " + propertyClass.getName() + " on "
                                + metaData.getBeanMetaData().getBeanClass() + ". Instead it returns "
                                + property.getPropertyType());
            }
        }
        return property;
    }

    /**
     * Gets the dependent property bean from the specified property.
     *
     * @return the dependent property bean, or null if not defined.
     * @param property the ElementMetaData property to extract the bean from
     */
    protected Object getDependentPropertyBean(ElementMetaData property)
    {
        if (property != null) {
            BeanPropertyModel model = (BeanPropertyModel)getModel();
            return property.getPropertyValue( model.getBean() );
        }

        return null;
    }
}
