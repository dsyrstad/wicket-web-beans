package net.sourceforge.wicketwebbeans.examples.annotations;

import static net.sourceforge.wicketwebbeans.annotations.Property.EMPTY;
import net.sourceforge.wicketwebbeans.annotations.Bean;
import net.sourceforge.wicketwebbeans.annotations.Beans;
import net.sourceforge.wicketwebbeans.annotations.Property;

@Beans({
    @Bean(type = TestBean.class,
        propertyNames = { "firstName", "lastName", "idNumber",
                "address1", EMPTY, EMPTY, 
                "address2", EMPTY, EMPTY, "city", "state", "zip"
        },
        
        // Customize certain properties from above.
        properties = {
          @Property(name = "firstName", required = true, maxLength = 10),
          @Property(name = "lastName", required = true)
        }
    ),

    // Inherits from default context.
    @Bean(type = TestBean.class, context = "someContext", propertyNames = "-idNumber")
})
public interface TestComponentConfig
{
    // This is just an interface to hold the annotations.
}
