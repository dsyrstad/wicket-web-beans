/*
 * 
 */
package wicket.contrib.webbeans.fields;

import java.util.Arrays;

import wicket.contrib.webbeans.model.ElementMetaData;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.panel.Fragment;
import wicket.model.IModel;


/**
 * A field for Java Enum types. Presents the values as a drop-down list.
 * 
 * @author Dan Syrstad
 */
public class JavaEnumField extends EnumField
{
    /**
     * Construct a new EnumField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     * @param viewOnly true if the component should be view-only.
     */
    public JavaEnumField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly, Arrays.asList(metaData.getPropertyType().getEnumConstants()));
    }
}
