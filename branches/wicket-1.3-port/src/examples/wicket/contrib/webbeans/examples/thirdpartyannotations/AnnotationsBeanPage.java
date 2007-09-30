package wicket.contrib.webbeans.examples.thirdpartyannotations;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.model.BeanMetaData;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

public class AnnotationsBeanPage extends WebPage
{
    private BeanForm beanForm;
    
    public AnnotationsBeanPage()
    {
        Object bean = new Wrapper();
        BeanMetaData meta = new BeanMetaData(Wrapper.class, null, this, null, false);
        beanForm = new BeanForm("beanForm", bean, meta);
        add(beanForm);
    }
    
    public void save(AjaxRequestTarget target, Form form, Wrapper bean)
    {
        if (!beanForm.validateRequired()) {
            return; // Errors
        }
        
        info("Saved - thank you");
    }

    public static final class Wrapper implements Serializable {
        private JPABean jpaBean = new JPABean(null, "XYZ123");
        private JDOBean jdoBean = new JDOBean(null, "XYZ456");

        public JPABean getJpaBean()
        {
            return jpaBean;
        }
        
        public void setJpaBean(JPABean jpaBean)
        {
            this.jpaBean = jpaBean;
        }
        
        public JDOBean getJdoBean()
        {
            return jdoBean;
        }
        
        public void setJdoBean(JDOBean jdoBean)
        {
            this.jdoBean = jdoBean;
        }
    }
}
