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

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.Address;
import net.sourceforge.wicketwebbeans.Employee;
import net.sourceforge.wicketwebbeans.model.jxpath.JXPathPropertyResolver;

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

        // Try again, but this time update bean is removed.
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
    }
}
