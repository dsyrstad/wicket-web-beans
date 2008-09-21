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
 * Proxies access to a property. This is created by a PropertyResolver. <p>
 * 
 * @author Dan Syrstad
 */
public interface PropertyProxy extends Serializable
{
    /**
     * Resolves the property's specification. 
     *
     * @param bean the bean from which the property will be derived.
     * 
     * @return the object corresponding to the property or null.
     */
    Object getValue(Object bean);

    void setValue(Object bean, Object value);
}
