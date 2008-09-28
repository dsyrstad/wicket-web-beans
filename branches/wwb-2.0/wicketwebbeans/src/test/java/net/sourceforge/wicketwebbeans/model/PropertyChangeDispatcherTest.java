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

import java.math.BigDecimal;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.jxpath.JXPathPropertyResolver;
import net.sourceforge.wicketwebbeans.test.Employee;

/**
 * Tests {@link PropertyChangeDispatcher}. <p>
 * 
 * @author Dan Syrstad
 */
public class PropertyChangeDispatcherTest extends TestCase
{
    private PropertyPathBeanCreator beanCreator = new JavaBeansPropertyPathBeanCreator();
    private Employee listenBean = new Employee("Dan", null, BigDecimal.valueOf(101.01));
    private Employee updateBean = new Employee();
    private PropertyProxy listenProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, "salary");
    private PropertyProxy updateProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, "salary");
    private PropertyBinder binder = new PropertyBinder(listenBean, updateBean, listenProxy, updateProxy);
    private PropertyChangeDispatcher dispatcher = new PropertyChangeDispatcher();

    public void testAddRemoveBinder()
    {
        dispatcher.add(binder);
        // Dispatch to test that it was added.
        assertNull(updateBean.getSalary());
        dispatcher.dispatch(listenBean, "salary");
        assertNotNull(updateBean.getSalary());

        dispatcher.remove(binder);
        updateBean.setSalary(null);
        dispatcher.dispatch(listenBean, "salary");
        // Should not have been updated since binder was removed.
        assertNull(updateBean.getSalary());
    }

    public void testBinderAutomaticallyRemovedWhenBeanCollectedAndDispatchCalled()
    {
        dispatcher.add(binder);

        // About all we can do here is test that dispatch doesn't throw.
        updateBean = null;
        System.gc();

        assertEquals(1, dispatcher.getPropertyBinders().size());
        dispatcher.dispatch(listenBean, "salary");
        assertEquals(0, dispatcher.getPropertyBinders().size());
    }

    public void testDispatchDoesNotMatchBinder()
    {
        dispatcher.add(binder);
        assertNull(updateBean.getSalary());
        dispatcher.dispatch(listenBean, "x");
        assertNull(updateBean.getSalary());
    }

    public void testDispatchMatchesBinderWithNullPropertyName()
    {
        dispatcher.add(binder);
        assertNull(updateBean.getSalary());
        dispatcher.dispatch(listenBean, null);
        assertNotNull(updateBean.getSalary());
    }
}
