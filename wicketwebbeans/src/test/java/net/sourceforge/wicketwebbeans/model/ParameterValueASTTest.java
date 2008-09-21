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
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

/**
 * ParameterValueAST. <p>
 * 
 * @author Dan Syrstad
 */
public class ParameterValueASTTest extends TestCase
{
    public void testGetSubParamter()
    {
        ParameterValueAST ast = new ParameterValueAST("test", false);
        List<ParameterAST> parameters = new ArrayList<ParameterAST>();
        parameters.add(new ParameterAST("subparam1", Collections.<ParameterValueAST> emptyList()));
        parameters.add(new ParameterAST("subparam2", Collections.<ParameterValueAST> emptyList()));
        parameters.add(new ParameterAST("subparam3", Collections.<ParameterValueAST> emptyList()));
        ast.setSubParameters(parameters);

        assertNull(ast.getSubParameter("notthere"));
        assertEquals("subparam1", ast.getSubParameter("subparam1").getName());
        assertEquals("subparam2", ast.getSubParameter("subparam2").getName());
        assertEquals("subparam3", ast.getSubParameter("subparam3").getName());
    }

    public void testGetSubParamterValueAsInt()
    {
        ParameterValueAST ast = new ParameterValueAST("test", false);
        List<ParameterAST> parameters = new ArrayList<ParameterAST>();
        parameters.add(new ParameterAST("subparam1", Collections.singletonList(new ParameterValueAST("11", true))));
        parameters.add(new ParameterAST("subparam2", Collections.singletonList(new ParameterValueAST("22", true))));
        parameters.add(new ParameterAST("subparam3", Collections.singletonList(new ParameterValueAST("33", true))));
        parameters.add(new ParameterAST("subparam4", Collections.singletonList(new ParameterValueAST(null, false))));
        ast.setSubParameters(parameters);

        assertEquals(55, ast.getSubParameterValueAsInt("notthere", 55));
        assertEquals(11, ast.getSubParameterValueAsInt("subparam1", 0));
        assertEquals(22, ast.getSubParameterValueAsInt("subparam2", 0));
        assertEquals(33, ast.getSubParameterValueAsInt("subparam3", 0));
        assertEquals(99, ast.getSubParameterValueAsInt("subparam4", 99));
    }
}
