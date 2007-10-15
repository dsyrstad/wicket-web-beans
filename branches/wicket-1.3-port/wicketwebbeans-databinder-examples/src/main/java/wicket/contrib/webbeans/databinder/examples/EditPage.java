package wicket.contrib.webbeans.databinder.examples;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;

import wicket.contrib.webbeans.databinder.DataBeanEditPanel;

public class EditPage extends WebPage
{
    private Panel panel;
    
    public EditPage(Object bean, Page returnPage)
    {
        add(panel = new ContactBeanEditPanel("beanTable", bean, returnPage));
    }
    
    static class ContactBeanEditPanel extends DataBeanEditPanel
    {
        public ContactBeanEditPanel(String id, Object bean, Page returnPage)
        {
            super(id,bean,returnPage);
        }
    }
}

