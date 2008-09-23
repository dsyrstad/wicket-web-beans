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

import java.io.Serializable;

/**
 * Resolves BeanConfig property specifications. <p>
 * 
 * @author Dan Syrstad
 */
public interface PropertyResolver extends Serializable
{
    /**
     * Creates PropertyProxy for a property specification. 
     *
     * @param beanCreator a PropertyPathBeanCreator.
     * @param propertySpec an implementation-specific property specification.
     * 
     * @return a PropertyProxy.
     */
    PropertyProxy createPropertyProxy(PropertyPathBeanCreator beanCreator, String propertySpec);
}
