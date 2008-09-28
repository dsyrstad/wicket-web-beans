package net.sourceforge.wicketwebbeans.test;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;

/**
 * Intended to be used with WicketTester.clickLink("link", true) to simulate Ajax requests.
 * Create an AjaxLink component with an id of "link" and override onClick() to handle the processing 
 * that should occur during the Ajax request. Set this component on ajaxLink prior to constructing the page. <p>
 * 
 * @author Dan Syrstad
 */
public class AjaxTestPage extends WebPage
{
    public static AjaxLink ajaxLink;

    public AjaxTestPage()
    {
        add(ajaxLink);
    }
}
