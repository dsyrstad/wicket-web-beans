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

import org.apache.wicket.util.tester.WicketTester;

/**
 * Tests {@link SessionConvertUtils}. <p>
 * 
 * @author Dan Syrstad
 */
public class SessionConvertUtilsTest extends TestCase
{
    public void testGetCurrentCreatesDispatcherAndSetCurrentChangesIt()
    {
        @SuppressWarnings("unused")
        WicketTester tester = new WicketTester();
        SessionConvertUtils convertUtils = SessionConvertUtils.getCurrent();
        assertNotNull(convertUtils);

        SessionConvertUtils convertUtils2 = new SessionConvertUtils();
        SessionConvertUtils.setCurrent(convertUtils2);

        SessionConvertUtils convertUtils3 = SessionConvertUtils.getCurrent();
        assertSame(convertUtils2, convertUtils3);
        assertNotSame(convertUtils, convertUtils3);
    }

    public void testCallingSessionConvertUtilsWithoutRequestCycle()
    {
        SessionConvertUtils convertUtils = SessionConvertUtils.getCurrent();
        assertNotNull(convertUtils);

        // Now we'll have a session
        @SuppressWarnings("unused")
        WicketTester tester = new WicketTester();
        SessionConvertUtils convertUtils2 = SessionConvertUtils.getCurrent();
        assertNotSame(convertUtils, convertUtils2);
    }
}
