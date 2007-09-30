package wicket.contrib.webbeans.examples.dependentfields;

import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.contrib.webbeans.model.ComponentRegistry;
import org.apache.wicket.markup.html.WebPage;

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
