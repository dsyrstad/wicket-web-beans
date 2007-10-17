package wicket.contrib.webbeans.databinder.examples;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;


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
	    Session session = DataStaticService.getHibernateSessionFactory().openSession();
	    session.beginTransaction();
	    Map<Integer,Category> categories = new HashMap<Integer,Category>();
	    String[] names = new String[]{"Friends","Family","Business"};
	    for(int ii = 0; ii < names.length; ii++ )
	    {
	    	Category category = new Category();
		    category.setName(names[ii]);
		    session.save(category);	
		    categories.put(ii, category);
	    }
	    URL url = getClass().getResource("/wicket/contrib/webbeans/databinder/examples/RandomNames.txt");
	    Random random = new Random();
	    int numberBase = 1;
	    String line = null;
	    try
	    {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		    while(null != (line = reader.readLine()))
		    {
		    	String[] name = line.split("\\t");
		    	Contact contact = new Contact();
			    contact.setFirstName(name[0]);
			    contact.setLastName(name[1]);
			    contact.setCategory(categories.get(random.nextInt(2)));
			    contact.setPhoneNumber(String.format("800-555-%04d",numberBase++));
			    session.save(contact);	
		    }
	    }
	    catch(IOException ex)
	    {
	    	throw new RuntimeException(ex);
	    }
	    session.getTransaction().commit();
	    session.close();
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
