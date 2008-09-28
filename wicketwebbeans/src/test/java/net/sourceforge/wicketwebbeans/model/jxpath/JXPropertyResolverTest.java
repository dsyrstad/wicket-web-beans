package net.sourceforge.wicketwebbeans.model.jxpath;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.JavaBeansPropertyPathBeanCreator;
import net.sourceforge.wicketwebbeans.model.PropertyPathBeanCreator;
import net.sourceforge.wicketwebbeans.model.PropertyResolver;
import net.sourceforge.wicketwebbeans.test.Address;
import net.sourceforge.wicketwebbeans.test.Employee;

/**
 * Tests JXPropertyResolver and JXPropertyProxy. <p>
 * 
 * @author Dan Syrstad
 */
public class JXPropertyResolverTest extends TestCase
{
    private PropertyPathBeanCreator beanCreator = new JavaBeansPropertyPathBeanCreator();
    private PropertyResolver resolver = new JXPathPropertyResolver();
    private Employee testEmployee = new Employee("Dan Syrstad", new Address("Minneapolis", "MN", "55306",
                    "123 Any Way", "P.O. Box 9112"), BigDecimal.valueOf(2500000));

    public void testGetValue()
    {
        assertSame(testEmployee, resolver.createPropertyProxy(beanCreator, ".").getValue(testEmployee));
        assertSame(testEmployee.getAddress(), resolver.createPropertyProxy(beanCreator, "address").getValue(
                        testEmployee));
        assertSame(testEmployee.getAddress().getCity(), resolver.createPropertyProxy(beanCreator, "address/city")
                        .getValue(testEmployee));
        assertSame(testEmployee.getAddress().getAddressLines(), resolver.createPropertyProxy(beanCreator,
                        "address/addressLines").getValue(testEmployee));
        assertSame(testEmployee.getAddress().getAddressLines()[1], resolver.createPropertyProxy(beanCreator,
                        "address/addressLines[2]").getValue(testEmployee));
    }

    public void testGetValueWithIntermediateNulls()
    {
        Employee employee = new Employee("Dan Syrstad", null, BigDecimal.valueOf(2500000));
        assertNull(resolver.createPropertyProxy(beanCreator, "address/city").getValue(employee));
    }

    public void testGetValueWithBadExpression()
    {
        assertNull(resolver.createPropertyProxy(beanCreator, "xyz").getValue(testEmployee));
    }

    public void testSetValue()
    {
        resolver.createPropertyProxy(beanCreator, "name").setValue(testEmployee, "New Name");
        assertEquals("New Name", testEmployee.getName());
    }

    public void testSetValueWithIntermediateNulls()
    {
        testEmployee.setAddress(null);
        resolver.createPropertyProxy(beanCreator, "address/city").setValue(testEmployee, "New City");
        assertEquals("New City", testEmployee.getAddress().getCity());
    }

    public void testMatches()
    {
        testEmployee.setAddress(null);
        JXPathPropertyProxy proxy = (JXPathPropertyProxy)resolver.createPropertyProxy(beanCreator,
                        "address/relatedAddress/addressLines[1]");

        assertTrue(proxy.matches(testEmployee, new PropertyChangeEvent(testEmployee, "address", null, null)));
        // Existing property on same object, but doesn't match.
        assertFalse(proxy.matches(testEmployee, new PropertyChangeEvent(testEmployee, "name", null, null)));
        // Shouldn't match some random object with correct property name.
        assertFalse(proxy.matches(testEmployee, new PropertyChangeEvent(new Object(), "address", null, null)));
        // Shouldn't match correct object with incorrect property name
        assertFalse(proxy.matches(testEmployee, new PropertyChangeEvent(testEmployee, "x", null, null)));
        // If property name is null it means "match any property".
        assertTrue(proxy.matches(testEmployee, new PropertyChangeEvent(testEmployee, null, null, null)));
    }

    public void testMatchesOnIntermediatePathProperty()
    {
        JXPathPropertyProxy proxy = (JXPathPropertyProxy)resolver.createPropertyProxy(beanCreator,
                        "address/relatedAddress/addressLines[1]");

        Address address = testEmployee.getAddress();
        assertTrue(proxy.matches(testEmployee, new PropertyChangeEvent(address, "relatedAddress", null, null)));
        // Right object, wrong property.
        assertFalse(proxy.matches(testEmployee, new PropertyChangeEvent(address, "city", null, null)));
    }

    public void testMatchesOnLastPathProperty()
    {
        JXPathPropertyProxy proxy = (JXPathPropertyProxy)resolver.createPropertyProxy(beanCreator,
                        "address/relatedAddress/addressLines[1]");

        Address relatedAddress = new Address("city", "55128", "MN", "addr1", "addr2", "addr3");
        Address address = testEmployee.getAddress();
        address.setRelatedAddress(relatedAddress);

        assertTrue(proxy.matches(testEmployee, new PropertyChangeEvent(relatedAddress, "addressLines", null, null)));
        // Right object, wrong property.
        assertFalse(proxy.matches(testEmployee, new PropertyChangeEvent(relatedAddress, "city", null, null)));
        // A different Address object
        assertFalse(proxy.matches(testEmployee, new PropertyChangeEvent(address, "addressLines", null, null)));
    }
}
