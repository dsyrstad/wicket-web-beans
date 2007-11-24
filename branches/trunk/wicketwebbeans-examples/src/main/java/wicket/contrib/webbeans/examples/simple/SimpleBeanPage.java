package wicket.contrib.webbeans.examples.simple;

import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.model.BeanMetaData;
import org.apache.wicket.markup.html.WebPage;

public class SimpleBeanPage extends WebPage
{
    public SimpleBeanPage()
    {
        TestBean bean = new TestBean();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
