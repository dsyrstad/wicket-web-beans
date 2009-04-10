package com.googlecode.wicketwebbeans.examples.annotations;


import org.apache.wicket.ajax.AjaxRequestTarget;

import static com.googlecode.wicketwebbeans.annotations.Property.EMPTY;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

import com.googlecode.wicketwebbeans.annotations.Action;
import com.googlecode.wicketwebbeans.annotations.Bean;
import com.googlecode.wicketwebbeans.annotations.Beans;
import com.googlecode.wicketwebbeans.annotations.Property;
import com.googlecode.wicketwebbeans.annotations.Tab;
import com.googlecode.wicketwebbeans.containers.BeanForm;
import com.googlecode.wicketwebbeans.model.BeanMetaData;

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
