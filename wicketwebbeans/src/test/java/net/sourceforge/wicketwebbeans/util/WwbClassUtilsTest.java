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

package net.sourceforge.wicketwebbeans.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Date;

import junit.framework.TestCase;

/**
 * Tests WwbClassUtils. <p>
 * 
 * @author Dan Syrstad
 */
public class WwbClassUtilsTest extends TestCase
{
    static {
        WwbClassUtilsTest.class.getClassLoader().setDefaultAssertionStatus(true);
    }

    public void testFindMostSpecificMethodUsingArgTypes() throws Exception
    {
        // Exact match.
        Method method = WwbClassUtils.findMostSpecificMethod(TestClass.class, "methodWith3Args", String.class,
                        Integer.TYPE, Object.class);
        assertEquals("methodWith3Args", method.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(Integer.TYPE, method.getParameterTypes()[1]);
        assertEquals(Object.class, method.getParameterTypes()[2]);

        // Exact match.
        method = WwbClassUtils.findMostSpecificMethod(TestClass.class, "methodWith3Args", String.class, Integer.class,
                        Object.class);
        assertEquals("methodWith3Args", method.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(Integer.class, method.getParameterTypes()[1]);
        assertEquals(Object.class, method.getParameterTypes()[2]);

        // Exact match.
        method = WwbClassUtils.findMostSpecificMethod(TestClass.class, "methodWith3Args", String.class, Integer.class,
                        TestClass.class);
        assertEquals("methodWith3Args", method.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(Integer.class, method.getParameterTypes()[1]);
        assertEquals(TestClass.class, method.getParameterTypes()[2]);

        // Inexact match - also tests most specific match.
        method = WwbClassUtils.findMostSpecificMethod(TestClass.class, "methodWith3Args", String.class, Short.TYPE,
                        Date.class);
        assertEquals("methodWith3Args", method.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(Short.class, method.getParameterTypes()[1]);
        assertEquals(Date.class, method.getParameterTypes()[2]);

        // Test null args can be used on object type arguments
        method = WwbClassUtils.findMostSpecificMethod(TestClass.class, "methodWith3Args", String.class, null, null);
        assertEquals("methodWith3Args", method.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(Integer.class, method.getParameterTypes()[1]);
        assertEquals(Object.class, method.getParameterTypes()[2]);

        method = WwbClassUtils.findMostSpecificMethod(TestClass.class, "methodWith3Args", String.class, null,
                        Object.class);
        assertEquals("methodWith3Args", method.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(Integer.class, method.getParameterTypes()[1]);
        assertEquals(Object.class, method.getParameterTypes()[2]);
    }

    public void testFindMostSpecificMethodUsingArgs() throws Exception
    {
        // Exact match.
        Method method = WwbClassUtils.findMostSpecificMethod(TestClass.class, "methodWith3Args", "x", (long)3,
                        new Object());
        assertEquals("methodWith3Args", method.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(Long.TYPE, method.getParameterTypes()[1]);
        assertEquals(Object.class, method.getParameterTypes()[2]);

        // Exact match.
        method = WwbClassUtils.findMostSpecificMethod(TestClass.class, "methodWith3Args", "x", Integer.valueOf(3),
                        new Object());
        assertEquals("methodWith3Args", method.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(Integer.class, method.getParameterTypes()[1]);
        assertEquals(Object.class, method.getParameterTypes()[2]);

        // Inexact match - also tests most specific match.
        method = WwbClassUtils.findMostSpecificMethod(TestClass.class, "methodWith3Args", "x", Short.valueOf((short)3),
                        new java.sql.Date(0));
        assertEquals("methodWith3Args", method.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(Short.class, method.getParameterTypes()[1]);
        assertEquals(Date.class, method.getParameterTypes()[2]);

        // Test null args can be used on object type arguments
        method = WwbClassUtils.findMostSpecificMethod(TestClass.class, "methodWith3Args", "X", null, null);
        assertEquals("methodWith3Args", method.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(Integer.class, method.getParameterTypes()[1]);
        assertEquals(Object.class, method.getParameterTypes()[2]);

        method = WwbClassUtils.findMostSpecificMethod(TestClass.class, "methodWith3Args", "X", null, new Date());
        assertEquals("methodWith3Args", method.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(Short.class, method.getParameterTypes()[1]);
        assertEquals(Date.class, method.getParameterTypes()[2]);
    }

    public void testFindMostSpecificConstructorUsingArgTypes() throws Exception
    {
        // Exact match.
        Constructor<?> constructor = WwbClassUtils.findMostSpecificConstructor(TestClass.class, String.class,
                        Integer.TYPE, Object.class);
        assertEquals(String.class, constructor.getParameterTypes()[0]);
        assertEquals(Integer.TYPE, constructor.getParameterTypes()[1]);
        assertEquals(Object.class, constructor.getParameterTypes()[2]);

        // Exact match.
        constructor = WwbClassUtils.findMostSpecificConstructor(TestClass.class, String.class, Integer.class,
                        Object.class);
        assertEquals(String.class, constructor.getParameterTypes()[0]);
        assertEquals(Integer.class, constructor.getParameterTypes()[1]);
        assertEquals(Object.class, constructor.getParameterTypes()[2]);

        // Exact match.
        constructor = WwbClassUtils.findMostSpecificConstructor(TestClass.class, String.class, Integer.class,
                        TestClass.class);
        assertEquals(String.class, constructor.getParameterTypes()[0]);
        assertEquals(Integer.class, constructor.getParameterTypes()[1]);
        assertEquals(TestClass.class, constructor.getParameterTypes()[2]);

        // Inexact match - also tests most specific match.
        constructor = WwbClassUtils.findMostSpecificConstructor(TestClass.class, String.class, Short.TYPE, Date.class);
        assertEquals(String.class, constructor.getParameterTypes()[0]);
        assertEquals(Short.class, constructor.getParameterTypes()[1]);
        assertEquals(Date.class, constructor.getParameterTypes()[2]);

        // Test null args can be used on object type arguments
        constructor = WwbClassUtils.findMostSpecificConstructor(TestClass.class, String.class, null, null);
        assertEquals(String.class, constructor.getParameterTypes()[0]);
        assertEquals(Integer.class, constructor.getParameterTypes()[1]);
        assertEquals(Object.class, constructor.getParameterTypes()[2]);

        constructor = WwbClassUtils.findMostSpecificConstructor(TestClass.class, String.class, null, Object.class);
        assertEquals(String.class, constructor.getParameterTypes()[0]);
        assertEquals(Integer.class, constructor.getParameterTypes()[1]);
        assertEquals(Object.class, constructor.getParameterTypes()[2]);
    }

    public void testFindMostSpecificConstructorUsingArgs() throws Exception
    {
        // Exact match.
        Constructor<?> constructor = WwbClassUtils.findMostSpecificConstructor(TestClass.class, "x", (long)3,
                        new Object());
        assertEquals(String.class, constructor.getParameterTypes()[0]);
        assertEquals(Long.TYPE, constructor.getParameterTypes()[1]);
        assertEquals(Object.class, constructor.getParameterTypes()[2]);

        // Exact match.
        constructor = WwbClassUtils.findMostSpecificConstructor(TestClass.class, "x", Integer.valueOf(3), new Object());
        assertEquals(String.class, constructor.getParameterTypes()[0]);
        assertEquals(Integer.class, constructor.getParameterTypes()[1]);
        assertEquals(Object.class, constructor.getParameterTypes()[2]);

        // Inexact match - also tests most specific match.
        constructor = WwbClassUtils.findMostSpecificConstructor(TestClass.class, "x", Short.valueOf((short)3),
                        new java.sql.Date(0));
        assertEquals(String.class, constructor.getParameterTypes()[0]);
        assertEquals(Short.class, constructor.getParameterTypes()[1]);
        assertEquals(Date.class, constructor.getParameterTypes()[2]);

        // Test null args can be used on object type arguments
        constructor = WwbClassUtils.findMostSpecificConstructor(TestClass.class, "X", null, null);
        assertEquals(String.class, constructor.getParameterTypes()[0]);
        assertEquals(Integer.class, constructor.getParameterTypes()[1]);
        assertEquals(Object.class, constructor.getParameterTypes()[2]);

        constructor = WwbClassUtils.findMostSpecificConstructor(TestClass.class, "X", null, new Date());
        assertEquals(String.class, constructor.getParameterTypes()[0]);
        assertEquals(Short.class, constructor.getParameterTypes()[1]);
        assertEquals(Date.class, constructor.getParameterTypes()[2]);
    }

    public void testInvokeMostSpecificConstructorUsingArgs() throws Exception
    {
        // Exact match.
        assertTrue(WwbClassUtils.invokeMostSpecificConstructor(TestClass.class, "x", (long)3, new Object()) instanceof TestClass);

        // Exact match.
        assertTrue(WwbClassUtils.invokeMostSpecificConstructor(TestClass.class, "x", Integer.valueOf(3), new Object()) instanceof TestClass);

        // Inexact match - also tests most specific match.
        assertTrue(WwbClassUtils.invokeMostSpecificConstructor(TestClass.class, "x", Short.valueOf((short)3),
                        new java.sql.Date(0)) instanceof TestClass);

        // Test null args can be used on object type arguments
        assertTrue(WwbClassUtils.invokeMostSpecificConstructor(TestClass.class, "X", null, null) instanceof TestClass);
        assertTrue(WwbClassUtils.invokeMostSpecificConstructor(TestClass.class, "X", null, new Date()) instanceof TestClass);
    }

    public void testInvokeMostSpecificConstructorUsingInvalidArgs() throws Exception
    {
        try {
            WwbClassUtils.invokeMostSpecificConstructor(TestClass.class, "x", (long)3);
            fail();
        }
        catch (Exception e) {
            // Expected
        }
    }


    private static class TestClass
    {
        public TestClass(String s) // Not 3 args, but makes sure we don't match this one.
        {
        }

        public TestClass(String s, Integer v, Object obj)
        {
        }

        public TestClass(String s, int v, Object obj)
        {
        }

        public TestClass(String s, long v, Object obj)
        {
        }

        public TestClass(String s, Integer v, TestClass obj)
        {
        }

        public TestClass(String s, Short v, Date date)
        {
        }

        public void methodWith3Args(String s) // Not 3 args, but makes sure we don't match this one.
        {
        }

        public void methodWith3Args(String s, Integer v, Object obj)
        {
        }

        public void methodWith3Args(String s, int v, Object obj)
        {
        }

        public void methodWith3Args(String s, long v, Object obj)
        {
        }

        public void methodWith3Args(String s, Integer v, TestClass obj)
        {
        }

        public void methodWith3Args(String s, Short v, Date date)
        {
        }

        public void differentMethodWith3Args(String s, Integer v, Object obj) // Not same name, but same args.
        {
        }
    }
}
