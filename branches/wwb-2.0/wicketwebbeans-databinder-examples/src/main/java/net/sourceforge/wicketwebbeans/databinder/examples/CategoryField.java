package net.sourceforge.wicketwebbeans.databinder.examples;


import org.apache.wicket.model.IModel;
import net.databinder.models.hib.*;
import net.sourceforge.wicketwebbeans.fields.DropDownChoiceField;
import net.sourceforge.wicketwebbeans.model.ElementMetaData;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class CategoryField extends DropDownChoiceField
{
    public CategoryField(String id, IModel model, ElementMetaData metaData)
    {
        super(id, model, metaData, new HibernateListModel("from Category order by name"), new ChoiceRenderer("name"));
    }
}
