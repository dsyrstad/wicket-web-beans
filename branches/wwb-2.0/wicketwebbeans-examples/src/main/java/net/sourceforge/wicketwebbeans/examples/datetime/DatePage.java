package net.sourceforge.wicketwebbeans.examples.datetime;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;

import org.apache.wicket.markup.html.WebPage;

public class DatePage extends WebPage
{
    public DatePage()
    {
        TestBean bean = new TestBean();
        ComponentConfig meta = new ComponentConfig(bean.getClass(), null, this, null);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
