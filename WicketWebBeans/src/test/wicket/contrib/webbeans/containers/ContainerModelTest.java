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
import wicket.Component;
import wicket.Page;
import wicket.contrib.webbeans.fields.InputField;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.contrib.webbeans.model.BeanPropertyModel;
import wicket.util.tester.ITestPageSource;
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
     * Tests BeanForm with a LoadableDetachableModel instead of a direct bean.
     */
    public void testBeanFormWithLoadableDetachableModel()
    {
        WicketTester tester = new WicketTester();

        final ContainerModelTestPage page = new ContainerModelTestPage();
        

        TestLoadableDetachableObjectModel nestedModel = new TestLoadableDetachableObjectModel();
        BeanMetaData meta = new BeanMetaData(nestedModel.getObject(page).getClass(), null, page, null, false);
        BeanForm form = new BeanForm("beanForm", nestedModel, meta);

        page.add(form);
        
        tester.startPage(new ITestPageSource() {
            public Page getTestPage()
            {
                return page;
            }
        });
        
        //tester.debugComponentTrees();

        // Check elements, labels.
        String firstRowPath = "beanForm:f:tabs:r:0";
        String namePath = firstRowPath + ":c:0:c";
        String nameFieldPath = namePath + ":c";
        
        tester.assertLabel(namePath + ":l", "Name");
        tester.assertComponent(nameFieldPath, InputField.class);
        Component nameField = tester.getComponentFromLastRenderedPage(nameFieldPath);

        String serialNumPath = firstRowPath + ":c:1:c";
        String serialNumFieldPath = serialNumPath + ":c";
        tester.assertLabel(serialNumPath + ":l", "Serial Number");
        tester.assertComponent(serialNumFieldPath, InputField.class);
        Component serialNumField = tester.getComponentFromLastRenderedPage(serialNumFieldPath);
        
        // Check attaching/detaching component's model (BeanPropertyModel).
        BeanPropertyModel nameFieldModel = (BeanPropertyModel)nameField.getModel();
        
        assertFalse(nestedModel.isAttached());

        // Should attach the nested model's object.
        nameFieldModel.getObject(form);
        
        assertTrue(nestedModel.isAttached());
        
        NonSerializableBean firstBean = (NonSerializableBean)nestedModel.getObject(null); 
        
        // Make the first bean detach. This also tests that the model is attached somewhere below the page.
        page.detachModels();
        
        assertFalse(nestedModel.isAttached());
        
        NonSerializableBean secondBean = (NonSerializableBean)nestedModel.getObject(null); 

        // Should be different and attached now.
        assertNotSame(firstBean, secondBean);
        assertTrue(nestedModel.isAttached());
        
        // Assert PropertyChangeListener on BeanForm is called.
        assertFalse( form.isComponentRefreshNeeded() );
        nameFieldModel.setObject(nameField, "test");
        assertTrue( form.isComponentRefreshNeeded() );

        // Clear the refresh components.
        form.clearRefreshComponents();
        
        // Assert PropertyChangeListener on BeanForm is called after detach()/attach().
        page.detachModels();
        assertFalse(nestedModel.isAttached());
        
        assertFalse( form.isComponentRefreshNeeded() );
        nameFieldModel.setObject(nameField, "test");
        assertTrue( form.isComponentRefreshNeeded() );

        // Clear the refresh components.
        form.clearRefreshComponents();
    }
}
