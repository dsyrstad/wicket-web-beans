package net.sourceforge.wicketwebbeans.examples.fileupload;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;


public class FileUploaderPage extends WebPage
{
    public FileUploaderPage()
    {
        TestBean bean = new TestBean();
        ComponentConfig meta = new ComponentConfig(bean.getClass(), null, this, null);
        add( new BeanForm("beanForm", bean, meta) );
    }

    public void save(AjaxRequestTarget target, Form form, TestBean bean)
    {
        info("Saved");
    }
}
