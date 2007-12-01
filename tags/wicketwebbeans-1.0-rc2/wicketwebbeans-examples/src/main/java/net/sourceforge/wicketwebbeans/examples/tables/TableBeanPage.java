package net.sourceforge.wicketwebbeans.examples.tables;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import wicket.ajax.AjaxRequestTarget;
import wicket.markup.html.WebPage;
import wicket.markup.html.form.Form;

public class TableBeanPage extends WebPage
{
    private Invoice bean = new Invoice();
    
    public TableBeanPage()
    {
        // Start with one empty line.
        bean.getLines().add( new InvoiceLine() );
        
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }
    
    public void addLine(AjaxRequestTarget target, Form form, Invoice bean)
    {
        this.bean.getLines().add( new InvoiceLine() );
    }

    public void removeLine(AjaxRequestTarget target, Form form, InvoiceLine line)
    {
        bean.getLines().remove(line);
        info("Removed line with item code " + (line.getItemCode() == null ? "<empty>" : line.getItemCode()) );
    }
}
