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

import java.util.ArrayList;
import java.util.List;


/**
 * ParameterValue AST for ComponentConfigParser. <p>
 * 
 * @author Dan Syrstad 
 */
public class ParameterValueAST
{
    private String value;
    private List<ParameterAST> subParameters = new ArrayList<ParameterAST>();
    private boolean literal;

    public ParameterValueAST(String value, boolean isLiteral)
    {
        this.value = value;
        this.literal = isLiteral;
    }

    public List<ParameterAST> getSubParameters()
    {
        return subParameters;
    }

    public void setSubParameters(List<ParameterAST> parameters)
    {
        this.subParameters = parameters;
    }

    /**
     * @return the raw value.
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * @return the boolean value, or null if the value is null.
     */
    public Boolean getBooleanValue() 
    {
        return value == null ? null : Boolean.valueOf(value);
    }

    /**
     * @return the Integer value, or null if the value is null.
     */
    public Integer getIntegerValue() 
    {
        return value == null ? null : Integer.valueOf(value);
    }

    /**
     * @return the Double value, or null if the value is null.
     */
    public Double getDoubleValue() 
    {
        return value == null ? null : Double.valueOf(value);
    }

    public boolean isLiteral()
    {
        return literal;
    }
}
