package wicket.contrib.webbeans.examples.contexts;

import java.math.BigDecimal;

import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.examples.simple.TestBean;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.markup.html.WebPage;

public class ContextBeanPage extends WebPage
{
    public ContextBeanPage()
    {
        TestBean bean = new TestBean();
        bean.setFirstName("Dan");
        bean.setLastName("Syrstad");
        bean.setOperand1(BigDecimal.valueOf(123.0));
        bean.setOperand2(BigDecimal.valueOf(456.0));
        
        BeanMetaData meta = new BeanMetaData(bean.getClass(), "view", this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
