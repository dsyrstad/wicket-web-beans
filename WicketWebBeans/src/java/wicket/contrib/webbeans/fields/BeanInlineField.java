/*
 * 
 */
package wicket.contrib.webbeans.fields;

import wicket.contrib.webbeans.containers.BeanGridPanel;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.contrib.webbeans.model.ElementMetaData;
import wicket.model.IModel;


/**
 * A Field that presents a property's bean inline with other fields.
 *
 * @author Dan Syrstad
 */
public class BeanInlineField extends AbstractField implements UnlabeledField
{
    /**
     * Construct a new BeanGridField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     * @param viewOnly true if the component should be view-only.
     */
    public BeanInlineField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly);
        
        BeanMetaData beanMetaData = metaData.createBeanMetaData(viewOnly);
        
        if (!viewOnly && model.getObject(this) == null) {
            // Create a blank instance for editing.
            model.setObject(this, metaData.createInstance() );
        }

        add( new BeanGridPanel("beanPanel", model, beanMetaData) );
    }
}
