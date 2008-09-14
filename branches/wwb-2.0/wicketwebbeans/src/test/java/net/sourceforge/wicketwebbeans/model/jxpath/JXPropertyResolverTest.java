package net.sourceforge.wicketwebbeans.model.jxpath;

import java.io.Serializable;
import java.math.BigDecimal;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.PropertyResolver;

import org.apache.commons.jxpath.JXPathException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

// TODO This should be an interface abstract test that calls getPropertyResolver 
public class JXPropertyResolverTest extends TestCase
{
    private PropertyResolver resolver = new JXPathPropertyResolver();
    private Employee testEmployee = new Employee("Dan Syrstad", new Address("Minneapolis", "MN", "55306",
                    "123 Any Way", "P.O. Box 9112"), BigDecimal.valueOf(2500000));
    private IModel testEmployeeModel = new Model(testEmployee);

    public void testGetValue()
    {
        assertSame(testEmployee, resolver.createPropertyProxy(".").getValue(testEmployeeModel));
        assertSame(testEmployee.getAddress(), resolver.createPropertyProxy("address").getValue(testEmployeeModel));
        assertSame(testEmployee.getAddress().getCity(), resolver.createPropertyProxy("address/city").getValue(
                        testEmployeeModel));
        assertSame(testEmployee.getAddress().getAddressLines(), resolver.createPropertyProxy("address/addressLines")
                        .getValue(testEmployeeModel));
        assertSame(testEmployee.getAddress().getAddressLines()[1], resolver.createPropertyProxy(
                        "address/addressLines[2]").getValue(testEmployeeModel));
    }

    public void testGetValueWithIntermediateNulls()
    {
        Employee employee = new Employee("Dan Syrstad", null, BigDecimal.valueOf(2500000));
        IModel employeeModel = new Model(employee);
        assertNull(resolver.createPropertyProxy("address/city").getValue(employeeModel));
    }

    public void testGetValueWithBadExpression()
    {
        try {
            resolver.createPropertyProxy("xyz").getValue(testEmployeeModel);
            fail("Expected exception");
        }
        catch (JXPathException e) {
            // Expected
        }
    }


    @SuppressWarnings("serial")
    public static final class Employee implements Serializable
    {
        private String name;
        private BigDecimal salary;
        private Address address;

        Employee(String name, Address address, BigDecimal salary)
        {
            this.address = address;
            this.name = name;
            this.salary = salary;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public BigDecimal getSalary()
        {
            return salary;
        }

        public void setSalary(BigDecimal salary)
        {
            this.salary = salary;
        }

        public Address getAddress()
        {
            return address;
        }

        public void setAddress(Address address)
        {
            this.address = address;
        }
    }


    @SuppressWarnings("serial")
    public static final class Address implements Serializable
    {
        private String[] addressLines;
        private String city;
        private String state;
        private String postalCode;

        Address(String city, String postalCode, String state, String... addressLines)
        {
            this.city = city;
            this.postalCode = postalCode;
            this.state = state;
            this.addressLines = addressLines;
        }

        public String[] getAddressLines()
        {
            return addressLines;
        }

        public void setAddressLines(String[] addressLine)
        {
            this.addressLines = addressLine;
        }

        public String getCity()
        {
            return city;
        }

        public void setCity(String city)
        {
            this.city = city;
        }

        public String getState()
        {
            return state;
        }

        public void setState(String state)
        {
            this.state = state;
        }

        public String getPostalCode()
        {
            return postalCode;
        }

        public void setPostalCode(String postalCode)
        {
            this.postalCode = postalCode;
        }
    }
}
