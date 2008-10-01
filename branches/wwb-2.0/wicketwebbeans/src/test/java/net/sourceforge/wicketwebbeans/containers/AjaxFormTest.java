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

import org.apache.wicket.markup.html.panel.Panel;
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
                        + " Grid { class: GridLayout; }");
        WicketTesterUtils.renderPage(tester, factory, "Form");

        tester.assertComponent("component", AjaxForm.class);
        tester.assertComponent("component:c", Panel.class);

    }
}
