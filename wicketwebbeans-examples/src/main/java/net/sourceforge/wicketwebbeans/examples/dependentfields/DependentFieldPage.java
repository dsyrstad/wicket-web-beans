package net.sourceforge.wicketwebbeans.examples.dependentfields;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;
import net.sourceforge.wicketwebbeans.model.ComponentRegistry;

import org.apache.wicket.markup.html.WebPage;

public class DependentFieldPage extends WebPage
{
    public DependentFieldPage()
    {
        // Register the ModelField for the Model enum class.
        ComponentRegistry registry = new ComponentRegistry();
        registry.register(Model.class, ModelField.class);
        
        Car bean = new Car();
        ComponentConfig meta = new ComponentConfig(bean.getClass(), null, this, registry);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
