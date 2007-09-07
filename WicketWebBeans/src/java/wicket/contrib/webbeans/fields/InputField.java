/*
 * 
 */
package wicket.contrib.webbeans.fields;

import wicket.behavior.SimpleAttributeModifier;
import wicket.contrib.webbeans.model.ElementMetaData;
import wicket.markup.html.form.TextField;
import wicket.markup.html.panel.Fragment;
import wicket.model.IModel;


/**
 * A Field that allows entry of text, numbers, etc. depending on the property type.
 * Accepts a config of "advOnEnter={true|false}", which means: advance to the next field when the
 * Enter key is pressed. This is useful for barcode scanners. 
 * 
 * @author Dan Syrstad
 */
public class InputField extends AbstractField
{
    /**
     * Construct a new InputField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     * @param viewOnly true if the component should be view-only.
     */
    public InputField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly);

        boolean advOnEnter = metaData.getBooleanParameter("advOnEnter");
        
        Fragment fragment;
        if (viewOnly) {
            fragment = new Fragment("frag", "viewer");
            fragment.add( new LabelWithMinSize("component", model) );
        }
        else {
            fragment = new Fragment("frag", "editor");

            TextField field = new TextField("component", model, metaData.getPropertyType());
            if (advOnEnter) {
                field.add( new SimpleAttributeModifier("onkeypress", "return inputField_HandleEnter(this, event)") );
            }
            
            fragment.add(field);
        }
        
        add(fragment);
    }
}
