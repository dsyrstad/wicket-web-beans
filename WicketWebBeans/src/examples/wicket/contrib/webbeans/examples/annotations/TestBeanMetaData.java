package wicket.contrib.webbeans.examples.annotations;

import static wicket.contrib.webbeans.annotations.Property.EMPTY;
import wicket.contrib.webbeans.annotations.Bean;
import wicket.contrib.webbeans.annotations.Beans;
import wicket.contrib.webbeans.annotations.Property;
import wicket.contrib.webbeans.annotations.Tab;

@Beans({
    @Bean(type = TestBean.class,
        tabs = {
            @Tab(name = "General", propertyNames = { "firstName", "lastName", "idNumber" }),
            @Tab(name = "Address", propertyNames = { 
                "address1", EMPTY, EMPTY, 
                "address2", EMPTY, EMPTY, "city", "state", "zip" })
        },
        
        // Customize certain properties from above.
        properties = {
          @Property(name = "firstName", required = true, maxLength = 10),
          @Property(name = "lastName", required = true),
        }
    ),

    // Inherits from default context.
    @Bean(type = TestBean.class, context = "someContext", propertyNames = "-idNumber"),
})
public interface TestBeanMetaData
{
    // This is just an interface to hold the annotations.
}
