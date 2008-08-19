package net.sourceforge.wicketwebbeans.examples.nested;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;

import org.apache.wicket.markup.html.WebPage;

public class NestedBeanPage extends WebPage
{
    public NestedBeanPage()
    {
        Customer bean = new Customer();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, null);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
