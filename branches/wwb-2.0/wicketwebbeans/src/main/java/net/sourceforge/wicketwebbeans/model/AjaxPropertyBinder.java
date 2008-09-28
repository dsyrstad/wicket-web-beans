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

import org.apache.wicket.Component;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * A specialization of {@link PropertyBinder} that handles listenBeans that are
 * Wicket Components. If an Ajax request is in progress, the listenBean is added
 * to the {@link AjaxRequestTarget}.<p>
 * 
 * @author Dan Syrstad
 */
public class AjaxPropertyBinder extends PropertyBinder
{
    private static final long serialVersionUID = 4035733882993910544L;

    /**
     * Construct an AjaxPropertyBinder. 
     */
    public AjaxPropertyBinder(Object listenBean, Object updateBean, PropertyProxy listenProperty,
                    PropertyProxy updateProperty)
    {
        super(listenBean, updateBean, listenProperty, updateProperty);
    }

    @Override
    public void updateProperty()
    {
        super.updateProperty();
        Object updateBean = getUpdateBean();
        if (isActive() && updateBean instanceof Component) {
            IRequestTarget target = RequestCycle.get().getRequestTarget();
            if (target instanceof AjaxRequestTarget) {
                ((AjaxRequestTarget)target).addComponent((Component)updateBean);
            }
        }
    }
}
