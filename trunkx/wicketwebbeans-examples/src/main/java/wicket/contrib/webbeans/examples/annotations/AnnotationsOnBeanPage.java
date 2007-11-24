package wicket.contrib.webbeans.examples.annotations;

import wicket.ajax.AjaxRequestTarget;
import wicket.contrib.webbeans.annotations.Action;
import wicket.contrib.webbeans.annotations.Bean;
import wicket.contrib.webbeans.annotations.Beans;
import wicket.contrib.webbeans.annotations.Property;
import wicket.contrib.webbeans.annotations.Tab;
import static wicket.contrib.webbeans.annotations.Property.EMPTY;
import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.markup.html.WebPage;
import wicket.markup.html.form.Form;

public class AnnotationsOnBeanPage extends WebPage
{
    public AnnotationsOnBeanPage()
    {
        TestBeanWithAnnotations bean = new TestBeanWithAnnotations();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), "someContext", this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }

    @Action(confirm = "Are you sure you want to save?")
    public void save(AjaxRequestTarget target, Form form, TestBeanWithAnnotations bean)
    {
        
        if (!BeanForm.findBeanFormParent(form).validateRequired()) {
            return; // Errors
        }
        
        info("Saved");
    }
}
