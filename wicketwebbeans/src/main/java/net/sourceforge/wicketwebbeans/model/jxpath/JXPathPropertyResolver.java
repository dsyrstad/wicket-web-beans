/*---
   Copyright 2007 Visual Systems Corporation.
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

package net.sourceforge.wicketwebbeans.model.jxpath;

import net.sourceforge.wicketwebbeans.model.PropertyProxy;
import net.sourceforge.wicketwebbeans.model.PropertyResolver;

/**
 * PropertyResolver that uses Apache Commons JXPath. <p>
 * 
 * @author Dan Syrstad
 */
public class JXPathPropertyResolver implements PropertyResolver
{
    private static final long serialVersionUID = -8758061340668585471L;

    /** 
     * {@inheritDoc}
     * @see net.sourceforge.wicketwebbeans.model.PropertyResolver#createPropertyProxy(java.lang.String)
     */
    public PropertyProxy createPropertyProxy(String propertySpec)
    {
        return new JXPathPropertyProxy(propertySpec);
    }
}
