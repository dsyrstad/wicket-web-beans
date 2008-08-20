package net.sourceforge.wicketwebbeans.examples.enums;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;

import org.apache.wicket.markup.html.WebPage;

public class EnumBeanPage extends WebPage
{
    public EnumBeanPage()
    {
        Customer bean = new Customer();
        ComponentConfig meta = new ComponentConfig(bean.getClass(), null, this, null);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
