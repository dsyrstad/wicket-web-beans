package wicket.contrib.webbeans.examples.enums;

import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.model.BeanMetaData;
import org.apache.wicket.markup.html.WebPage;

public class EnumBeanPage extends WebPage
{
    public EnumBeanPage()
    {
        Customer bean = new Customer();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
