package wicket.contrib.webbeans.examples.customfields;

import wicket.contrib.webbeans.fields.EnumField;
import wicket.contrib.webbeans.model.ElementMetaData;
import org.apache.wicket.model.IModel;

public class CountryField extends EnumField
{
    public CountryField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly, Country.values());
    }
}
