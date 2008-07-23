package net.sourceforge.wicketwebbeans.examples.fileupload;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;


public class FileUploaderPage extends WebPage
{
    public FileUploaderPage()
    {
        TestBean bean = new TestBean();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }

    public void save(AjaxRequestTarget target, Form form, TestBean bean)
    {
        info("Saved");
    }
}
