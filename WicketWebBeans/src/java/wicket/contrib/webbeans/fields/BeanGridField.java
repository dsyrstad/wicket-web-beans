/*
 * 
 */
package wicket.contrib.webbeans.fields;

import wicket.contrib.webbeans.containers.BeanGridPanel;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.contrib.webbeans.model.ElementMetaData;
import wicket.model.IModel;


/**
 * A Field that presents a property's bean in a BeanGridPanel. 
 *
 * @author Dan Syrstad
 */
public class BeanGridField extends AbstractField
{
    /**
     * Construct a new BeanGridField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     * @param viewOnly true if the component should be view-only.
     */
    public BeanGridField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly);
        
        BeanMetaData beanMetaData = metaData.createBeanMetaData(viewOnly);
        
        if (!viewOnly && model.getObject(this) == null) {
            // Create a blank instance for editing.
            model.setObject(this, metaData.createInstance() );
        }

        add( new BeanGridPanel("p", model, beanMetaData) );
    }
}
