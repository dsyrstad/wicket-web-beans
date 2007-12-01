package net.sourceforge.wicketwebbeans.examples.models;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.examples.LoadableDetachableObjectModel;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import wicket.markup.html.WebPage;
import wicket.model.IModel;

public class ModelBeanPage extends WebPage
{
    public ModelBeanPage()
    {
        IModel beanModel = new LoadableDetachableObjectModel();
        
        BeanMetaData meta = new BeanMetaData(beanModel.getObject(this).getClass(), null, this, null, false);
        add( new BeanForm("beanForm", beanModel, meta) );
    }
}
