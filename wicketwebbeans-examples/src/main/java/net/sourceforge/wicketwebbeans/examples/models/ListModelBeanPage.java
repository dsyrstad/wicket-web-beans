package net.sourceforge.wicketwebbeans.examples.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.examples.SerializableBean;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;

import wicket.markup.html.WebPage;
import wicket.model.IModel;
import wicket.model.Model;

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
