package wicket.contrib.webbeans.databinder.examples;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;

import wicket.contrib.webbeans.databinder.DataBeanListPanel;

public class ListPage extends WebPage
{
    private Panel panel;
    
    public ListPage()
    {
        Form form = new Form("form");
        add(form);
        form.add(panel = new ContactBeanListPanel("beanTable", Contact.class));
    }
    
    private static class ContactBeanListPanel extends DataBeanListPanel
    {
        public ContactBeanListPanel(String id, Class beanClass)
        {
            super(id,beanClass);
        }
        
        public void create(AjaxRequestTarget target, Form form, Object bean)
        {
            
        }
        
        public void edit(AjaxRequestTarget target, Form form, Object bean)
        {
            
        }
    }
}

