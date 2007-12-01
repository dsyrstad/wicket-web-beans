package net.sourceforge.wicketwebbeans.examples.container;

import net.sourceforge.wicketwebbeans.annotations.Bean;
import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.containers.VerticalLayoutBeanPanel;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import wicket.markup.html.WebPage;

@Bean(type = TestBean.class, container = VerticalLayoutBeanPanel.class)
public class CustomContainerPage extends WebPage
{
    public CustomContainerPage()
    {
        TestBean bean = new TestBean();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
