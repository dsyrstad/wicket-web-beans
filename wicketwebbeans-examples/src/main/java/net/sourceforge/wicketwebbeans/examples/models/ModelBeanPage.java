package net.sourceforge.wicketwebbeans.examples.models;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.examples.LoadableDetachableObjectModel;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;

public class ModelBeanPage extends WebPage
{
    public ModelBeanPage()
    {
        IModel beanModel = new LoadableDetachableObjectModel();
        
        ComponentConfig meta = new ComponentConfig(beanModel.getObject().getClass(), null, this, null);
        add( new BeanForm("beanForm", beanModel, meta) );
    }
}
