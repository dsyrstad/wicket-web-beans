package net.sourceforge.wicketwebbeans.examples.api;

import static net.sourceforge.wicketwebbeans.annotations.Property.EMPTY;
import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import net.sourceforge.wicketwebbeans.model.api.JAction;
import net.sourceforge.wicketwebbeans.model.api.JBean;
import net.sourceforge.wicketwebbeans.model.api.JProperty;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

public class ApiPage extends WebPage
{
    public ApiPage()
    {
        TestBean bean = new TestBean();

        // Create the meta data
        JBean jbean = new JBean(TestBean.class).propertyNames("firstName", "lastName", "idNumber", "address1", EMPTY,
                        EMPTY, "address2", EMPTY, EMPTY, "city", "state", "zip")
                        // Customize certain properties from above.
                        .properties(new JProperty("firstName").required(true).maxLength(10),
                                        new JProperty("lastName").required(true)).actions(
                                        new JAction("save").confirm("Are you sure you want to save?"));

        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, jbean, this, null);
        add(new BeanForm("beanForm", bean, meta));
    }

    public void save(AjaxRequestTarget target, Form form, TestBean bean)
    {

        if (!BeanForm.findBeanFormParent(form).validateRequired()) {
            return; // Errors
        }

        info("Saved");
    }
}
