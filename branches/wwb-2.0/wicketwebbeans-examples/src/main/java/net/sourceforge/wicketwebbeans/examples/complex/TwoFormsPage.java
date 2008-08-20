package net.sourceforge.wicketwebbeans.examples.complex;


import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

public class TwoFormsPage extends WebPage
{
    private static final long serialVersionUID = 1L;

    public TwoFormsPage()
    {
        TwoFormsBean bean1 = new TwoFormsBean();
        ComponentConfig meta1 = new ComponentConfig(bean1.getClass(), null, this, null);
        BeanForm beanForm1 = new BeanForm("beanForm1", bean1, meta1);
        add(beanForm1);

        TwoFormsBean bean2 = new TwoFormsBean();
        ComponentConfig meta2 = new ComponentConfig(bean2.getClass(), null, this, null);
        BeanForm beanForm2 = new BeanForm("beanForm2", bean2, meta2);
        add(beanForm2);
    }
    
    public void save(AjaxRequestTarget target, Form form, TwoFormsBean bean)
    {
        BeanForm beanForm = BeanForm.findBeanFormParent(form);
        if (!beanForm.validateRequired()) {
            return; // Errors
        }
        
        beanForm.info("Saved - thank you");
    }

    public void cancel(AjaxRequestTarget target, Form form, TwoFormsBean bean)
    {
        BeanForm beanForm = BeanForm.findBeanFormParent(form);
        info("Canceled - Page message");
        beanForm.info("Canceled - Form message");
    }
}