/*---
   Copyright 2008 Visual Systems Corporation.
   http://www.vscorp.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
        http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
---*/

package net.sourceforge.wicketwebbeans.model;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.jxpath.JXPathPropertyResolver;
import net.sourceforge.wicketwebbeans.test.Address;
import net.sourceforge.wicketwebbeans.test.Employee;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Tests PropertyBinder. <p>
 * 
 * @author Dan Syrstad
 */
public class PropertyBinderTest extends TestCase
{
    private PropertyPathBeanCreator beanCreator = new JavaBeansPropertyPathBeanCreator();

    public void testIsActive()
    {
        // Try listenBean being GC'd.
        Employee listenBean = new Employee();
        Address updateBean = new Address();
        PropertyProxy listenProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, "address");
        PropertyProxy updateProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, ".");
        PropertyBinder binder = new PropertyBinder(listenBean, updateBean, listenProxy, updateProxy);

        // Should still be active because beans have not been GC'd.
        assertTrue(binder.isActive());

        listenBean = null;
        System.gc();

        // Should have been GC'd now.
        assertFalse(binder.isActive());
        // This should not fail
        binder.updateProperty();
        // Matches should return false (i.e., should not throw)
        assertFalse(binder.matchesListenBean(new PropertyChangeEvent(new Object(), "x", null, null)));

        // Try again, but this time update bean is GC'd.
        listenBean = new Employee();
        updateBean = new Address();
        binder = new PropertyBinder(listenBean, updateBean, listenProxy, updateProxy);

        // Should still be active because beans have not been GC'd.
        assertTrue(binder.isActive());

        updateBean = null;
        System.gc();

        // Should have been GC'd now.
        assertFalse(binder.isActive());
        // This should not fail
        binder.updateProperty();
        // Matches should return false even though it matches the original listenProxy. 
        assertFalse(binder.matchesListenBean(new PropertyChangeEvent(listenBean, "address", null, null)));

        // Try again, but this time both beans are GC'd.
        listenBean = new Employee();
        updateBean = new Address();
        binder = new PropertyBinder(listenBean, updateBean, listenProxy, updateProxy);

        // Should still be active because beans have not been GC'd.
        assertTrue(binder.isActive());

        updateBean = null;
        listenBean = null;
        System.gc();

        // Should have been GC'd now.
        assertFalse(binder.isActive());
        // This should not fail
        binder.updateProperty();
        // Matches should return false (i.e., should not throw)
        assertFalse(binder.matchesListenBean(new PropertyChangeEvent(new Object(), "x", null, null)));
    }

    public void testMatchesListenBean()
    {
        Address address = new Address("Mpls", "55128", "MN", "123 Any Way", "456 No Way");
        Employee listenBean = new Employee("Dan", address, null);
        Address relatedAddress = new Address("San Diego", "99999", "CA", "987 Some Way");
        address.setRelatedAddress(relatedAddress);

        Employee updateBean = new Employee();
        PropertyProxy listenProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator,
                        "address/relatedAddress/city");
        PropertyProxy updateProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, ".");
        PropertyBinder binder = new PropertyBinder(listenBean, updateBean, listenProxy, updateProxy);

        assertTrue(binder.matchesListenBean(new PropertyChangeEvent(listenBean, "address", null, null)));
        assertTrue(binder.matchesListenBean(new PropertyChangeEvent(address, "relatedAddress", null, null)));
        assertTrue(binder.matchesListenBean(new PropertyChangeEvent(relatedAddress, "city", null, null)));
        // Wrong bean
        assertFalse(binder.matchesListenBean(new PropertyChangeEvent(updateBean, "address", null, null)));

        // Change related address without affecting root bean.
        Address relatedAddress2 = new Address("San Diego", "99999", "CA", "987 Some Way");
        address.setRelatedAddress(relatedAddress2);

        // This returned true last time but should now be false
        assertFalse(binder.matchesListenBean(new PropertyChangeEvent(relatedAddress, "city", null, null)));
        assertTrue(binder.matchesListenBean(new PropertyChangeEvent(relatedAddress2, "city", null, null)));
    }

    public void testUpdateProperty()
    {
        Address address = new Address("Mpls", "55128", "MN", "123 Any Way", "456 No Way");
        Employee listenBean = new Employee("Dan", address, null);
        Address relatedAddress = new Address("San Diego", "99999", "CA", "987 Some Way");
        address.setRelatedAddress(relatedAddress);

        Employee updateBean = new Employee();
        PropertyProxy listenProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator,
                        "address/relatedAddress");
        PropertyProxy updateProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, "address");
        PropertyBinder binder = new PropertyBinder(listenBean, updateBean, listenProxy, updateProxy);

        assertNotNull(listenBean.getAddress().getRelatedAddress());
        assertNull(updateBean.getAddress());

        binder.updateProperty();

        assertSame(relatedAddress, updateBean.getAddress());
    }

    public void testUpdatePropertyWithConversion()
    {
        Employee listenBean = new Employee("Dan", null, BigDecimal.valueOf(101.01));
        Employee updateBean = new Employee();

        PropertyProxy listenProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, "salary");
        PropertyProxy updateProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator,
                        "address/relatedAddress/city");
        PropertyBinder binder = new PropertyBinder(listenBean, updateBean, listenProxy, updateProxy);

        assertNotNull(listenBean.getSalary());
        assertNull(updateBean.getAddress());

        binder.updateProperty();

        assertEquals("101.01", updateBean.getAddress().getRelatedAddress().getCity());
    }

    public void testBeanWrappedAsIModel()
    {
        Employee listenBean = new Employee("Dan", null, BigDecimal.valueOf(101.01));
        IModel listenBeanModel = new Model(listenBean);
        Employee updateBean = new Employee();
        IModel updateBeanModel = new Model(updateBean);

        PropertyProxy listenProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, "salary");
        PropertyProxy updateProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator,
                        "address/relatedAddress/city");
        PropertyBinder binder = new PropertyBinder(listenBeanModel, updateBeanModel, listenProxy, updateProxy);

        assertNotNull(listenBean.getSalary());
        assertNull(updateBean.getAddress());

        // Test updateProperty
        binder.updateProperty();

        assertEquals("101.01", updateBean.getAddress().getRelatedAddress().getCity());

        // Test matchesListenBean
        assertTrue(binder.matchesListenBean(new PropertyChangeEvent(listenBean, "salary", null, null)));
        // Wrong bean
        assertFalse(binder.matchesListenBean(new PropertyChangeEvent(listenBeanModel, "salary", null, null)));
    }
}
