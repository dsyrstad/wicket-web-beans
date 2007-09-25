package wicket.contrib.webbeans.examples.annotations;

import wicket.ajax.AjaxRequestTarget;
import wicket.contrib.webbeans.annotations.Action;
import wicket.contrib.webbeans.annotations.Bean;
import wicket.contrib.webbeans.annotations.Beans;
import wicket.contrib.webbeans.annotations.Property;
import wicket.contrib.webbeans.annotations.Tab;
import static wicket.contrib.webbeans.annotations.Property.EMPTY;
import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.markup.html.WebPage;
import wicket.markup.html.form.Form;

@Beans({
    @Bean(type = TestBean.class, 
        // Defines order for the properties that will implicitly fall on the General tab.
        propertyNames = { "firstName", "lastName", "idNumber" },
        tabs = {
            @Tab(name = "General"),
            @Tab(name = "Address", propertyNames = { 
                "address1", EMPTY, EMPTY, 
                "address2", EMPTY, EMPTY, "city", "state", "zip" })
        },
        // Customize certain properties from above.
        properties = {
          @Property(name = "firstName", required = true),
          @Property(name = "lastName", required = true),
        }
    )
})

public class AnnotationsOnPage extends WebPage
{
    public AnnotationsOnPage()
    {
        TestBean bean = new TestBean();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }

    @Action(confirm = "Are you sure you want to save")
    public void save(AjaxRequestTarget target, Form form, TestBean bean)
    {
        
        if (!BeanForm.findBeanFormParent(form).validateRequired()) {
            return; // Errors
        }
        
        info("Saved");
    }
}
