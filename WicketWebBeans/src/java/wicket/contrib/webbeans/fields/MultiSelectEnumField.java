/*
 * 
 */
package wicket.contrib.webbeans.fields;

import java.util.Arrays;

import wicket.contrib.webbeans.model.ElementMetaData;
import wicket.markup.html.form.ListMultipleChoice;
import wicket.model.IModel;


/**
 * A Field for a selecting zero or more elements from collection of Java Enum types. Presents the values as a list.
 * 
 * @author Dan Syrstad
 */
public class MultiSelectEnumField extends AbstractField
{
    /**
     * Construct a new MultiSelectEnumField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     * @param viewOnly true if the component should be view-only.
     */
    public MultiSelectEnumField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly);
        
        final Class elementType = metaData.getElementType(null);
        if (elementType == null) {
            throw new RuntimeException("Cannot find elementType for property " + metaData.getPropertyName());
        }
        
        Object[] enumValues = elementType.getEnumConstants();

        ListMultipleChoice multChoice = new ListMultipleChoice("component", model, Arrays.asList(enumValues));
        multChoice.setEnabled(!viewOnly);
        
        add(multChoice);
    }
}
