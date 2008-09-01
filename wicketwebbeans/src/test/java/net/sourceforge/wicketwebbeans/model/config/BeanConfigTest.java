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

package net.sourceforge.wicketwebbeans.model.config;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.BeanConfig;
import net.sourceforge.wicketwebbeans.model.ComponentRegistry;
import net.sourceforge.wicketwebbeans.model.ParameterValueAST;

import org.apache.commons.io.FileUtils;

/**
 * Tests BeanConfig. <p>
 * 
 * @author Dan Syrstad
 */
public class BeanConfigTest extends TestCase
{
    static {
        BeanConfigTest.class.getClassLoader().setDefaultAssertionStatus(true);
    }

    public void testConstructorWithDefaultBeanName() throws Exception
    {
        URL url = createConfig("ROOT { class: x; param: value } NOTROOT { param: value2 }");
        BeanConfig config = new BeanConfig(null, url, null);
        assertNotNull(config.getComponentRegistry());
        assertEquals("value", config.getParameterValue("param").getValue());

        ComponentRegistry registry = new ComponentRegistry();
        config = new BeanConfig(null, url, registry);
        assertSame(registry, config.getComponentRegistry());
        assertEquals("value", config.getParameterValue("param").getValue());
    }

    public void testConstructorWithSpecifiedBeanName() throws Exception
    {
        URL url = createConfig("ROOT { param: value } Bean1 { class: x; param2: value2 } Bean2 { param3: value3 }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        assertNotNull(config.getComponentRegistry());
        assertEquals("value2", config.getParameterValue("param2").getValue());

        ComponentRegistry registry = new ComponentRegistry();
        config = new BeanConfig("Bean1", url, registry);
        assertSame(registry, config.getComponentRegistry());
        assertEquals("value2", config.getParameterValue("param2").getValue());
    }

    public void testGetParameterValues() throws Exception
    {
        URL url = createConfig("ROOT { class: x; firstParam: x; param: value1, value2, value3 }");
        BeanConfig config = new BeanConfig(null, url, null);
        List<ParameterValueAST> values = config.getParameterValues("param");
        assertEquals(3, values.size());
        assertEquals("value1", values.get(0).getValue());
        assertEquals("value2", values.get(1).getValue());
        assertEquals("value3", values.get(2).getValue());
    }

    public void testBadUrl() throws Exception
    {
        // Null URL
        try {
            new BeanConfig(null, null, null);
            fail();
        }
        catch (AssertionError e) {
            // Expected
        }

        // File Not Found
        try {
            new BeanConfig(null, new URL("file:///xyzzy-not-found"), null);
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Error reading stream"));
            assertNotNull(e.getCause());
        }
    }

    public void testBeanNotFound() throws Exception
    {
        URL url = createConfig("Bean1 { }");
        try {
            new BeanConfig("Bean2", url, null);
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Could not find bean"));
        }
    }

    public void testSetParameter() throws Exception
    {
        URL url = createConfig("ROOT { class: x; param2: x }");
        BeanConfig config = new BeanConfig(null, url, null);
        assertNull(config.getParameterValue("param1"));
        assertNotNull(config.getParameterValue("param2"));

        List<ParameterValueAST> values = new ArrayList<ParameterValueAST>();
        values.add(new ParameterValueAST("value1", false));
        config.setParameter("param1", values);

        List<ParameterValueAST> param1Values = config.getParameterValues("param1");
        assertEquals(1, param1Values.size());
        assertEquals("value1", param1Values.get(0).getValue());

        List<ParameterValueAST> param2Values = config.getParameterValues("param2");
        assertEquals(1, param2Values.size());
        assertEquals("x", param2Values.get(0).getValue());

        // Test overriding an existing parameter.
        List<ParameterValueAST> values2 = new ArrayList<ParameterValueAST>();
        values2.add(new ParameterValueAST("value2", false));
        values2.add(new ParameterValueAST("value3", false));
        config.setParameter("param2", values2);

        param2Values = config.getParameterValues("param2");
        assertEquals(2, param2Values.size());
        assertEquals("value2", param2Values.get(0).getValue());
        assertEquals("value3", param2Values.get(1).getValue());
    }

    public void testClassAndExtendsParameter() throws Exception
    {
        URL url = createConfig("ROOT { param2: x }");
        try {
            new BeanConfig(null, url, null);
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("must specify class or extends"));
        }

        // Class by itself is OK
        url = createConfig("ROOT { class: x }");
        BeanConfig config = new BeanConfig(null, url, null);
        assertNotNull(config.getParameterValue("class"));

        // Extends by itself is OK
        url = createConfig("ROOT { extends: x }");
        config = new BeanConfig(null, url, null);
        assertNotNull(config.getParameterValue("extends"));

        // But not both
        url = createConfig("ROOT { class: y; extends: x }");
        try {
            new BeanConfig(null, url, null);
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("cannot specify both class and extends"));
        }
    }

    public void testGetParameterValueAsString() throws Exception
    {
        URL url = createConfig("ROOT { class: x; param: \"xyzzy\" }");
        BeanConfig config = new BeanConfig(null, url, null);
        assertEquals("xyzzy", config.getParameterValueAsString("param"));
        assertNull(config.getParameterValueAsString("not-present"));
    }

    public void testNewInstanceWithNoParameters() throws Exception
    {
        URL url = createConfig("Bean1 { class: java.util.Date; }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        assertTrue(config.newInstance() instanceof Date);
    }

    public void testNewInstanceWithParameters() throws Exception
    {
        URL url = createConfig("Bean1 { " + "class: net.sourceforge.wicketwebbeans.model.config.TestBean;"
                        + "stringProp: \"stringValue\"; intProp: 5; doubleProp: 3.14; booleanProp: true; "
                        + " floatProp: 9.5; shortProp: 3; longProp: 123456789012345678;"
                        + " integerObjProp: 3; doubleObjProp: 9.99; floatObjProp: 10.3; shortObjProp: 62;"
                        + " booleanObjProp: false; longObjProp: 323; }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        TestBean bean = (TestBean)config.newInstance();
        assertTrue(bean instanceof TestBean);
        assertEquals(5, bean.getIntProp());
        assertEquals(3.14, bean.getDoubleProp());
        assertTrue(bean.isBooleanProp());
        assertEquals(9.5F, bean.getFloatProp());
        assertEquals((short)3, bean.getShortProp());
        assertEquals(123456789012345678L, bean.getLongProp());

        assertEquals(Integer.valueOf(3), bean.getIntegerObjProp());
        assertEquals(Double.valueOf(9.99), bean.getDoubleObjProp());
        assertEquals(Boolean.FALSE, bean.getBooleanObjProp());
        assertEquals(Float.valueOf(10.3F), bean.getFloatObjProp());
        assertEquals(Short.valueOf((short)62), bean.getShortObjProp());
        assertEquals(Long.valueOf(323L), bean.getLongObjProp());
    }

    public void testNewInstanceClassNotFound() throws Exception
    {
        URL url = createConfig("Bean1 { class: java.util.NotFound; }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        try {
            config.newInstance();
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Cannot create instance of bean"));
            assertTrue(e.getMessage().contains("java.util.NotFound"));
            assertNotNull(e.getCause());
        }
    }

    public void testNewInstanceNoPublicDefaultConstructor() throws Exception
    {
        URL url = createConfig("Bean1 { class: java.lang.reflect.AccessibleObject; }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        try {
            config.newInstance();
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Cannot create instance of bean"));
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof NoSuchMethodException);
        }
    }

    public void testNewInstancePropertyNotFound() throws Exception
    {
        URL url = createConfig("Bean1 { class: net.sourceforge.wicketwebbeans.model.config.TestBean; notfound: x }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        try {
            config.newInstance();
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Cannot find property"));
            assertTrue(e.getMessage().contains("notfound"));
        }
    }

    public void testNewInstancePropertySetterNotExposed() throws Exception
    {
        URL url = createConfig("Bean1 { class: net.sourceforge.wicketwebbeans.model.config.TestBean; setterNotExposed: x }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        try {
            config.newInstance();
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().matches("Property setterNotExposed .* does not have an exposed setter.*"));
        }
    }

    public void testNewInstancePropertyTypeNotSupported() throws Exception
    {
        URL url = createConfig("Bean1 { class: net.sourceforge.wicketwebbeans.model.config.TestBean; typeNotSupported: x }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        try {
            config.newInstance();
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().matches("Property type .* typeNotSupported .* not supported.*"));
        }
    }

    public void testNewInstancePropertyNotExposed() throws Exception
    {
        URL url = createConfig("Bean1 { class: net.sourceforge.wicketwebbeans.model.config.TestBean; notExposed: x }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        try {
            config.newInstance();
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Cannot find property"));
            assertTrue(e.getMessage().contains("notExposed"));
        }
    }

    public void testNewInstancePropertyThrows() throws Exception
    {
        URL url = createConfig("Bean1 { class: net.sourceforge.wicketwebbeans.model.config.TestBean; throwsException: x }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        try {
            config.newInstance();
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Error setting property"));
            assertTrue(e.getCause().getMessage().equals("Thrown from setter"));
        }
    }

    public void testNewInstanceWithNullParameters() throws Exception
    {
        URL url = createConfig("Bean1 { class: net.sourceforge.wicketwebbeans.model.config.TestBean;"
                        + "integerObjProp: null; stringProp: \"null\" }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        TestBean bean = (TestBean)config.newInstance();
        assertNull(bean.getIntegerObjProp());
        assertEquals("null", bean.getStringProp());
    }

    public void testNewInstanceWithArgs() throws Exception
    {
        URL url = createConfig("Bean1 { class: net.sourceforge.wicketwebbeans.model.config.TestBean; }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        TestBean bean = (TestBean)config.newInstance("string", 32, 64);
        assertEquals("string", bean.getStringProp());
        assertEquals(32, bean.getIntProp());
        assertEquals(Integer.valueOf(64), bean.getIntegerObjProp());
    }

    // TODO Broke because nulls not handled right now
    public void xtestNewInstanceWithNullArgs() throws Exception
    {
        URL url = createConfig("Bean1 { class: net.sourceforge.wicketwebbeans.model.config.TestBean; }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        TestBean bean = (TestBean)config.newInstance(null, 3, null);
        assertNull(bean.getStringProp());
        assertEquals(3, bean.getIntProp());
        assertNull(bean.getIntegerObjProp());
    }

    public void testNewInstanceWithUnmatchedArgs() throws Exception
    {
        URL url = createConfig("Bean1 { class: net.sourceforge.wicketwebbeans.model.config.TestBean; }");
        BeanConfig config = new BeanConfig("Bean1", url, null);
        try {
            config.newInstance("1", 2);
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Cannot create instance of bean"));
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof NoSuchMethodException);
        }
    }

    private URL createConfig(String configStr) throws Exception
    {
        File tmpFile = File.createTempFile("config", ".wwb");
        tmpFile.deleteOnExit();
        FileUtils.writeStringToFile(tmpFile, configStr);
        return tmpFile.toURI().toURL();
    }

}
