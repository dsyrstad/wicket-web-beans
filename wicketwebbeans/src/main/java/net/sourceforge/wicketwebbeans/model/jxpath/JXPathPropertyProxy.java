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

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;

/**
 * A PropertyProxy for Apache Commons JXPath. Created by JXPathPropertyResolver. <p>
 * 
 * @author Dan Syrstad
 */
public class JXPathPropertyProxy implements PropertyProxy
{
    private static final long serialVersionUID = -504835121389856794L;

    private CompiledExpression compiledExpression;

    JXPathPropertyProxy(String propertyExpression)
    {
        compiledExpression = JXPathContext.compile(propertyExpression);
    }

    /** 
     * {@inheritDoc}
     * @see net.sourceforge.wicketwebbeans.model.PropertyProxy#getValue(org.apache.wicket.model.IModel)
     */
    public Object getValue(Object bean)
    {
        if (bean == null) {
            return bean;
        }

        JXPathContext context = JXPathContext.newContext(bean);
        // LATER - Unfortunately unrecognized properties are not caught by this.
        context.setLenient(true);
        return compiledExpression.getValue(context);
    }
}
