package wicket.contrib.webbeans.databinder.examples;

import java.net.URL;

import org.hibernate.cfg.AnnotationConfiguration;

import net.databinder.DataApplication;
import net.databinder.DataStaticService;

import org.hibernate.Session;

public class ExampleApplication extends DataApplication
{
	public ExampleApplication()
	{
	    URL url = getClass().getResource("/wicket/contrib/webbeans/databinder/examples/log4j.config.xml");
        org.apache.log4j.xml.DOMConfigurator.configure(url);
	}

	@Override
	public Class getHomePage()
	{
		return ListPage.class;
	}
	
	@Override
	protected void init()
	{
	    super.init();
	    // load some example contacts
	    Session session = DataStaticService.getHibernateSession();
	    session.beginTransaction();
	    Category category = new Category();
	    category.setName("Friends");
	    session.save(category);
	    Contact contact = new Contact();
	    contact.setName("John Smith");
	    contact.setCategory(category);
	    contact.setPhoneNumber("1-800-555-1234");
	    session.save(contact);
	    session.getTransaction().commit();
	}
	
	@Override
	protected void configureHibernate(AnnotationConfiguration config)
    {
	    super.configureHibernate(config);
	    URL url = getClass().getResource("/wicket/contrib/webbeans/databinder/examples/hibernate.cfg.xml");
        config.configure(url);
        config.addAnnotatedClass(Contact.class);
        config.addAnnotatedClass(Category.class);
    }
}
