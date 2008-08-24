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
        ComponentConfigParser parser = new ComponentConfigParser("test", createStream("ROOT {\n"
                        + "    singleValueParam: singleValue;\n" + "    multipleValueParam: value1, value2, value3;\n"
                        + "    literalValuesParam: \"literal1\", 5, 5.5, true, false;\n" + "}\n"
                        + "# Make sure parser allows empty blocks\n" + "XX { }\n" + "XX2 { x: a{}, b, c; }"));
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
        ComponentConfigParser parser = new ComponentConfigParser("test", createStream("Component1 {\n"
                        + "    params: value1, subParams { subParam1: v1; subParam2: v2 }, value3;\n" + "}\n"));
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
        assertEquals(2, paramValue.getSubParameters().size());
        assertEquals("subParam1", paramValue.getSubParameters().get(0).getName());
        assertEquals(1, paramValue.getSubParameters().get(0).getValues().size());
        assertEquals("v1", paramValue.getSubParameters().get(0).getValuesAsStrings()[0]);
        assertEquals("subParam2", paramValue.getSubParameters().get(1).getName());
        assertEquals(1, paramValue.getSubParameters().get(1).getValues().size());
        assertEquals("v2", paramValue.getSubParameters().get(1).getValuesAsStrings()[0]);
    }

    public void testSyntaxErrors()
    {
        Test[] tests = new Test[] {
                        //
                        new Test("componentName", "Unexpected EOF reading from test"),
                        new Test("componentName {", "Unexpected EOF reading from test"),
                        new Test("{", "Error: test at line 1: Expected 'bean name', but got '{'"),
                        new Test("X { x }", "Error: test at line 1: Expected ':', but got '}'"),
                        new Test("X { x: }", "Unexpected EOF reading from test"),
                        new Test("X { x: x;; }", "Error: test at line 1: Expected ':', but got '}'"),
                        new Test("X { x: x; };", "Error: test at line 1: Expected 'bean name', but got ';'"),
                        new Test("X { x: x, y,, z }", "Error: test at line 1: Expected ';' or '}', but got 'z'"),
                        new Test("X { x: x, y, z: x; }", "Error: test at line 1: Expected ';' or '}', but got ':'"),
                        new Test("X { x: x; } Z", "Unexpected EOF reading from test"),
                        new Test("X { x: x { }", "Unexpected EOF reading from test"),
                        new Test("X { x: x { x } }", "Error: test at line 1: Expected ':', but got '}'"),
                        new Test("X { x: x { x: } }", "Unexpected EOF reading from test"),
                        new Test("X { x: \"unterminated literal }", "Unexpected EOF reading from test"),
        //                
        };

        for (Test test : tests) {
            assertSyntaxError(test);
        }
    }

    private void assertSyntaxError(Test test)
    {
        ComponentConfigParser parser = new ComponentConfigParser("test", createStream(test.configString));
        try {
            parser.parse();
            fail("Expected Exception for '" + test.configString + "'");
        }
        catch (RuntimeException e) {
            System.out.println("new Test(\"" + test.configString.replace("\"", "\\\"").replace("\n", "\\n") + "\", \""
                            + e.getMessage() + "\"),");
            //assertEquals(test.expectedMsg, e.getMessage());
        }

    }

    private InputStream createStream(String configStr)
    {
        return new ReaderInputStream(new StringReader(configStr));
    }


    private static final class Test
    {
        String configString;
        String expectedMsg;

        Test(String configString, String expectedMsg)
        {
            this.configString = configString;
            this.expectedMsg = expectedMsg;
        }
    }
}
