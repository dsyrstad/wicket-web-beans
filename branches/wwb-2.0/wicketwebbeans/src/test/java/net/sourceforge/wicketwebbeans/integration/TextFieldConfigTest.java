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

package net.sourceforge.wicketwebbeans.integration;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.containers.GridLayout;
import net.sourceforge.wicketwebbeans.model.BeanFactory;
import net.sourceforge.wicketwebbeans.test.TestPage;
import net.sourceforge.wicketwebbeans.test.TestUtils;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.ITestPageSource;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Tests creating a Page with a simple configuration.
 * <p>
 * 
 * @author Dan Syrstad
 */
public class TextFieldConfigTest extends TestCase
{
    static {
        TextFieldConfigTest.class.getClassLoader().setDefaultAssertionStatus(true);
    }

    @SuppressWarnings("serial")
    public void testGridLayout() throws Exception
    {
        final BeanFactory factory = TestUtils
                        .createBeanFactory("Grid { class: GridLayout; components: Field, Field, Field, Field; } Field { class: org.apache.wicket.markup.html.form.TextField; }");
        WicketTester tester = new WicketTester();
        tester.startPage(new ITestPageSource() {
            public Page getTestPage()
            {
                return new TestPage("Grid", factory);
            }
        });

        tester.assertComponent("component", GridLayout.class);
        tester.assertComponent("component:r:0:c:0:frag:c", TextField.class);
        tester.assertComponent("component:r:0:c:1:frag:c", TextField.class);
        tester.assertComponent("component:r:0:c:2:frag:c", TextField.class);
        tester.assertComponent("component:r:1:c:0:frag:c", TextField.class);
    }
}