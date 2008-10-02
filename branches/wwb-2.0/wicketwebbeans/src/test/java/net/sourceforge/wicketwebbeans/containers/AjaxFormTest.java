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

package net.sourceforge.wicketwebbeans.containers;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.BeanFactory;
import net.sourceforge.wicketwebbeans.test.TestUtils;
import net.sourceforge.wicketwebbeans.test.WicketTesterUtils;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Tests {@link AjaxForm}.
 * <p>
 * 
 * @author Dan Syrstad
 */
public class AjaxFormTest extends TestCase
{
    static {
        AjaxFormTest.class.getClassLoader().setDefaultAssertionStatus(true);
    }

    private WicketTester tester = new WicketTester();

    public void testRenderingWithDefaults() throws Exception
    {
        final BeanFactory factory = TestUtils.createBeanFactory("Form { class: AjaxForm; component: Grid; } "
                        + " Grid { class: GridLayout; components: Field, Field, Field; }"
                        + " Field { class: org.apache.wicket.markup.html.form.TextField; }");
        WicketTesterUtils.renderPage(tester, factory, "Form");

        tester.assertComponent("component", AjaxForm.class);
        tester.assertComponent("component:component", WebMarkupContainer.class);
        tester.assertComponent("component:component:c", GridLayout.class);
        for (int i = 1; i <= 3; i++) {
            String path = "component:component:c:r:1:c:" + i + ":frag:c";
            tester.assertComponent(path, TextField.class);
            Component field = tester.getComponentFromLastRenderedPage(path);
            assertEquals(1, field.getBehaviors().size());
            assertTrue(field.getBehaviors().get(0) instanceof AjaxFormComponentUpdatingBehavior);
        }
    }

    public void testMissingComponent() throws Exception
    {
        final BeanFactory factory = TestUtils.createBeanFactory("Form { class: AjaxForm;  } ");

        try {
            WicketTesterUtils.renderPage(tester, factory, "Form");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
        }
    }
}
