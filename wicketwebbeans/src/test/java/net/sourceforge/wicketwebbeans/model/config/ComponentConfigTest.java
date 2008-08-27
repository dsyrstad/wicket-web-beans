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
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;
import net.sourceforge.wicketwebbeans.model.ComponentRegistry;
import net.sourceforge.wicketwebbeans.model.ParameterValueAST;

import org.apache.commons.io.FileUtils;

/**
 * Tests ComponentConfig. <p>
 * 
 * @author Dan Syrstad
 */
public class ComponentConfigTest extends TestCase
{
    static {
        ComponentConfigTest.class.getClassLoader().setDefaultAssertionStatus(true);
    }

    public ComponentConfigTest(String name)
    {
        super(name);
    }

    public void testConstructorWithDefaultComponentName() throws Exception
    {
        URL url = createConfig("ROOT { class: x; param: value } NOTROOT { param: value2 }");
        ComponentConfig config = new ComponentConfig(null, url, null);
        assertNotNull(config.getComponentRegistry());
        assertEquals("value", config.getParameterValue("param").getValue());

        ComponentRegistry registry = new ComponentRegistry();
        config = new ComponentConfig(null, url, registry);
        assertSame(registry, config.getComponentRegistry());
        assertEquals("value", config.getParameterValue("param").getValue());
    }

    public void testConstructorWithSpecifiedComponentName() throws Exception
    {
        URL url = createConfig("ROOT { param: value } Component1 { class: x; param2: value2 } Component2 { param3: value3 }");
        ComponentConfig config = new ComponentConfig("Component1", url, null);
        assertNotNull(config.getComponentRegistry());
        assertEquals("value2", config.getParameterValue("param2").getValue());

        ComponentRegistry registry = new ComponentRegistry();
        config = new ComponentConfig("Component1", url, registry);
        assertSame(registry, config.getComponentRegistry());
        assertEquals("value2", config.getParameterValue("param2").getValue());
    }

    public void testGetParameterValues() throws Exception
    {
        URL url = createConfig("ROOT { class: x; firstParam: x; param: value1, value2, value3 }");
        ComponentConfig config = new ComponentConfig(null, url, null);
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
            new ComponentConfig(null, null, null);
            fail();
        }
        catch (AssertionError e) {
            // Expected
        }

        // File Not Found
        try {
            new ComponentConfig(null, new URL("file:///xyzzy-not-found"), null);
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Error reading stream"));
        }
    }

    public void testComponentNotFound() throws Exception
    {
        URL url = createConfig("Component1 { }");
        try {
            new ComponentConfig("Component2", url, null);
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Could not find component"));
        }
    }

    public void testSetParameter() throws Exception
    {
        URL url = createConfig("ROOT { class: x; param2: x }");
        ComponentConfig config = new ComponentConfig(null, url, null);
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
            new ComponentConfig(null, url, null);
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("must specify class or extends"));
        }

        // Class by itself is OK
        url = createConfig("ROOT { class: x }");
        ComponentConfig config = new ComponentConfig(null, url, null);
        assertNotNull(config.getParameterValue("class"));

        // Extends by itself is OK
        url = createConfig("ROOT { extends: x }");
        config = new ComponentConfig(null, url, null);
        assertNotNull(config.getParameterValue("extends"));

        // But not both
        url = createConfig("ROOT { class: y; extends: x }");
        try {
            new ComponentConfig(null, url, null);
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("cannot specify both class and extends"));
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
