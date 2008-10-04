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

import java.lang.reflect.Method;
import java.util.List;

import net.sourceforge.wicketwebbeans.model.BeanFactory;
import net.sourceforge.wicketwebbeans.model.ParameterValueAST;
import net.sourceforge.wicketwebbeans.model.SessionConvertUtils;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Base class for components that handle an action. Sub-class must provide html for 
 * a component with an id of "link" and sub-component of "label". <p>
 * 
 * @author Dan Syrstad
 */
abstract public class AbstractAction extends Panel
{
    private static final long serialVersionUID = -4861052632762308989L;

    private static final Object[] NO_ARGS = new Object[0];

    private IModel actionBeanModel;
    private String actionMethodName;
    private IModel labelModel = new Model();
    private List<ParameterValueAST> parameters;

    public AbstractAction(String wicketId)
    {
        super(wicketId);
        setRenderBodyOnly(true);
        AjaxLink button = new AjaxLinkWithIndirectOnClick("link");
        add(button);
        Label label = new Label("label", labelModel);
        label.setRenderBodyOnly(true);
        button.add(label);
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

    public void setActionParameters(List<ParameterValueAST> parameters)
    {
        this.parameters = parameters;
    }

    public void setLabel(String label)
    {
        labelModel.setObject(label);
    }

    private void invokeAction()
    {
        if (actionBeanModel == null || actionMethodName == null) {
            return;
        }

        Object actionBean = actionBeanModel.getObject();
        if (actionBean == null) {
            throw new RuntimeException("actionBean is null for Button");
        }

        Object[] args = NO_ARGS;
        if (parameters != null && !parameters.isEmpty()) {
            args = new Object[parameters.size()];
            int i = 0;
            for (ParameterValueAST parameter : parameters) {
                args[i++] = parameter.getValue();
            }
        }

        Method target = null;
        for (Method method : actionBean.getClass().getMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (method.getName().equals(actionMethodName) && parameterTypes.length == args.length) {
                convertArgs(args, parameterTypes);
                target = method;
            }
        }

        if (target == null) {
            throw new RuntimeException("Cannot find method '" + actionMethodName + "' on " + actionBean.getClass()
                            + " with " + args.length + " parameters");
        }

        try {
            target.invoke(actionBean, args);
        }
        catch (Exception e) {
            throw new RuntimeException("Error invoking method '" + actionMethodName + "' on action bean "
                            + actionBean.getClass(), e);
        }
    }

    private void convertArgs(Object[] args, Class<?>[] parameterTypes)
    {
        SessionConvertUtils converter = SessionConvertUtils.getCurrent();
        for (int i = 0; i < args.length; i++) {
            args[i] = converter.convert(args[i], parameterTypes[i]);
        }
    }


    private final class AjaxLinkWithIndirectOnClick extends AjaxLink
    {
        private static final long serialVersionUID = -5558217133030680162L;

        public AjaxLinkWithIndirectOnClick(String wicketId)
        {
            super(wicketId);
        }

        @Override
        public void onClick(AjaxRequestTarget target)
        {
            invokeAction();
        }
    }
}
