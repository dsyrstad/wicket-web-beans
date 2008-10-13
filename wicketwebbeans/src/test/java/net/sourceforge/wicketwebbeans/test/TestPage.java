package net.sourceforge.wicketwebbeans.test;

import net.sourceforge.wicketwebbeans.model.BeanFactory;

import org.apache.wicket.Component;

public class TestPage extends SingleComponentTestPage
{
    public TestPage(String componentName, BeanFactory factory)
    {
        super((Component)factory.newInstance(componentName, "component"));
    }
}
