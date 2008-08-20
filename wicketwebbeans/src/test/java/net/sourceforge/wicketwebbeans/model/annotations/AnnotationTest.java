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

package net.sourceforge.wicketwebbeans.model.annotations;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;
import net.sourceforge.wicketwebbeans.model.ElementMetaData;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Tests ComponentConfig, ElementMetaData, and TabMetaData from an annotation perspective. <p>
 * 
 * @author Dan Syrstad
 */
public class AnnotationTest extends TestCase
{
    
    /**
     * Construct a AnnotationTest. 
     *
     * @param name
     */
    public AnnotationTest(String name)
    {
        super(name);
    }

    private void assertElement(ComponentConfig beanMetaData, ElementMetaData element, ElementInfo expected)
    {
        assertEquals(expected.propName, expected.propName, element.getPropertyName());
        assertEquals(expected.propName, expected.propName.startsWith("action."), element.isAction());
        assertSame(beanMetaData, element.getComponentConfig());
        if (element.isAction()) {
            assertEquals(expected.propName, expected.propName.substring(expected.propName.indexOf('.') + 1) , element.getActionMethodName());
        }
        else {
            assertNull(expected.propName, element.getActionMethodName());
        }
        
        assertEquals(expected.propName, expected.elementType, element.getElementTypeName() );
        assertEquals(expected.propName, expected.fieldType, element.getFieldType() );
        assertEquals(expected.propName, expected.label, element.getLabel());
        assertNotNull(expected.propName, element.getLabelComponent("test"));
        assertEquals(expected.propName, expected.labelImage, element.getLabelImage());
    }
    
    /**
     * Tests basic Annotations
     */
    public void testBasicAnnotations()
    {
        WicketTester tester = new WicketTester();
        tester.startPage(AnnotationTestPage.class);
        Page page = tester.getLastRenderedPage();
        
        ComponentConfig beanMetaData = new ComponentConfig(AnnotationTestBean.class, null, page, null);
        // Test that ${} substitution works and that the properties file is referenced.
        assertEquals("My Experiment Title", beanMetaData.getLabel() );
        
        ElementInfo[] expectedProps = { 
            new ElementInfo("action.save", null, null, "Save", null),
            new ElementInfo("result", null, null, "Result", null),
            new ElementInfo("firstName", null, null, "First Name", null),
            new ElementInfo("palette", null, "net.sourceforge.wicketwebbeans.model.annotations.AnnotationTestBean$ColorEnum", "Palette", null),
            new ElementInfo("lastName", null, null, "Last Name", null),
            new ElementInfo("palette2", null, "net.sourceforge.wicketwebbeans.model.annotations.AnnotationTestBean$ColorEnum", "Palette 2", null),
            new ElementInfo("description", "net.sourceforge.wicketwebbeans.fields.TextAreaField", null, "Description", null),
            new ElementInfo("EMPTY:28", "net.sourceforge.wicketwebbeans.fields.EmptyField", null, "", null),
            new ElementInfo("activePrimitive", null, null, "Active Primitive", null),
            new ElementInfo("beans", null, null, "Beans", null),
            new ElementInfo("color", null, null, "Color", null),
            new ElementInfo("inlineBean", "net.sourceforge.wicketwebbeans.fields.BeanInlineField", null, "Inline Bean", null),
            new ElementInfo("dateTimestamp", null, null, "Date Timestamp", null),
            new ElementInfo("blockBean", "net.sourceforge.wicketwebbeans.fields.BeanGridField", null, "Block Bean", null),
            new ElementInfo("testBean2", null, null, "Test Bean 2", null),
            new ElementInfo("popupBean", null, null, "Popup Bean", null),
            new ElementInfo("action.addRow", null, null, "Add Row", null),
            new ElementInfo("action.cancel", null, null, "Cancel", null),
            new ElementInfo("action.doIt", null, null, "Do It", null),
            new ElementInfo("age", null, null, "Age", null),
            new ElementInfo("dateOnly", null, null, "Date Only", null),
            new ElementInfo("gender", null, null, "Gender", null),
            new ElementInfo("isActive", null, null, "Is Active", null),
            new ElementInfo("operand1", null, null, "Operand 1", null),
            new ElementInfo("operand2", null, null, "Operand 2", null),
            new ElementInfo("savingsAmount", null, null, "Savings Amount", null),
            new ElementInfo("startDate", null, null, "Start Date", null),
            new ElementInfo("timeOnly", null, null, "Time Only", null),
        };

        assertEquals(expectedProps.length, beanMetaData.getDisplayedElements().size());
        
        int elementIdx = 0;
        for (ElementMetaData element : beanMetaData.getDisplayedElements()) {
            //System.out.println("new ElementInfo(\"" + element.getPropertyName() + "\", " + element.isViewOnly() + ", \"" + element.getFieldType() + "\", \"" + element.getElementTypeName() + "\", \"" + element.getLabel() + "\", \"" + element.getLabelImage() + "\"),");
            assertElement(beanMetaData, element, expectedProps[elementIdx]);
            ++elementIdx;
        }
        
        // action.save (first element) should have a parameter of colspan: 3
        assertEquals(Integer.valueOf(3), beanMetaData.getDisplayedElements().get(0).getIntegerParameter("colspan"));
    }
    
    /**
     * Tests a context. Implicitly extends the default context.
     */
    public void testContext()
    {
        WicketTester tester = new WicketTester();
        tester.startPage(AnnotationTestPage.class);
        Page page = tester.getLastRenderedPage();
        
        // Use the "view" context
        ComponentConfig beanMetaData = new ComponentConfig(AnnotationTestBean.class, "view", page, null);
        assertEquals("Bean View", beanMetaData.getLabel() );
        
        ElementInfo[] expectedProps = { 
            new ElementInfo("action.save", null, null, "Save", null),
            new ElementInfo("result", null, null, "Result", null),
            new ElementInfo("firstName", null, null, "First Name", null),
            new ElementInfo("palette", null, "net.sourceforge.wicketwebbeans.model.annotations.AnnotationTestBean$ColorEnum", "Palette", null),
            new ElementInfo("lastName", null, null, "Last Name", null),
            new ElementInfo("palette2", null, "net.sourceforge.wicketwebbeans.model.annotations.AnnotationTestBean$ColorEnum", "Palette 2", null),
            new ElementInfo("description", "net.sourceforge.wicketwebbeans.fields.TextAreaField", null, "Description", null),
            new ElementInfo("EMPTY:28", "net.sourceforge.wicketwebbeans.fields.EmptyField", null, "", null),
            new ElementInfo("activePrimitive", null, null, "Active Primitive", null),
            new ElementInfo("beans", null, null, "Beans", null),
            new ElementInfo("color", null, null, "Color", null),
            new ElementInfo("inlineBean", "net.sourceforge.wicketwebbeans.fields.BeanInlineField", null, "Inline Bean", null),
            new ElementInfo("dateTimestamp", null, null, "Date Timestamp", null),
            new ElementInfo("blockBean", "net.sourceforge.wicketwebbeans.fields.BeanGridField", null, "Block Bean", null),
            new ElementInfo("testBean2", null, null, "Test Bean 2", null),
            new ElementInfo("popupBean", null, null, "Popup Bean", null),
            new ElementInfo("action.addRow", null, null, "Add Row", null),
            new ElementInfo("action.cancel", null, null, "Cancel", null),
            new ElementInfo("action.doIt", null, null, "Do It", null),
            new ElementInfo("age", null, null, "Age", null),
            new ElementInfo("dateOnly", null, null, "Date Only", null),
            new ElementInfo("gender", null, null, "Gender", null),
            new ElementInfo("isActive", null, null, "Is Active", null),
            new ElementInfo("operand1", null, null, "Operand 1", null),
            new ElementInfo("operand2", null, null, "Operand 2", null),
            new ElementInfo("savingsAmount", null, null, "Savings Amount", null),
            new ElementInfo("startDate", null, null, "Start Date", null),
            new ElementInfo("timeOnly", null, null, "Time Only", null),
        };

        assertEquals(expectedProps.length, beanMetaData.getDisplayedElements().size());
        
        int elementIdx = 0;
        for (ElementMetaData element : beanMetaData.getDisplayedElements()) {
            //System.out.println("new ElementInfo(\"" + element.getPropertyName() + "\", " + element.isViewOnly() + ", \"" + element.getFieldType() + "\", \"" + element.getElementTypeName() + "\", \"" + element.getLabel() + "\", \"" + element.getLabelImage() + "\"),");
            assertElement(beanMetaData, element, expectedProps[elementIdx]);
            ++elementIdx;
        }
        
        // action.save should have a parameter of colspan: 4
        assertEquals(Integer.valueOf(4), beanMetaData.getDisplayedElements().get(0).getIntegerParameter("colspan"));
    }
    
    private static final class ElementInfo
    {
        String propName;
        String fieldType;
        String elementType;
        String label;
        String labelImage;
 
        ElementInfo(String propName, String fieldType, String elementType, String label, String labelImage)
        {
            this.propName = propName;
            this.fieldType = fieldType;
            this.elementType = elementType;
            this.label = label;
            this.labelImage = labelImage;
        }
    }
}
