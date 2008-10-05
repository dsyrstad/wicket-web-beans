package net.sourceforge.wicketwebbeans.examples.experimental;

import net.sourceforge.wicketwebbeans.examples.BeanWithEvents;
import net.sourceforge.wicketwebbeans.model.BeanFactory;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;

public class ExperimentalPage extends WebPage
{
    public ExperimentalPage()
    {
        BeanFactory factory = new BeanFactory(new BeanWithEvents("Dan", "Serial#")).loadBeanConfig(getClass()
                        .getResource("ExperimentConfig.wwb"));
        Component root = (Component)factory.newInstance("Root", "component");
        add(root);
    }
}
