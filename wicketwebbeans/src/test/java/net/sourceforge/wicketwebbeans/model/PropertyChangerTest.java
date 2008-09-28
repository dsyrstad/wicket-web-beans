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
 * Tests {@link PropertyChanger}. <p>
 * 
 * @author Dan Syrstad
 */
public class PropertyChangerTest extends TestCase
{
    @Override
    public void setUp()
    {
        PropertyChanger.setCurrent(null);
    }

    public void testGetCurrentCreatesDispatcherAndSetCurrentChangesIt()
    {
        PropertyChangeDispatcher dispatcher = PropertyChanger.getCurrent();
        assertNotNull(dispatcher);
        assertEquals(0, dispatcher.getPropertyBinders().size());

        PropertyChangeDispatcher dispatcher2 = new PropertyChangeDispatcher();
        PropertyChanger.setCurrent(dispatcher2);

        PropertyChangeDispatcher dispatcher3 = PropertyChanger.getCurrent();
        assertSame(dispatcher2, dispatcher3);
        assertNotSame(dispatcher, dispatcher3);
    }

    public void testDispatch()
    {
        PropertyPathBeanCreator beanCreator = new JavaBeansPropertyPathBeanCreator();
        Employee listenBean = new Employee("Dan", null, BigDecimal.valueOf(101.01));
        Employee updateBean = new Employee();
        PropertyProxy listenProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, "salary");
        PropertyProxy updateProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, "salary");
        PropertyBinder binder = new PropertyBinder(listenBean, updateBean, listenProxy, updateProxy);
        PropertyChanger.getCurrent().add(binder);

        assertNull(updateBean.getSalary());
        PropertyChanger.dispatch(listenBean, "salary");
        assertNotNull(updateBean.getSalary());
    }
}
