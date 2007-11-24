package wicket.contrib.webbeans.examples.actions;

import org.apache.wicket.ajax.AjaxRequestTarget;
import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.examples.simple.TestBean;
import wicket.contrib.webbeans.model.BeanMetaData;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

public class ActionBeanPage extends WebPage
{
    public ActionBeanPage()
    {
        TestBean bean = new TestBean();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }

    public void save(AjaxRequestTarget target, Form form, TestBean bean)
    {
        info("Saved - thank you");
    }

    public void cancel(AjaxRequestTarget target, Form form, TestBean bean)
    {
        info("Canceled - thank you");
    }

    public void clearLastName(AjaxRequestTarget target, Form form, TestBean bean)
    {
        bean.setLastName("");
    }
}
