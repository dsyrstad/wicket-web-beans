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

import java.util.Date;

import junit.framework.TestCase;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * PropertyProxyModel. <p>
 * 
 * @author Dan Syrstad
 */
public class PropertyProxyModelTest extends TestCase
{
    @SuppressWarnings("serial")
    private PropertyProxy proxy = new PropertyProxy() {
        public Object getValue(Object bean)
        {
            return bean;
        }

        public void setValue(Object bean, Object value)
        {

        }
    };

    public void testGetObject()
    {
        final Date dateValue = new Date();
        PropertyProxyModel model = new PropertyProxyModel(proxy, new Model(dateValue));
        assertSame(dateValue, model.getObject());
    }

    public void testGetChainedModel()
    {
        IModel chainedModel = new Model("test");
        PropertyProxyModel model = new PropertyProxyModel(proxy, chainedModel);
        assertSame(chainedModel, model.getChainedModel());
    }
}
