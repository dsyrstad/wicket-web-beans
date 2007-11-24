package wicket.contrib.webbeans.examples.container;

import org.apache.wicket.markup.html.WebPage;

import wicket.contrib.webbeans.annotations.Bean;
import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.containers.VerticalLayoutBeanPanel;
import wicket.contrib.webbeans.model.BeanMetaData;

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
