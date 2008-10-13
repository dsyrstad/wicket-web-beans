package net.sourceforge.wicketwebbeans.test;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;

public class SingleComponentTestPage extends WebPage
{
    public SingleComponentTestPage(Component component)
    {
        add(component);
    }
}
