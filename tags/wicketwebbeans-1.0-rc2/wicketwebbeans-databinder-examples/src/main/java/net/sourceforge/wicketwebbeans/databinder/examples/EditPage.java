package net.sourceforge.wicketwebbeans.databinder.examples;

import net.sourceforge.wicketwebbeans.databinder.DataBeanEditPanel;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;


public class EditPage extends WebPage
{
    public EditPage(Object bean, Page returnPage)
    {
        add(new EditPanel("beanForm", bean, returnPage));
    }
    
    public static class EditPanel extends DataBeanEditPanel
    {
        public EditPanel(String id, Object bean, Page returnPage)
        {
            super(id,bean,returnPage);
        }
    }
}

