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

package net.sourceforge.wicketwebbeans.components;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.containers.GridLayoutTest;
import net.sourceforge.wicketwebbeans.test.WicketTesterUtils;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Tests {@link FormComponentWrapper}. <p>
 * 
 * @author Dan Syrstad
 */
public class FormComponentWrapperTest extends TestCase
{
    static {
        GridLayoutTest.class.getClassLoader().setDefaultAssertionStatus(true);
    }

    private WicketTester tester = new WicketTester();

    public void testRender() throws Exception
    {
        Component component = FormComponentWrapper.wrapIfFormComponent(new TextField("component"));
        WicketTesterUtils.renderPage(tester, component);

        tester.assertComponent("component", FormComponentWrapper.class);
        tester.assertComponent("component:c:component", TextField.class);

        TagTester overriddenTag = tester.getTagByWicketId("component");
        assertEquals(
                        "<div wicket:id=\"component\"><wicket:panel><input name=\"component:c:component\" value=\"\" wicket:id=\"c\"/></wicket:panel></div>",
                        overriddenTag.getMarkup());
    }

    public void testWrapIfFormComponentWhenNonFormComponent() throws Exception
    {
        Component component = FormComponentWrapper.wrapIfFormComponent(new Label("component", "test"));
        WicketTesterUtils.renderPage(tester, component);

        tester.assertComponent("component", Label.class);

        TagTester overriddenTag = tester.getTagByWicketId("component");
        assertEquals("<div wicket:id=\"component\">test</div>", overriddenTag.getMarkup());
    }
}
