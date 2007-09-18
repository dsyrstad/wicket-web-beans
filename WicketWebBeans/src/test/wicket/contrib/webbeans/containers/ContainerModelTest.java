/*---
   Copyright 2006-2007 Visual Systems Corporation.
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

package wicket.contrib.webbeans.containers;

import junit.framework.TestCase;
import wicket.Page;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.util.tester.WicketTester;

/**
 * Tests Models with bean containers. <p>
 * 
 * @author Dan Syrstad
 */
public class ContainerModelTest extends TestCase
{
    /*
     * TODO:
     * - Lists from model. Should use BeanTablePanel.
     * - No model, just bean.
     */
    
    /**
     * Construct a ContainerModelTest. 
     *
     * @param name
     */
    public ContainerModelTest(String name)
    {
        super(name);
    }

    /**
     * Tests BeanForm with a LoadableDetachableModel.
     */
    public void testBeanForm()
    {
        WicketTester tester = new WicketTester();

        Page page = tester.startPage(ContainerModelTestPage.class);

        TestLoadableDetachableObjectModel model = new TestLoadableDetachableObjectModel();
        BeanMetaData meta = new BeanMetaData(model.getObject(page).getClass(), null, page, null, false);
        BeanForm form = new BeanForm("beanForm", model, meta);
        page.add(form);
        
        assertTrue(model.isAttached());
        
        page.detachModels();
        
        assertFalse(model.isAttached());
        
        
    }
 
    
    
}
