package wicket.contrib.webbeans.databinder.examples;

import java.net.URL;

import org.hibernate.cfg.AnnotationConfiguration;

import net.databinder.DataApplication;

public class ExampleApplication extends DataApplication
{
	public ExampleApplication()
	{
	    
	}

	@Override
	public Class getHomePage()
	{
		return ListPage.class;
	}
	
	@Override
	protected void configureHibernate(AnnotationConfiguration config)
    {
	    super.configureHibernate(config);
	    URL url = getClass().getResource("/wicket/contrib/webbeans/databinder/examples/hibernate.cfg.xml");
        config.configure(url);
        config.addAnnotatedClass(Player.class);
        config.addAnnotatedClass(Country.class);
        config.addAnnotatedClass(City.class);
    }
}
