package net.sourceforge.wicketwebbeans.examples.login;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import net.sourceforge.wicketwebbeans.model.api.JBean;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;


@SuppressWarnings("serial")
public class LoginPage extends WebPage
{
    public LoginPage()
    {
        LoginBean bean = new LoginBean();
        
        // Create the meta data
        JBean jbean = new JBean(LoginBean.class)
            .propertyNames("userName", "password", "action.login");
        
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, jbean, this, null, false);
        add( new BeanForm("beanForm", bean, meta) );
    }

    public void login(AjaxRequestTarget target, Form form, LoginBean bean)
    {
        if (!BeanForm.findBeanFormParent(form).validateRequired()) {
            return; // Errors
        }
        
        if (bean.getUserName().equals("wicket") && bean.getPassword().equals("xyzzy")) {
            info("Logged In");
        }
        else {
            error("Invalid Username or Password - Please try again");
        }
    }
}
