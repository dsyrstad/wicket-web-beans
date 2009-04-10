package com.googlecode.wicketwebbeans.examples.enums;


import org.apache.wicket.markup.html.WebPage;

import com.googlecode.wicketwebbeans.containers.BeanForm;
import com.googlecode.wicketwebbeans.model.BeanMetaData;

public class EnumBeanPage extends WebPage
{
    public EnumBeanPage()
    {
        Customer bean = new Customer();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
