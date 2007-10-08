package wicket.contrib.webbeans.examples.models;

import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.examples.LoadableDetachableObjectModel;
import wicket.contrib.webbeans.model.BeanMetaData;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;

public class ModelBeanPage extends WebPage
{
    public ModelBeanPage()
    {
        IModel beanModel = new LoadableDetachableObjectModel();
        
        BeanMetaData meta = new BeanMetaData(beanModel.getObject().getClass(), null, this, null, false);
        add( new BeanForm("beanForm", beanModel, meta) );
    }
}
