/*
 * 
 */
package wicket.contrib.webbeans.fields;

import wicket.contrib.webbeans.model.ElementMetaData;
import wicket.markup.html.form.CheckBox;
import wicket.model.IModel;


/**
 * A boolean editor component (a check box).
 * 
 * @author Dan Syrstad
 */
public class BooleanField extends AbstractField
{
    /**
     * Construct a new BooleanField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     * @param viewOnly true if the component should be view-only.
     */
    public BooleanField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly);

        CheckBox checkBox =  new CheckBox("component", model) ;
        checkBox.setEnabled(!viewOnly);
        add(checkBox);
    }
}
