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
 * Component Configuration AST for ComponentConfigParser. <p>
 * 
 * @author Dan Syrstad 
 */
public class ComponentConfigAST     // TODO Is there really any difference between this and a ParameterAST?
{
    private String name;
    private List<ParameterAST> parameters;

    public ComponentConfigAST(String name, List<ParameterAST> parameters)
    {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName()
    {
        return name;
    }

    public List<ParameterAST> getParameters()
    {
        return parameters;
    }
}
