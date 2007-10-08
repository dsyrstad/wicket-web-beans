package wicket.contrib.webbeans.examples.yuidate;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.wicket.markup.html.WebPage;

import wicket.contrib.webbeans.containers.BeanForm;
import wicket.contrib.webbeans.fields.YUIDateField;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.contrib.webbeans.model.ComponentRegistry;

public class DatePage extends WebPage
{
    public DatePage()
    {
        ComponentRegistry registry = new ComponentRegistry();
        registry.register(Date.class, YUIDateField.class);
        registry.register(java.sql.Date.class, YUIDateField.class);
        registry.register(Time.class, YUIDateField.class);
        registry.register(Timestamp.class, YUIDateField.class);
        registry.register(Calendar.class, YUIDateField.class);

        TestBean bean = new TestBean();
        BeanMetaData meta = new BeanMetaData(bean.getClass(), null, this, registry, false);
        add( new BeanForm("beanForm", bean, meta) );
    }
}
