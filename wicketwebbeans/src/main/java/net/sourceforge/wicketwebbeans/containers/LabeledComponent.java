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

import net.sourceforge.wicketwebbeans.model.BeanFactory;
import net.sourceforge.wicketwebbeans.model.ParameterValueAST;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * A container that lays out a Label and component either vertically or horizontally. <p>
 * 
 * @author Dan Syrstad
 */
public class LabeledComponent extends Panel
{
    private static final long serialVersionUID = -5403746532321790638L;

    private String label;
    private ParameterValueAST component;
    private boolean horizontal;
    private BeanFactory beanFactory;

    public LabeledComponent(String id)
    {
        super(id);
    }

    public void setComponent(ParameterValueAST component)
    {
        this.component = component;
    }

    public ParameterValueAST getComponent()
    {
        return component;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setHorizontal(boolean horizontal)
    {
        this.horizontal = horizontal;
    }

    public boolean isHorizontal()
    {
        return horizontal;
    }

    public void setBeanFactory(BeanFactory beanFactory)
    {
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory()
    {
        return beanFactory;
    }

    @Override
    protected void onBeforeRender()
    {
        super.onBeforeRender();
        Label labelComponent = new Label("label", label);
        add(labelComponent);

        Component child = getBeanFactory().resolveComponent("component", component);
        add(child);
    }
}
