package net.sourceforge.wicketwebbeans.examples.customfields;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;
import net.sourceforge.wicketwebbeans.model.ComponentRegistry;

import org.apache.wicket.markup.html.WebPage;

public class CustomFieldPage extends WebPage
{
    public CustomFieldPage()
    {
        Address bean = new Address();
        
        // Register the ModelField for the Country class.
        ComponentRegistry registry = new ComponentRegistry();
        registry.register(Country.class, CountryField.class);
        
        ComponentConfig meta = new ComponentConfig(bean.getClass(), null, this, registry);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
