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

package net.sourceforge.wicketwebbeans.model.beanprops;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import net.sourceforge.wicketwebbeans.model.ElementMetaData;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Tests BeanMetaData, ElementMetaData, TabMetaData, and BeanPropsParser. <p>
 * 
 * @author Dan Syrstad
 */
public class BeanMetaDataTest extends TestCase
{
    
    /**
     * Construct a AnnotationTest. 
     *
     * @param name
     */
    public BeanMetaDataTest(String name)
    {
        super(name);
    }

    private void assertElement(BeanMetaData beanMetaData, ElementMetaData element, ElementInfo expected)
    {
        assertEquals(expected.propName, expected.propName, element.getPropertyName());
        assertEquals(expected.propName, expected.viewOnly, element.isViewOnly());
        assertEquals(expected.propName, expected.propName.startsWith("action."), element.isAction());
        assertSame(beanMetaData, element.getBeanMetaData());
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
     * Tests basic parsing and info.
     */
    public void testBasicParsing()
    {
        WicketTester tester = new WicketTester();
        tester.startPage(BeanMetaDataTestPage.class);
        Page page = tester.getLastRenderedPage();
        
        BeanMetaData beanMetaData = new BeanMetaData(BeanMetaDataTestBean.class, null, page, null, false);
        // Check parameters, elements, element parameters
        assertFalse( beanMetaData.isViewOnly() );
        assertTrue( beanMetaData.isDisplayed() );
        // Test that ${} substitution works and that the properties file is referenced.
        assertEquals("My Experiment Title", beanMetaData.getLabel() );
        
        ElementInfo[] expectedProps = { 
            new ElementInfo("action.save", false, null, null, "Save", null),
            new ElementInfo("firstName", false, null, null, "First Name", null),
            new ElementInfo("lastName", false, null, null, "Last Name", null),
            new ElementInfo("EMPTY:28", true, "net.sourceforge.wicketwebbeans.fields.EmptyField", null, "", null),
            new ElementInfo("activePrimitive", false, null, null, "Active Primitive", null),
            new ElementInfo("color", false, null, null, "Color", null),
            new ElementInfo("inlineBean", false, "net.sourceforge.wicketwebbeans.fields.BeanInlineField", null, "Inline Bean", null),
            new ElementInfo("dateTimestamp", false, null, null, "Date Timestamp", null),
            new ElementInfo("blockBean", false, "net.sourceforge.wicketwebbeans.fields.BeanGridField", null, "Block Bean", null),
            new ElementInfo("testBean2", false, null, null, "Test Bean 2", null),
            new ElementInfo("popupBean", false, null, null, "Popup Bean", null),
            new ElementInfo("action.addRow", false, null, null, "Add Row", null),
            new ElementInfo("action.cancel", false, null, null, "Cancel", null),
            new ElementInfo("action.doIt", false, null, null, "Do It", null),
            new ElementInfo("age", false, null, null, "Age", null),
            new ElementInfo("beans", false, null, null, "Beans", null),
            new ElementInfo("dateOnly", false, null, null, "Date Only", null),
            new ElementInfo("description", false, null, null, "Description", null),
            new ElementInfo("gender", false, null, null, "Gender", null),
            new ElementInfo("isActive", false, null, null, "Is Active", null),
            new ElementInfo("operand1", false, null, null, "Operand 1", null),
            new ElementInfo("operand2", false, null, null, "Operand 2", null),
            new ElementInfo("palette", false, null, null, "Palette", null),
            new ElementInfo("palette2", true, null, null, "Palette 2", null),
            new ElementInfo("result", true, null, null, "Result", null),
            new ElementInfo("savingsAmount", false, null, null, "Savings Amount", null),
            new ElementInfo("startDate", false, null, null, "Start Date", null),
            new ElementInfo("timeOnly", false, null, null, "Time Only", null),
        };

        //assertEquals(expectedProps.length, beanMetaData.getDisplayedElements().size());
        
        int elementIdx = 0;
        for (ElementMetaData element : beanMetaData.getDisplayedElements()) {
            //System.out.println("new ElementInfo(\"" + element.getPropertyName() + "\", " + element.isViewOnly() + ", \"" + element.getFieldType() + "\", \"" + element.getElementTypeName() + "\", \"" + element.getLabel() + "\", \"" + element.getLabelImage() + "\")");
            assertElement(beanMetaData, element, expectedProps[elementIdx]);
            ++elementIdx;
        }
        
        // action.save should have a parameter of colspan: 3
        assertEquals(Integer.valueOf(3), beanMetaData.getDisplayedElements().get(0).getIntegerParameter("colspan"));
    }
    
    /**
     * Tests that defaults, with no beanprops, works.
     */
    public void testDefaults()
    {
        WicketTester tester = new WicketTester();
        tester.startPage(BeanMetaDataTestNoPropsPage.class);
        Page page = tester.getLastRenderedPage();
        
        BeanMetaData beanMetaData = new BeanMetaData(BeanMetaDataTestBean.class, null, page, null, false);
        // Check parameters, elements, element parameters.
        assertFalse( beanMetaData.isViewOnly() );
        assertTrue( beanMetaData.isDisplayed() );
        
        assertEquals("Bean Meta Data Test Bean", beanMetaData.getLabel() );
        
        // Props should be in alphabetical order
        ElementInfo[] expectedProps = { 
            new ElementInfo("activePrimitive", false, null, null, "Active Primitive", null),
            new ElementInfo("age", false, null, null, "Age", null),
            new ElementInfo("beans", false, null, null, "Beans", null),
            new ElementInfo("blockBean", false, null, null, "Block Bean", null),
            new ElementInfo("color", false, null, null, "Color", null),
            new ElementInfo("dateOnly", false, null, null, "Date Only", null),
            new ElementInfo("dateTimestamp", false, null, null, "Date Timestamp", null),
            new ElementInfo("description", false, null, null, "Description", null),
            new ElementInfo("firstName", false, null, null, "First Name", null),
            new ElementInfo("gender", false, null, null, "Gender", null),
            new ElementInfo("inlineBean", false, null, null, "Inline Bean", null),
            new ElementInfo("isActive", false, null, null, "Is Active", null),
            new ElementInfo("lastName", false, null, null, "Last Name", null),
            new ElementInfo("operand1", false, null, null, "Operand 1", null),
            new ElementInfo("operand2", false, null, null, "Operand 2", null),
            new ElementInfo("palette", false, null, null, "Palette", null),
            new ElementInfo("palette2", true, null, null, "Palette 2", null),
            new ElementInfo("popupBean", false, null, null, "Popup Bean", null),
            // Result is viewOnly because it only has a getter.
            new ElementInfo("result", true, null, null, "Result", null),
            new ElementInfo("savingsAmount", false, null, null, "Savings Amount", null),
            new ElementInfo("startDate", false, null, null, "Start Date", null),
            new ElementInfo("subComponent", false, null, null, "Sub Component", null),
            new ElementInfo("testBean2", false, null, null, "Test Bean 2", null),
            new ElementInfo("timeOnly", false, null, null, "Time Only", null),
        };

        assertEquals(expectedProps.length, beanMetaData.getDisplayedElements().size());
        
        int elementIdx = 0;
        for (ElementMetaData element : beanMetaData.getDisplayedElements()) {
            //System.out.println("new ElementInfo(\"" + element.getPropertyName() + "\", " + element.isViewOnly() + ", \"" + element.getFieldType() + "\", \"" + element.getElementTypeName() + "\", \"" + element.getLabel() + "\", \"" + element.getLabelImage() + "\", \"" + element.getTabId() + "\);
            assertElement(beanMetaData, element, expectedProps[elementIdx]);
            ++elementIdx;
        }
    }
    
    /**
     * Tests a context, without "extends". Implicitly extends the default context.
     */
    public void testContext()
    {
        WicketTester tester = new WicketTester();
        tester.startPage(BeanMetaDataTestPage.class);
        Page page = tester.getLastRenderedPage();
        
        // Use the "view" context
        BeanMetaData beanMetaData = new BeanMetaData(BeanMetaDataTestBean.class, "view", page, null, false);
        // Check parameters, elements, element parameters, tabs, tab elements.
        assertTrue( beanMetaData.isViewOnly() );
        assertTrue( beanMetaData.isDisplayed() );
        assertEquals("Bean View", beanMetaData.getLabel() );
        
        ElementInfo[] expectedProps = { 
            new ElementInfo("action.save", true, null, null, "Save", null),
            // firstName was explicitly overridden as not viewOnly.
            new ElementInfo("firstName", false, null, null, "First Name", null),
            new ElementInfo("lastName", true, null, null, "Last Name", null),
            new ElementInfo("EMPTY:28", true, "net.sourceforge.wicketwebbeans.fields.EmptyField", null, "", null),
            new ElementInfo("activePrimitive", true, null, null, "Active Primitive", null),
            new ElementInfo("color", true, null, null, "Color", null),
            new ElementInfo("inlineBean", true, "net.sourceforge.wicketwebbeans.fields.BeanInlineField", null, "Inline Bean", null),
            new ElementInfo("dateTimestamp", true, null, null, "Date Timestamp", null),
            new ElementInfo("blockBean", true, "net.sourceforge.wicketwebbeans.fields.BeanGridField", null, "Block Bean", null),
            new ElementInfo("testBean2", true, null, null, "Test Bean 2", null),
            new ElementInfo("popupBean", true, null, null, "Popup Bean", null),
            new ElementInfo("action.addRow", true, null, null, "Add Row", null),
            new ElementInfo("action.cancel", true, null, null, "Cancel", null),
            new ElementInfo("action.doIt", true, null, null, "Do It", null),
            new ElementInfo("age", true, null, null, "Age", null),
            new ElementInfo("beans", true, null, null, "Beans", null),
            new ElementInfo("dateOnly", true, null, null, "Date Only", null),
            new ElementInfo("description", true, null, null, "Description", null),
            new ElementInfo("gender", true, null, null, "Gender", null),
            new ElementInfo("isActive", true, null, null, "Is Active", null),
            new ElementInfo("operand1", true, null, null, "Operand 1", null),
            new ElementInfo("operand2", true, null, null, "Operand 2", null),
            new ElementInfo("palette", true, null, null, "Palette", null),
            new ElementInfo("palette2", true, null, null, "Palette 2", null),
            new ElementInfo("result", true, null, null, "Result", null),
            new ElementInfo("savingsAmount", true, null, null, "Savings Amount", null),
            new ElementInfo("startDate", true, null, null, "Start Date", null),
            new ElementInfo("timeOnly", true, null, null, "Time Only", null),
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
    
    /**
     * Tests a context with "extends". 
     */
    public void testContextWithExtends()
    {
        WicketTester tester = new WicketTester();
        tester.startPage(BeanMetaDataTestPage.class);
        Page page = tester.getLastRenderedPage();
        
        // Use the "popupView" context
        BeanMetaData beanMetaData = new BeanMetaData(BeanMetaDataTestBean.class, "popupView", page, null, false);
        // Check parameters, elements, element parameters.
        assertTrue( beanMetaData.isViewOnly() );
        assertTrue( beanMetaData.isDisplayed() );
        assertEquals("Bean Popup View", beanMetaData.getLabel() );
        
        ElementInfo[] expectedProps = { 
            new ElementInfo("action.save", true, null, null, "Save", null),
            // firstName was explicitly overridden as not viewOnly.
            new ElementInfo("firstName", false, null, null, "First Name", null),
            new ElementInfo("lastName", false, null, null, "Last Name", null),
            new ElementInfo("EMPTY:28", true, "net.sourceforge.wicketwebbeans.fields.EmptyField", null, "", null),
            new ElementInfo("activePrimitive", true, null, null, "Active Primitive", null),
            new ElementInfo("inlineBean", true, "net.sourceforge.wicketwebbeans.fields.BeanInlineField", null, "Inline Bean", null),
            new ElementInfo("dateTimestamp", true, null, null, "Date Timestamp", null),
            new ElementInfo("blockBean", true, "net.sourceforge.wicketwebbeans.fields.BeanGridField", null, "Block Bean", null),
            new ElementInfo("testBean2", true, null, null, "Test Bean 2", null),
            new ElementInfo("popupBean", true, null, null, "Popup Bean", null),
            new ElementInfo("action.addRow", true, null, null, "Add Row", null),
            new ElementInfo("action.cancel", true, null, null, "Cancel", null),
            new ElementInfo("action.doIt", true, null, null, "Do It", null),
            new ElementInfo("age", true, null, null, "Age", null),
            new ElementInfo("beans", true, null, null, "Beans", null),
            new ElementInfo("dateOnly", true, null, null, "Date Only", null),
            new ElementInfo("description", true, null, null, "Description", null),
            new ElementInfo("gender", true, null, null, "Gender", null),
            new ElementInfo("isActive", true, null, null, "Is Active", null),
            new ElementInfo("operand1", true, null, null, "Operand 1", null),
            new ElementInfo("operand2", true, null, null, "Operand 2", null),
            new ElementInfo("palette", true, null, null, "Palette", null),
            new ElementInfo("palette2", true, null, null, "Palette 2", null),
            new ElementInfo("result", true, null, null, "Result", null),
            new ElementInfo("savingsAmount", true, null, null, "Savings Amount", null),
            new ElementInfo("startDate", true, null, null, "Start Date", null),
            new ElementInfo("timeOnly", true, null, null, "Time Only", null),
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
    
    /**
     * Tests a missing context. 
     */
    public void testMissingContext()
    {
        WicketTester tester = new WicketTester();
        tester.startPage(BeanMetaDataTestPage.class);
        Page page = tester.getLastRenderedPage();
        
        try {
            new BeanMetaData(BeanMetaDataTestBean.class, "missingContext", page, null, false);
            fail("Expected exception on missing context");
        }
        catch (RuntimeException e) {
            // Expected.
        }
            
    }
    
    private static final class ElementInfo
    {
        String propName;
        boolean viewOnly;
        String fieldType;
        String elementType;
        String label;
        String labelImage;
 
        ElementInfo(String propName, boolean viewOnly, String fieldType, String elementType, String label, String labelImage)
        {
            this.propName = propName;
            this.viewOnly = viewOnly;
            this.fieldType = fieldType;
            this.elementType = elementType;
            this.label = label;
            this.labelImage = labelImage;
        }
    }
}
