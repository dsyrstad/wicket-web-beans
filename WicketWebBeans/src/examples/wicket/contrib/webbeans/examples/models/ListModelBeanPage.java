package wicket.contrib.webbeans.examples.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.examples.SerializableBean;
import wicket.contrib.webbeans.model.BeanMetaData;
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
