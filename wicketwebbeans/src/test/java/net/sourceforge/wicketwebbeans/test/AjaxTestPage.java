package net.sourceforge.wicketwebbeans.test;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;

public class AjaxTestPage extends WebPage
{
    public AjaxLink ajaxLink;

    @SuppressWarnings("serial")
    public AjaxTestPage()
    {
        ajaxLink = new AjaxLink("link") {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
            }
        };
        add(ajaxLink);
    }
}
