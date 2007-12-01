package net.sourceforge.wicketwebbeans.examples.annotations;

import net.sourceforge.wicketwebbeans.annotations.Action;
import net.sourceforge.wicketwebbeans.annotations.Bean;
import net.sourceforge.wicketwebbeans.annotations.Beans;
import net.sourceforge.wicketwebbeans.annotations.Property;
import net.sourceforge.wicketwebbeans.annotations.Tab;
import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;

import org.apache.wicket.ajax.AjaxRequestTarget;

import static net.sourceforge.wicketwebbeans.annotations.Property.EMPTY;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

public class AnnotationsOnMetaDataClassPage extends WebPage
{
    public AnnotationsOnMetaDataClassPage()
    {
        TestBean bean = new TestBean();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, TestBeanMetaData.class, this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }

    @Action(confirm = "Are you sure you want to save?")
    public void save(AjaxRequestTarget target, Form form, TestBean bean)
    {
        
        if (!BeanForm.findBeanFormParent(form).validateRequired()) {
            return; // Errors
        }
        
        info("Saved");
    }
}
