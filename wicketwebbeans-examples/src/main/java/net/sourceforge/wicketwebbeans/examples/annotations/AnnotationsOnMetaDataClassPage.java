package net.sourceforge.wicketwebbeans.examples.annotations;

import net.sourceforge.wicketwebbeans.annotations.Action;
import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

public class AnnotationsOnMetaDataClassPage extends WebPage
{
    public AnnotationsOnMetaDataClassPage()
    {
        TestBean bean = new TestBean();
        ComponentConfig meta = new ComponentConfig(bean.getClass(), null, TestComponentConfig.class, this, null);
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
