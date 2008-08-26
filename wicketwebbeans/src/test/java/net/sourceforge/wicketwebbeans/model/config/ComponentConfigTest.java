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
    public ComponentConfigTest(String name)
    {
        super(name);
    }

    public void testConstructorWithDefaultComponentName() throws Exception
    {
        URL url = createConfig("ROOT { param: value } NOTROOT { param: value2 }");
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
        URL url = createConfig("ROOT { param: value } Component1 { param2: value2 } Component2 { param3: value3 }");
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
        URL url = createConfig("ROOT { firstParam: x; param: value1, value2, value3 }");
        ComponentConfig config = new ComponentConfig(null, url, null);
        List<ParameterValueAST> values = config.getParameterValues("param");
        assertEquals(3, values.size());
        assertEquals("value1", values.get(0).getValue());
        assertEquals("value2", values.get(1).getValue());
        assertEquals("value3", values.get(2).getValue());
    }

    private URL createConfig(String configStr) throws Exception
    {
        File tmpFile = File.createTempFile("config", "wwb");
        tmpFile.deleteOnExit();
        FileUtils.writeStringToFile(tmpFile, configStr);
        return tmpFile.toURI().toURL();
    }
}
