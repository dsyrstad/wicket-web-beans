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

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.jxpath.JXPathPropertyResolver;
import net.sourceforge.wicketwebbeans.test.AjaxTestPage;
import net.sourceforge.wicketwebbeans.test.Employee;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.tester.ITestPageSource;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Tests {@link AjaxPropertyBinder}. <p>
 * 
 * @author Dan Syrstad
 */
public class AjaxPropertyBinderTest extends TestCase
{
    private WicketTester tester = new WicketTester();
    private PropertyPathBeanCreator beanCreator = new JavaBeansPropertyPathBeanCreator();
    private Employee listenBean = new Employee("test", null, null);
    private PropertyProxy listenProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, "name");
    private PropertyProxy updateProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, "markupId");
    private PropertyBinder binder;
    @SuppressWarnings("serial")
    private AjaxLink link = new AjaxLink("link") {
        @Override
        public void onClick(AjaxRequestTarget target)
        {
            binder.updateProperty();
        }
    };

    public void testUpdatePropertyUpdatesWhenNoAjaxTarget()
    {
        Component updateBean = new Label("id");
        binder = new AjaxPropertyBinder(listenBean, updateBean, listenProxy, updateProxy);

        assertFalse("test".equals(updateBean.getMarkupId()));
        binder.updateProperty();
        assertEquals("test", updateBean.getMarkupId());
        assertComponentNotOnAjaxResponse(updateBean);
    }

    public void testUpdatePropertyUpdatesAjaxTarget()
    {
        binder = new AjaxPropertyBinder(listenBean, link, listenProxy, updateProxy);
        performAjaxRequestCycle(link);

        tester.assertComponentOnAjaxResponse(link);
        assertEquals("test", link.getMarkupId());
    }

    public void testUpdatePropertyUpdatesWhenUpdateBeanNotAComponent()
    {
        Employee updateBean = new Employee();
        updateProxy = new JXPathPropertyResolver().createPropertyProxy(beanCreator, "name");
        binder = new AjaxPropertyBinder(listenBean, updateBean, listenProxy, updateProxy);
        performAjaxRequestCycle(link);

        assertComponentNotOnAjaxResponse(link);
        assertFalse("test".equals(link.getMarkupId()));
        assertEquals("test", updateBean.getName());
    }

    @SuppressWarnings("serial")
    private void performAjaxRequestCycle(AjaxLink link)
    {
        AjaxTestPage.ajaxLink = link;
        tester.startPage(new ITestPageSource() {
            public Page getTestPage()
            {
                return new AjaxTestPage();
            }
        });
        tester.clickLink("link", true);
    }

    private void assertComponentNotOnAjaxResponse(Component component)
    {
        assertTrue(tester.isComponentOnAjaxResponse(component).wasFailed());
    }
}
