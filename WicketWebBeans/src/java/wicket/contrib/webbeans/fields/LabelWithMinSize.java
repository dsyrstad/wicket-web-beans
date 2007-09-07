package wicket.contrib.webbeans.fields;


import wicket.markup.ComponentTag;
import wicket.markup.MarkupStream;
import wicket.markup.html.basic.Label;
import wicket.model.IModel;
import wicket.util.string.Strings;


/**
 * A Label that has a minimum size if it's model is null.
 * 
 * @author Dan Syrstad
 */
public class LabelWithMinSize extends Label
{
    /**
     * Construct a new LabelWithMinSize.
     *
     * @param id
     */
    public LabelWithMinSize(String id)
    {
        super(id);
    }

    /**
     * Construct a new LabelWithMinSize.
     *
     * @param id
     * @param label
     */
    public LabelWithMinSize(String id, String label)
    {
        super(id, label);
    }

    /**
     * Construct a new LabelWithMinSize.
     *
     * @param id
     * @param model
     */
    public LabelWithMinSize(String id, IModel model)
    {
        super(id, model);
    }

    /**
     * {@inheritDoc}
     * @see wicket.markup.html.basic.Label#onComponentTagBody(wicket.markup.MarkupStream, wicket.markup.ComponentTag)
     */
    @Override
    protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
    {
        String value = getModelObjectAsString();
        if (Strings.isEmpty(value)) {
            value = "&nbsp;";
            setEscapeModelStrings(false);
        }

        replaceComponentTagBody(markupStream, openTag, value);
    }

}
