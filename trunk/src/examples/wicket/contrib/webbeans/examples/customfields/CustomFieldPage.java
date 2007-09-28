package wicket.contrib.webbeans.examples.customfields;

import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.contrib.webbeans.model.ComponentRegistry;
import wicket.markup.html.WebPage;

public class CustomFieldPage extends WebPage
{
    public CustomFieldPage()
    {
        Address bean = new Address();
        
        // Register the ModelField for the Country class.
        ComponentRegistry registry = new ComponentRegistry();
        registry.register(Country.class, CountryField.class);
        
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, registry, false);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
