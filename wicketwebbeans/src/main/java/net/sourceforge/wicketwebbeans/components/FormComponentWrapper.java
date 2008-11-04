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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

/**
 * Wraps a FormComponent so that it can be handled like any other Wicket component. 
 * FormComponents are special because they are required to be placed on an INPUT tag.
 * The wrapped component has the same Wicket id as the original.<p>
 * 
 * @author Dan Syrstad
 */
public class FormComponentWrapper extends Panel
{
    private static final long serialVersionUID = 1720694533684915414L;

    private Component wrappedComponent;

    /**
     * Construct a FormComponentWrapper. 
     *
     * @param id
     */
    public FormComponentWrapper(String id, Component component)
    {
        super(id);
        this.wrappedComponent = component;
        RepeatingView repeatingView = new RepeatingView("c");
        add(repeatingView);
        repeatingView.add(component);
    }

    public static Component wrapIfFormComponent(Component component)
    {
        if (component instanceof FormComponent) {
            return new FormComponentWrapper(component.getId(), component);
        }

        return component;
    }

    public Component getWrappedComponent()
    {
        return wrappedComponent;
    }

}
