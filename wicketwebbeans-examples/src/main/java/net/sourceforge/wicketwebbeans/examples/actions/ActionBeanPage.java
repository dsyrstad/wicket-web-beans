package net.sourceforge.wicketwebbeans.examples.actions;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.examples.simple.TestBean;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import wicket.ajax.AjaxRequestTarget;
import wicket.markup.html.WebPage;
import wicket.markup.html.form.Form;

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
