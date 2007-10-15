package wicket.contrib.webbeans.databinder.examples;

import org.apache.wicket.Page;
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
        form.add(panel = new ContactBeanListPanel("beanTable", Contact.class, this));
    }
    
    static class ContactBeanListPanel extends DataBeanListPanel
    {
        private Page returnPage;
        
        public ContactBeanListPanel(String id, Class beanClass, Page returnPage)
        {
            super(id,beanClass);
            this.returnPage = returnPage;
        }
        
        public void edit(AjaxRequestTarget target, Form form, Object bean)
        {
            setResponsePage(new EditPage(bean,returnPage));
        }
    }
}

