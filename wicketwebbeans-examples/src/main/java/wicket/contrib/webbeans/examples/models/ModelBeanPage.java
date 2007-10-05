package wicket.contrib.webbeans.examples.models;

import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.examples.LoadableDetachableObjectModel;
import wicket.contrib.webbeans.model.BeanMetaData;
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
