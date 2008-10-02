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

package net.sourceforge.wicketwebbeans.containers;

import java.io.Serializable;
import java.util.List;

import net.sourceforge.wicketwebbeans.model.BeanFactory;
import net.sourceforge.wicketwebbeans.model.ParameterValueAST;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Implements a non-HTML form that sends Ajax requests for input elements as they are changed. <p>
 * 
 * @author Dan Syrstad
 */
public class AjaxForm extends Panel
{
    private static final long serialVersionUID = 5036535481084244561L;

    private ParameterValueAST componentParameterValue;
    private BeanFactory beanFactory;
    private WebMarkupContainer container;

    public AjaxForm(String wickedId)
    {
        super(wickedId);
        setRenderBodyOnly(true);

        container = new WebMarkupContainer("component");
        container.setRenderBodyOnly(true);
        add(container);
    }

    public void setComponent(ParameterValueAST componentParameterValue)
    {
        this.componentParameterValue = componentParameterValue;
    }

    public BeanFactory getBeanFactory()
    {
        return beanFactory;
    }

    /** Called by BeanFactory.newInstance(). */
    public void setBeanFactory(BeanFactory beanFactory)
    {
        this.beanFactory = beanFactory;
    }

    @Override
    protected void onBeforeRender()
    {
        if (componentParameterValue == null) {
            throw new RuntimeException("No component set on bean for " + AjaxForm.class);
        }

        Component component = getBeanFactory().resolveComponent("c", componentParameterValue);
        container.add(component);

        super.onBeforeRender();

        visitChildren(new FormComponentVisitor());
    }


    private final class FormComponentVisitor implements IVisitor, Serializable
    {
        private static final long serialVersionUID = -7777117909292944143L;

        @SuppressWarnings("unchecked")
        public Object component(Component component)
        {
            if (component instanceof FormComponent) {
                boolean addBehavior = true;
                for (IBehavior behavior : (List<IBehavior>)component.getBehaviors()) {
                    if (behavior instanceof AjaxComponentChangeBehavior) {
                        addBehavior = false;
                        break;
                    }
                }

                if (addBehavior) {
                    AjaxComponentChangeBehavior behavior = new AjaxComponentChangeBehavior();
                    component.add(behavior);
                    //component.add(new SimpleAttributeModifier("onfocus", "bfOnFocus(this)"));
                }
            }

            return IVisitor.CONTINUE_TRAVERSAL;
        }
    }


    // NOTE: OnChangeAjaxBehavior would be useful for auto-complete...
    private static final class AjaxComponentChangeBehavior extends AjaxFormComponentUpdatingBehavior
    {
        private static final long serialVersionUID = 3061887481515194883L;

        public AjaxComponentChangeBehavior()
        {
            super("onchange");
        }

        @Override
        protected void onUpdate(AjaxRequestTarget target)
        {
            target.addComponent(getComponent());
        }
    }
}
