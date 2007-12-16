package net.sourceforge.wicketwebbeans.databinder.examples;

import net.sourceforge.wicketwebbeans.databinder.DataBeanListPanel;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;

@SuppressWarnings("serial")
public class ListPage extends WebPage
{
    public ListPage()
    {
        Form form = new Form("contactForm");
        add(form);
        form.add(new ListPanel("contactBeanTable", Contact.class, this));
        form = new Form("categoryForm");
        add(form);
        form.add(new ListPanel("categoryBeanTable", Category.class, this));

    }


    public static class ListPanel extends DataBeanListPanel
    {
        private Page returnPage;

        public ListPanel(String id, Class<?> beanClass, Page returnPage)
        {
            super(id, beanClass);
            this.returnPage = returnPage;
        }

        public void edit(AjaxRequestTarget target, Form form, Object bean)
        {
            setResponsePage(new EditPage(bean, returnPage));
        }
    }
}
