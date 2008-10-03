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

package net.sourceforge.wicketwebbeans.components;

import net.sourceforge.wicketwebbeans.model.BeanFactory;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Button that triggers an action method when clicked. <p>
 * 
 * @author Dan Syrstad
 */
public class Button extends Panel
{
    private static final long serialVersionUID = -7958656485641819413L;

    private static final Object[] NO_ARGS = new Object[0];

    private IModel actionBeanModel;
    private String actionMethodName;
    private IModel labelModel = new Model();

    public Button(String wicketId)
    {
        super(wicketId);
        AjaxLink button = new ButtonAjaxLink("button");
        add(button);

        button.add(new Label("label", labelModel));
    }

    /** Called by BeanFactory.newInstance(). */
    public void setBeanFactory(BeanFactory beanFactory)
    {
        // Default model
        this.actionBeanModel = beanFactory.getBeanModel();
    }

    public void setActionBean(IModel beanModel)
    {
        this.actionBeanModel = beanModel;
    }

    public void setAction(String methodName)
    {
        this.actionMethodName = methodName;
    }

    public void setLabel(String label)
    {
        labelModel.setObject(label);
    }


    private final class ButtonAjaxLink extends AjaxLink
    {
        private static final long serialVersionUID = -5558217133030680162L;

        public ButtonAjaxLink(String wicketId)
        {
            super(wicketId);
        }

        @Override
        public void onClick(AjaxRequestTarget target)
        {
            if (actionBeanModel == null || actionMethodName == null) {
                return;
            }

            Object actionBean = actionBeanModel.getObject();
            if (actionBean == null) {
                throw new RuntimeException("actionBean is null for Button");
            }

            try {
                MethodUtils.invokeExactMethod(actionBean, actionMethodName, NO_ARGS);
            }
            catch (Exception e) {
                throw new RuntimeException("Error invoking method '" + actionMethodName + "' on action bean "
                                + actionBean.getClass(), e);
            }
        }
    }
}
