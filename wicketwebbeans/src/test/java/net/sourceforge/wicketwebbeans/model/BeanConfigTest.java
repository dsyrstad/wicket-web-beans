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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.BeanConfig;
import net.sourceforge.wicketwebbeans.model.ParameterAST;
import net.sourceforge.wicketwebbeans.model.ParameterValueAST;
import net.sourceforge.wicketwebbeans.test.TestUtils;

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
        BeanConfig config = createBeanConfig("ROOT { class: x; firstParam: x; param: value1, value2, value3 }");
        List<ParameterValueAST> values = config.getParameterValues("param");
        assertEquals(3, values.size());
        assertEquals("value1", values.get(0).getValue());
        assertEquals("value2", values.get(1).getValue());
        assertEquals("value3", values.get(2).getValue());
    }

    public void testSetParameter() throws Exception
    {
        BeanConfig config = createBeanConfig("ROOT { class: x; param2: x }");
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

    public void testSetParameters() throws Exception
    {
        BeanConfig config = createBeanConfig("ROOT { class: x; param3: overrideme }");
        Collection<ParameterAST> parameters = new ArrayList<ParameterAST>();
        for (int i = 0; i < 5; ++i) {
            List<ParameterValueAST> values = new ArrayList<ParameterValueAST>();
            values.add(new ParameterValueAST("value" + i, false));
            parameters.add(new ParameterAST("param" + i, values));
        }

        config.setParameters(parameters);
        for (int i = 0; i < 5; i++) {
            assertEquals("value" + i, config.getParameterValueAsString("param" + i));
        }
    }

    public void testGetParameterValueAsString() throws Exception
    {
        BeanConfig config = createBeanConfig("ROOT { class: x; param: \"xyzzy\" }");
        assertEquals("xyzzy", config.getParameterValueAsString("param"));
        assertNull(config.getParameterValueAsString("not-present"));
    }

    public void testGetParameterValueAsInt() throws Exception
    {
        BeanConfig config = createBeanConfig("ROOT { class: x; param: 55 }");
        assertEquals(55, config.getParameterValueAsInt("param", 12));
        assertEquals(12, config.getParameterValueAsInt("param2", 12));
    }

    public void testClone() throws Exception
    {
        BeanConfig config = createBeanConfig("ROOT { class: x; param: \"xyzzy\" }");
        BeanConfig clone = config.clone();
        // Setting parameter on clone should affect the original
        clone.setParameter("param2", Collections.singletonList(new ParameterValueAST("value2", true)));
        assertNotNull(clone.getParameterValue("param2"));
        assertNull(config.getParameterValue("param2"));
    }

    public void testRemoveParameter() throws Exception
    {
        BeanConfig config = createBeanConfig("ROOT { class: x; param: \"xyzzy\" }");
        assertNotNull(config.getParameterValue("param"));
        config.removeParameter("param");
        assertNull(config.getParameterValue("param"));
    }

    public static BeanConfig createBeanConfig(String configStr) throws Exception
    {
        return TestUtils.createBeanFactory(configStr).getBeanConfig("ROOT");
    }
}
