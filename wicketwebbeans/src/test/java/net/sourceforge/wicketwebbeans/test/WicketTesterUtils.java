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

package net.sourceforge.wicketwebbeans.test;

import net.sourceforge.wicketwebbeans.model.BeanFactory;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.apache.wicket.util.tester.WicketTester;

/**
 * WicketTester utility methods. <p>
 * 
 * @author Dan Syrstad
 */
public class WicketTesterUtils
{
    private WicketTesterUtils()
    {
    }

    /**
     * Renders a {@link TestPage} using the given parameters.  
     *
     * @param tester
     * @param factory the factory containing beanName.
     * @param beanName the name of the bean to be attached to the page.
     */
    @SuppressWarnings("serial")
    public static void renderPage(WicketTester tester, final BeanFactory factory, final String beanName)
    {
        tester.startPage(new ITestPageSource() {
            public Page getTestPage()
            {
                return new TestPage(beanName, factory);
            }
        });
    }

    /**
     * Renders a {@link SingleComponentTestPage} using the given component.  
     *
     * @param tester
     * @param component the component to add to the page.
     */
    @SuppressWarnings("serial")
    public static void renderPage(WicketTester tester, final Component component)
    {
        tester.startPage(new ITestPageSource() {
            public Page getTestPage()
            {
                return new SingleComponentTestPage(component);
            }
        });
    }
}
