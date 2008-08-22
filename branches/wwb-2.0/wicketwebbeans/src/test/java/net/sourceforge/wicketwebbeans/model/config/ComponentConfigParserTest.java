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

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.model.ComponentConfigAST;
import net.sourceforge.wicketwebbeans.model.ComponentConfigParser;
import net.sourceforge.wicketwebbeans.model.ParameterAST;
import net.sourceforge.wicketwebbeans.model.ParameterValueAST;

/**
 * Tests ComponentConfigParser. <p>
 * 
 * @author Dan Syrstad
 */
public class ComponentConfigParserTest extends TestCase
{
    public ComponentConfigParserTest(String name)
    {
        super(name);
    }

    public void testParseBasic()
    {
        ComponentConfigParser parser = new ComponentConfigParser("test", createStream(
                        "ROOT {\n" +
                        "    singleValueParam: singleValue;\n" +
                        "    multipleValueParam: value1, value2, value3;\n" +
                        "    literalValuesParam: \"literal1\", 5, 5.5, true, false;\n" +
                        "}\n" +
                        "# Make sure parser allows empty blocks\n" +
                        "XX { }\n" +
                        "XX2 { x: a{}, b, c; }"
                        ));
        List<ComponentConfigAST> asts = parser.parse();
        assertEquals(3, asts.size());
        
        ComponentConfigAST rootAst = asts.get(0);
        assertEquals("ROOT", rootAst.getName());
        List<ParameterAST> rootParams = rootAst.getParameters();
        assertEquals(3, rootParams.size());
        
        ParameterAST parameterAST = rootParams.get(0);
        assertEquals("singleValueParam", parameterAST.getName());
        List<ParameterValueAST> values = parameterAST.getValues();
        assertEquals(1, values.size());
        assertEquals("singleValue", values.get(0).getValue());
        assertFalse(values.get(0).isLiteral());
        assertTrue(values.get(0).getSubParameters().isEmpty());
        
        parameterAST = rootParams.get(1);
        assertEquals("multipleValueParam", parameterAST.getName());
        values = parameterAST.getValues();
        assertEquals(3, values.size());
        assertEquals("value1", values.get(0).getValue());
        assertFalse(values.get(0).isLiteral());
        assertTrue(values.get(0).getSubParameters().isEmpty());
        assertEquals("value2", values.get(1).getValue());
        assertFalse(values.get(1).isLiteral());
        assertTrue(values.get(1).getSubParameters().isEmpty());
        assertEquals("value3", values.get(2).getValue());
        assertFalse(values.get(2).isLiteral());
        assertTrue(values.get(2).getSubParameters().isEmpty());
        
        parameterAST = rootParams.get(2);
        assertEquals("literalValuesParam", parameterAST.getName());
        values = parameterAST.getValues();
        assertEquals(5, values.size());
        assertEquals("literal1", values.get(0).getValue());
        assertTrue(values.get(0).isLiteral());
        assertTrue(values.get(0).getSubParameters().isEmpty());
        assertEquals("5", values.get(1).getValue());
        assertEquals(5, values.get(1).getIntegerValue().intValue());
        assertTrue(values.get(1).isLiteral());
        assertTrue(values.get(1).getSubParameters().isEmpty());
        assertEquals("5.5", values.get(2).getValue());
        assertEquals(5.5, values.get(2).getDoubleValue().doubleValue());
        assertTrue(values.get(2).isLiteral());
        assertTrue(values.get(2).getSubParameters().isEmpty());
        assertEquals("true", values.get(3).getValue());
        assertEquals(true, values.get(3).getBooleanValue().booleanValue());
        assertTrue(values.get(3).isLiteral());
        assertTrue(values.get(3).getSubParameters().isEmpty());
        assertEquals("false", values.get(4).getValue());
        assertEquals(false, values.get(4).getBooleanValue().booleanValue());
        assertTrue(values.get(4).isLiteral());
        assertTrue(values.get(4).getSubParameters().isEmpty());
    }
    
    public void testParseWithSubParameter()
    {
        ComponentConfigParser parser = new ComponentConfigParser("test", createStream(
                        "Component1 {\n" +
                        "    params: value1, subParams { subParam1: v1; subParam2: v2 }, value3;\n" +
                        "}\n"
                        ));
        List<ComponentConfigAST> asts = parser.parse();
        assertEquals(1, asts.size());
        ComponentConfigAST ast = asts.get(0);
        assertEquals("Component1", ast.getName());
        
        assertEquals(1, ast.getParameters().size());
        ParameterAST param = ast.getParameters().get(0);
        assertEquals("params", param.getName());
        assertEquals(3, param.getValues().size());
        ParameterValueAST paramValue = param.getValues().get(1);
        assertEquals("subParams", paramValue.getValue());
    }
    
    private InputStream createStream(String configStr) {
        return new ReaderInputStream(new StringReader(configStr));
    }
}
