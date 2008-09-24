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

package net.sourceforge.wicketwebbeans.model.jxpath;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.JavaBeansPropertyPathBeanCreator;
import net.sourceforge.wicketwebbeans.model.TestBean;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

/**
 * Tests JXPathObjectFactory. <p>
 * 
 * @author Dan Syrstad
 */
public class JXPathObjectFactoryTest extends TestCase
{
    private JXPathObjectFactory objectFactory = new JXPathObjectFactory(new JavaBeansPropertyPathBeanCreator());
    private TestBean bean = new TestBean();
    private JXPathContext context = JXPathContext.newContext(bean);
    private Pointer pointer = context.getPointer("nestedBean");

    public void testCreateObject()
    {
        boolean result = objectFactory.createObject(context, pointer, bean, "nestedBean", 0);
        assertTrue(result);
        assertNotNull(bean.getNestedBean());
    }
}
