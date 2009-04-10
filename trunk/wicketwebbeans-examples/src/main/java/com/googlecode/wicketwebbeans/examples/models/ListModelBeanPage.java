package com.googlecode.wicketwebbeans.examples.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicketwebbeans.containers.BeanForm;
import com.googlecode.wicketwebbeans.examples.SerializableBean;
import com.googlecode.wicketwebbeans.model.BeanMetaData;

public class ListModelBeanPage extends WebPage
{
    public ListModelBeanPage()
    {
        SerializableBean[] beans = new SerializableBean[20];
        for (int i = 0; i < beans.length; i++) {
            beans[i] = new SerializableBean("Name" + i, "XYZ" + i);
        }
        
        IModel beanModel = new Model((Serializable)(Object)Arrays.asList(beans));
        
        BeanMetaData meta = new BeanMetaData(SerializableBean.class, null, this, null, false);
        add( new BeanForm("beanForm", beanModel, meta) );
    }
}
