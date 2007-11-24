package wicket.contrib.webbeans.databinder.examples;

import wicket.contrib.webbeans.fields.DropDownChoiceField;
import wicket.contrib.webbeans.model.ElementMetaData;

import org.apache.wicket.model.IModel;
import net.databinder.models.*;
import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class CategoryField extends DropDownChoiceField
{
    public CategoryField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly, new HibernateListModel("from Category order by name"), new ChoiceRenderer("name"));
    }
}
