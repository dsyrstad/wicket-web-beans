package wicket.contrib.webbeans.examples.api;

import static wicket.contrib.webbeans.annotations.Property.EMPTY;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.contrib.webbeans.model.api.JAction;
import wicket.contrib.webbeans.model.api.JBean;
import wicket.contrib.webbeans.model.api.JProperty;
import wicket.contrib.webbeans.model.api.JTab;

public class ApiPage extends WebPage
{
    public ApiPage()
    {
        TestBean bean = new TestBean();
        
        // Create the meta data
        JBean jbean = new JBean(TestBean.class)
            .tabs(
                new JTab("General").propertyNames("firstName", "lastName", "idNumber"),
                new JTab("Address")
                    .propertyNames("address1", EMPTY, EMPTY, 
                                   "address2", EMPTY, EMPTY, "city", "state", "zip")
            )
            // Customize certain properties from above.
            .properties(
                new JProperty("firstName").required(true).maxLength(10),
                new JProperty("lastName").required(true)
            )
            .actions( new JAction("save").confirm("Are you sure you want to save?") );
        
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, jbean, this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }

    public void save(AjaxRequestTarget target, Form form, TestBean bean)
    {
        
        if (!BeanForm.findBeanFormParent(form).validateRequired()) {
            return; // Errors
        }
        
        info("Saved");
    }
}
