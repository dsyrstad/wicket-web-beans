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

/**
 * Tests JavaBeansPropertyPathCreator. <p>
 * 
 * @author Dan Syrstad
 */
public class JavaBeansPropetyPathTest extends TestCase
{
    public void testCreateObject()
    {
        TestBean bean = (TestBean)new JavaBeansPropertyPathBeanCreator().createBean(TestBean.class);
        assertNotNull(bean);
    }

    public void testCreateObjectWithoutNoArgConstructor()
    {
        try {
            new JavaBeansPropertyPathBeanCreator().createBean(MissingNoArgConstructorBean.class);
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertEquals("Error creating bean", e.getMessage());
            assertTrue(e.getCause() instanceof NoSuchMethodException);
        }
    }


    public static final class MissingNoArgConstructorBean
    {
        public MissingNoArgConstructorBean(int x)
        {
        }
    }
}
