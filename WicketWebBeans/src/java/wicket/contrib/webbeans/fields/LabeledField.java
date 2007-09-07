/*
 * 
 */
package wicket.contrib.webbeans.fields;

import wicket.Component;
import wicket.markup.html.panel.Panel;


/**
 * A field that encloses a label and a field.
 * 
 * @author Dan Syrstad
 */
public class LabeledField extends Panel
{
    /**
     * Construct a LabeledField. 
     *
     * @param id the wicket id.
     * @param label the label component. Must have an id of "l".
     * @param field the field component. Must have an id of "f".
     */
    public LabeledField(String id, Component label, Component field)
    {
        super(id);

        add(label);
        add(field);
    }
}
