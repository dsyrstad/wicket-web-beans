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

import org.apache.wicket.model.IChainingModel;
import org.apache.wicket.model.IModel;

/**
 * Wicket IModel for a PropertyProxy. <p>
 * 
 * @author Dan Syrstad
 */
// TODO Test
public class PropertyProxyModel implements IChainingModel
{
    private static final long serialVersionUID = -5278835598135566134L;

    private IModel beanModel;
    private PropertyProxy proxy;

    public PropertyProxyModel(PropertyProxy proxy, IModel beanModel)
    {
        this.proxy = proxy;
        this.beanModel = beanModel;
    }

    public Object getObject()
    {
        return proxy.getValue(beanModel.getObject());
    }

    public void setObject(Object object)
    {
        // TODO Later
    }

    public void detach()
    {
        beanModel.detach();
    }

    public IModel getChainedModel()
    {
        return beanModel;
    }

    public void setChainedModel(IModel model)
    {
        beanModel = model;
    }
}
