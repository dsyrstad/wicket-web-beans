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
package net.sourceforge.wicketwebbeans.model;

import java.util.List;


/**
 * Parameter AST for BeanConfigParser. <p>
 * 
 * @author Dan Syrstad 
 */
public class ParameterAST
{
    private String name;
    private List<ParameterValueAST> values;

    ParameterAST(String name, List<ParameterValueAST> values)
    {
        this.name = name;
        this.values = values;
    }

    public String getName()
    {
        return name;
    }

    public List<ParameterValueAST> getValues()
    {
        return values;
    }

    public String[] getValuesAsStrings()
    {
        String[] stringValues = new String[values.size()];
        for (int i = 0; i < stringValues.length; i++) {
            stringValues[i] = values.get(i).getValue();
        }
        
        return stringValues;
    }
}
