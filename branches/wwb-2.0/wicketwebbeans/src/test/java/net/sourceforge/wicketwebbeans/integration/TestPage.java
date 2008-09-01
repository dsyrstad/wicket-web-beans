package net.sourceforge.wicketwebbeans.integration;

import net.sourceforge.wicketwebbeans.model.BeanConfig;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;

public class TestPage extends WebPage
{
    public TestPage(BeanConfig config)
    {
        add((Component)config.newInstance("component"));
    }
}
