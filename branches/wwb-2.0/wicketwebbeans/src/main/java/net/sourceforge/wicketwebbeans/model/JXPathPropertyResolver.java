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

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathContextFactory;
import org.apache.wicket.model.IModel;

/**
 * PropertyResolver that uses Apache Commons JXPath. <p>
 * 
 * @author Dan Syrstad
 */
public class JXPathPropertyResolver implements PropertyResolver
{
    private static final long serialVersionUID = -8758061340668585471L;

    private JXPathContextFactory jxpathContextFactory = JXPathContextFactory.newInstance();

    /** 
     * {@inheritDoc}
     * @see net.sourceforge.wicketwebbeans.model.PropertyResolver#resolveProperty(org.apache.wicket.model.IModel, java.lang.String)
     */
    public Object resolveProperty(IModel model, String propertySpec)
    {
        assert model != null;
        Object bean = model.getObject();
        if (bean == null) {
            return bean;
        }

        JXPathContext context = jxpathContextFactory.newContext(null, bean);
        return context.getValue(propertySpec);
    }
}
