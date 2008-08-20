package net.sourceforge.wicketwebbeans.examples.container;

import net.sourceforge.wicketwebbeans.annotations.Bean;
import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.containers.VerticalLayoutBeanPanel;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;

import org.apache.wicket.markup.html.WebPage;


@Bean(type = TestBean.class, container = VerticalLayoutBeanPanel.class)
public class CustomContainerPage extends WebPage
{
    public CustomContainerPage()
    {
        TestBean bean = new TestBean();
        ComponentConfig meta = new ComponentConfig(bean.getClass(), null, this, null);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
