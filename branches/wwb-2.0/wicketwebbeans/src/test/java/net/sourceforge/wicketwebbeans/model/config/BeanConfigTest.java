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
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.BeanConfig;
import net.sourceforge.wicketwebbeans.model.BeanFactory;
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

    public void testGetParameterValues() throws Exception
    {
        BeanConfig config = createConfig("ROOT { class: x; firstParam: x; param: value1, value2, value3 }");
        List<ParameterValueAST> values = config.getParameterValues("param");
        assertEquals(3, values.size());
        assertEquals("value1", values.get(0).getValue());
        assertEquals("value2", values.get(1).getValue());
        assertEquals("value3", values.get(2).getValue());
    }

    public void testSetParameter() throws Exception
    {
        BeanConfig config = createConfig("ROOT { class: x; param2: x }");
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

    public void testGetParameterValueAsString() throws Exception
    {
        BeanConfig config = createConfig("ROOT { class: x; param: \"xyzzy\" }");
        assertEquals("xyzzy", config.getParameterValueAsString("param"));
        assertNull(config.getParameterValueAsString("not-present"));
    }

    private BeanConfig createConfig(String configStr) throws Exception
    {
        File tmpFile = File.createTempFile("config", ".wwb");
        tmpFile.deleteOnExit();
        FileUtils.writeStringToFile(tmpFile, configStr);
        return new BeanFactory().loadBeanConfig(tmpFile.toURI().toURL()).getBeanConfig("ROOT");
    }
}
