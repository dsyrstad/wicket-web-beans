/*
 * 
 */
package wicket.contrib.webbeans.fields;

import wicket.contrib.webbeans.model.ElementMetaData;
import wicket.markup.ComponentTag;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.form.TextField;
import wicket.model.IModel;


/**
 * A text area Field. Accepts parameters for "rows" and/or "cols".
 * 
 * @author Dan Syrstad
 */
public class TextAreaField extends AbstractField
{
    /**
     * Construct a new TextAreaField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     * @param viewOnly true if the component should be view-only.
     */
    public TextAreaField(String id, IModel model, final ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly);

        metaData.consumeParameter("rows");
        metaData.consumeParameter("cols");
        TextArea field = new TextArea("component", model) {
            @Override
            protected void onComponentTag(ComponentTag tag)
            {
                super.onComponentTag(tag);
                String rows = metaData.getParameter("rows");
                if (rows != null) {
                    tag.put("rows", rows);
                }

                String cols = metaData.getParameter("cols");
                if (cols != null) {
                    tag.put("cols", cols);
                }
            }
        };
        
        field.setEnabled(!viewOnly);

        add(field);
    }
}
