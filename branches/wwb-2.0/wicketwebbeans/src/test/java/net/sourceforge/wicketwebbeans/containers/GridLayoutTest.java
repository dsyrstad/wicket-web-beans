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

import org.apache.wicket.markup.html.form.TextField;
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
        WicketTesterUtils.renderPage(tester, factory, "Grid");

        tester.assertComponent("component", GridLayout.class);
        tester.assertComponent("component:r:1:c:1:c:c:c", TextField.class);
        tester.assertComponent("component:r:1:c:2:c:c:c", TextField.class);
        tester.assertComponent("component:r:1:c:3:c:c:c", TextField.class);
        tester.assertComponent("component:r:2:c:1:c:c:c", TextField.class);
    }

    public void testRenderingWithDefaultOverrides() throws Exception
    {
        final BeanFactory factory = TestUtils
                        .createBeanFactory("Grid { class: GridLayout; columns: 4; components: Field, Field, Field, Field, Field { _colspan: 2 }, Field, Field; } "
                                        + " Field { class: org.apache.wicket.markup.html.form.TextField; }");
        WicketTesterUtils.renderPage(tester, factory, "Grid");

        tester.assertComponent("component", GridLayout.class);
        tester.assertComponent("component:r:1:c:1:c:c:c", TextField.class);
        tester.assertComponent("component:r:1:c:2:c:c:c", TextField.class);
        tester.assertComponent("component:r:1:c:3:c:c:c", TextField.class);
        tester.assertComponent("component:r:1:c:4:c:c:c", TextField.class);
        tester.assertComponent("component:r:2:c:1:c:c:c", TextField.class);
        tester.assertComponent("component:r:2:c:2:c:c:c", TextField.class);
        tester.assertComponent("component:r:2:c:3:c:c:c", TextField.class);

        TagTester overriddenTag = tester.getTagByWicketId("component");
        assertEquals(
                        "<div wicket:id=\"component\"><wicket:panel>\n"
                                        + "<div class=\"wwbGridLayout\">\n"
                                        + "<table class=\"wwbGridLayoutTable\">\n"
                                        + "<tbody>\n"
                                        + "  <tr wicket:id=\"r\"><td style=\"width: 25.0%;\" wicket:id=\"c\"><div wicket:id=\"c\"><wicket:panel><input name=\"component:r:1:c:1:c:c:c\" value=\"\" wicket:id=\"c\"/></wicket:panel></div></td><td style=\"width: 25.0%;\" wicket:id=\"c\"><div wicket:id=\"c\"><wicket:panel><input name=\"component:r:1:c:2:c:c:c\" value=\"\" wicket:id=\"c\"/></wicket:panel></div></td><td style=\"width: 25.0%;\" wicket:id=\"c\"><div wicket:id=\"c\"><wicket:panel><input name=\"component:r:1:c:3:c:c:c\" value=\"\" wicket:id=\"c\"/></wicket:panel></div></td><td style=\"width: 25.0%;\" wicket:id=\"c\"><div wicket:id=\"c\"><wicket:panel><input name=\"component:r:1:c:4:c:c:c\" value=\"\" wicket:id=\"c\"/></wicket:panel></div></td></tr><tr wicket:id=\"r\"><td colspan=\"2\" style=\"width: 50.0%;\" wicket:id=\"c\"><div wicket:id=\"c\"><wicket:panel><input name=\"component:r:2:c:1:c:c:c\" value=\"\" wicket:id=\"c\"/></wicket:panel></div></td><td style=\"width: 25.0%;\" wicket:id=\"c\"><div wicket:id=\"c\"><wicket:panel><input name=\"component:r:2:c:2:c:c:c\" value=\"\" wicket:id=\"c\"/></wicket:panel></div></td><td style=\"width: 25.0%;\" wicket:id=\"c\"><div wicket:id=\"c\"><wicket:panel><input name=\"component:r:2:c:3:c:c:c\" value=\"\" wicket:id=\"c\"/></wicket:panel></div></td></tr>\n"
                                        + "</tbody>\n" + "</table>\n" + "</div>\n" + "</wicket:panel></div>",
                        overriddenTag.getMarkup().replaceAll("\t", "    "));
    }

    public void testInvalidColumnsParameter() throws Exception
    {
        final BeanFactory factory = TestUtils
                        .createBeanFactory("Grid { class: GridLayout; columns: 0; components: Field; } Field { class: org.apache.wicket.markup.html.form.TextField; }");
        try {
            factory.newInstance("Grid", "id");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage(), e.getMessage().startsWith("Unable to invoke setter 'setColumns'"));
        }

    }

    public void testInvalidColspanParameter() throws Exception
    {
        final BeanFactory factory = TestUtils
                        .createBeanFactory("Grid { class: GridLayout; components: Field { _colspan: 0 }; } Field { class: org.apache.wicket.markup.html.form.TextField; }");
        try {
            WicketTesterUtils.renderPage(tester, factory, "Grid");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getCause().getMessage().startsWith("Invalid colspan parameter value"));
        }

    }
}
