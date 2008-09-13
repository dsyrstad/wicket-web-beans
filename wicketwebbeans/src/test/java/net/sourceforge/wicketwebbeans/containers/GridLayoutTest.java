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
import net.sourceforge.wicketwebbeans.test.TestPage;
import net.sourceforge.wicketwebbeans.test.TestUtils;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.ITestPageSource;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Tests GridLayout.
 * <p>
 * 
 * @author Dan Syrstad
 */
public class GridLayoutTest extends TestCase
{
    static {
        GridLayoutTest.class.getClassLoader().setDefaultAssertionStatus(true);
    }

    private WicketTester tester = new WicketTester();

    public void testRenderingWithDefault() throws Exception
    {
        final BeanFactory factory = TestUtils
                        .createBeanFactory("Grid { class: GridLayout; components: Field, Field, Field, Field; } "
                                        + " Field { class: org.apache.wicket.markup.html.form.TextField; }");
        renderPage(factory);

        tester.assertComponent("component", GridLayout.class);
        tester.assertComponent("component:r:0:c:0:frag:c", TextField.class);
        tester.assertComponent("component:r:0:c:1:frag:c", TextField.class);
        tester.assertComponent("component:r:0:c:2:frag:c", TextField.class);
        tester.assertComponent("component:r:1:c:0:frag:c", TextField.class);
    }

    public void testRenderingWithDefaultOverrides() throws Exception
    {
        final BeanFactory factory = TestUtils
                        .createBeanFactory("Grid { class: GridLayout; columns: 4; components: Field, Field, Field, Field, Field { _colspan: 2 }, Field, Field; } "
                                        + " Field { class: org.apache.wicket.markup.html.form.TextField; }");
        renderPage(factory);

        tester.assertComponent("component", GridLayout.class);
        tester.assertComponent("component:r:0:c:0:frag:c", TextField.class);
        tester.assertComponent("component:r:0:c:1:frag:c", TextField.class);
        tester.assertComponent("component:r:0:c:2:frag:c", TextField.class);
        tester.assertComponent("component:r:0:c:3:frag:c", TextField.class);
        tester.assertComponent("component:r:1:c:0:frag:c", TextField.class);
        tester.assertComponent("component:r:1:c:1:frag:c", TextField.class);
        tester.assertComponent("component:r:1:c:2:frag:c", TextField.class);

        TagTester overriddenTag = tester.getTagById("component:r:1:c:0:frag:c");
        // These are on td
        assertEquals("2", overriddenTag.getAttribute("colspan"));
        assertEquals("width: 50.0%", overriddenTag.getAttribute("style"));
    }

    // TODO bad columns, bad colspan

    @SuppressWarnings("serial")
    private void renderPage(final BeanFactory factory)
    {
        tester.startPage(new ITestPageSource() {
            public Page getTestPage()
            {
                return new TestPage("Grid", factory);
            }
        });
    }
}
