package net.sourceforge.wicketwebbeans.examples.yuidate;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.fields.YUIDateField;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import net.sourceforge.wicketwebbeans.model.ComponentRegistry;

import org.apache.wicket.markup.html.WebPage;


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
