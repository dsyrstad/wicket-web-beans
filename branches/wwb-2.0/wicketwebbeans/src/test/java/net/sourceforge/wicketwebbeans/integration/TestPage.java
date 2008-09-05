package net.sourceforge.wicketwebbeans.integration;

import net.sourceforge.wicketwebbeans.model.BeanFactory;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;

public class TestPage extends WebPage
{
    public TestPage(BeanFactory factory)
    {
        add((Component)factory.newInstance("Field", "component"));
    }
}
