/*
 * 
 */
package wicket.contrib.webbeans.fields;

import wicket.contrib.webbeans.model.ElementMetaData;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;


/**
 * Stub Field that displays nothing. Can be used to keep a layout cell blank.
 *
 * @author Dan Syrstad
 */
public class EmptyField extends Panel implements Field
{
    /**
     * Construct a new EmptyField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     * @param viewOnly true if the component should be view-only.
     */
    public EmptyField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id);
        setRenderBodyOnly(true);
    }
}
