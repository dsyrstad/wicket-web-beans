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

package net.sourceforge.wicketwebbeans.model;

import org.apache.commons.beanutils.ConstructorUtils;

/**
 * A simple BeanCreator that assumes that the bean to be created is a JavaBean
 * and has a public no-arg constructor. <p>
 * 
 * @author Dan Syrstad
 */
public class JavaBeansPropertyPathBeanCreator implements PropertyPathBeanCreator
{
    private static final Object[] NO_ARGS = new Object[0];

    public Object createBean(Class<?> type)
    {
        try {
            return ConstructorUtils.invokeConstructor(type, NO_ARGS);
        }
        catch (Exception e) {
            throw new RuntimeException("Error creating bean", e);
        }
    }
}
