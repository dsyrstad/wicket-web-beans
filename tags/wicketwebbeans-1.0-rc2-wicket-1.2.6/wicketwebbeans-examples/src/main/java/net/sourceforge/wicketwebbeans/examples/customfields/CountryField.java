package net.sourceforge.wicketwebbeans.examples.customfields;

import net.sourceforge.wicketwebbeans.fields.EnumField;
import net.sourceforge.wicketwebbeans.model.ElementMetaData;
import wicket.model.IModel;

public class CountryField extends EnumField
{
    public CountryField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly, Country.values());
    }
}
