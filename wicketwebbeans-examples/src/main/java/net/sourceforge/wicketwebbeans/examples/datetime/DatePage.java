package net.sourceforge.wicketwebbeans.examples.datetime;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import wicket.markup.html.WebPage;

public class DatePage extends WebPage
{
    public DatePage()
    {
        TestBean bean = new TestBean();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
