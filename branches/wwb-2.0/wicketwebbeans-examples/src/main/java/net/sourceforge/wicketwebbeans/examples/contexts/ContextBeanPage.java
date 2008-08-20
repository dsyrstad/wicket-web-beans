package net.sourceforge.wicketwebbeans.examples.contexts;

import java.math.BigDecimal;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.examples.simple.TestBean;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;

import org.apache.wicket.markup.html.WebPage;

public class ContextBeanPage extends WebPage
{
    public ContextBeanPage()
    {
        TestBean bean = new TestBean();
        bean.setFirstName("Dan");
        bean.setLastName("Syrstad");
        bean.setOperand1(BigDecimal.valueOf(123.0));
        bean.setOperand2(BigDecimal.valueOf(456.0));
        
        ComponentConfig meta = new ComponentConfig(bean.getClass(), "limitedEdit", this, null);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
