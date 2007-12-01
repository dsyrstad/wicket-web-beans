package net.sourceforge.wicketwebbeans.examples.dependentfields;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import net.sourceforge.wicketwebbeans.model.ComponentRegistry;
import wicket.markup.html.WebPage;

public class DependentFieldPage extends WebPage
{
    public DependentFieldPage()
    {
        // Register the ModelField for the Model enum class.
        ComponentRegistry registry = new ComponentRegistry();
        registry.register(Model.class, ModelField.class);
        
        Car bean = new Car();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, registry, false);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
