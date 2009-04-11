package com.googlecode.wicketwebbeans.examples.customfields;


import org.apache.wicket.model.IModel;

import com.googlecode.wicketwebbeans.fields.EnumField;
import com.googlecode.wicketwebbeans.model.ElementMetaData;

public class CountryField extends EnumField
{
    public CountryField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly, Country.values());
    }
}
