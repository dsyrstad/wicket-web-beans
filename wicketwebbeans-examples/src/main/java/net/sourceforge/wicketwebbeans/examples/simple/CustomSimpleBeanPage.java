package net.sourceforge.wicketwebbeans.examples.simple;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;

import org.apache.wicket.markup.html.WebPage;

public class CustomSimpleBeanPage extends WebPage
{
    public CustomSimpleBeanPage()
    {
        TestBean bean = new TestBean();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, null);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
