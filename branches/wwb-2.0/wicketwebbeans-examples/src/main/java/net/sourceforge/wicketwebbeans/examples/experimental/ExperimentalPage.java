package net.sourceforge.wicketwebbeans.examples.experimental;

import net.sourceforge.wicketwebbeans.examples.SerializableBean;
import net.sourceforge.wicketwebbeans.model.BeanFactory;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;

public class ExperimentalPage extends WebPage
{
    public ExperimentalPage()
    {
        BeanFactory factory = new BeanFactory(new SerializableBean("Dan", "Serial#")).loadBeanConfig(getClass()
                        .getResource("ExperimentConfig.wwb"));
        Component root = (Component)factory.newInstance("Root", "component");
        add(root);
    }
}
