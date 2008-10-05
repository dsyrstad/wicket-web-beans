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

package net.sourceforge.wicketwebbeans.model.jxpath;

import java.io.Serializable;

import net.sourceforge.wicketwebbeans.model.PropertyPathBeanCreator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

/**
 * A JXPath AbstractFactory to create objects for a path. <p>
 * 
 * @author Dan Syrstad
 */
public class JXPathObjectFactory extends AbstractFactory implements Serializable
{
    private static final long serialVersionUID = -4199210836923584421L;

    private PropertyPathBeanCreator beanCreator;

    public JXPathObjectFactory(PropertyPathBeanCreator beanCreator)
    {
        this.beanCreator = beanCreator;
    }

    @Override
    public boolean createObject(JXPathContext context, Pointer pointer, Object parent, String name, int index)
    {
        Class<?> type;
        try {
            type = PropertyUtils.getPropertyType(parent, name);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to find property '" + name + "' on " + parent);
        }

        Object obj = beanCreator.createBean(type);
        pointer.setValue(obj);
        return true;
    }
}
