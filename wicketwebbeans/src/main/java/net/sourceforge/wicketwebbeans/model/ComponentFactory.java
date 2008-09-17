/*---
   Copyright 20078 Visual Systems Corporation.
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

/**
 * Manufactures components from various configurations. <p>
 * 
 * @author Dan Syrstad
 */
public class ComponentFactory
{
    private PropertyProxyModel propertyProxyModel;
    private BeanConfig componentConfig;

    public ComponentFactory(BeanFactory factory, ParameterValueAST parameterValue)
    {
        String valueString = parameterValue.getValue();
        // TODO Test
        if (valueString.startsWith("$")) {
            this.propertyProxyModel = factory.resolvePropertyProxyModel(valueString);
        }
        else {
            this.componentConfig = factory.getBeanConfig(valueString, parameterValue.getSubParameters());
        }
    }

}
