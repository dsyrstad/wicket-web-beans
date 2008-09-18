/*---
   Copyright 2006-2008 Visual Systems Corporation.
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

import java.util.List;

import net.sourceforge.wicketwebbeans.model.BeanFactory;
import net.sourceforge.wicketwebbeans.model.ParameterValueAST;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

/**
 * A panel for generically displaying Components in a grid-style layout. The
 * Bean config may specify the number of columns as "columns". The default is 3.
 * Elements within the grid may specify a config parameter of "_colspan" which
 * indicates the number of columns to span in the grid.
 * 
 * @author Dan Syrstad
 */
public class GridLayout extends Panel
{
    private static final long serialVersionUID = -2149828837634944417L;

    public static final String SPECIAL_PARAM_COLSPAN = "_colspan";

    private int columns = 3;
    private List<ParameterValueAST> components;
    private BeanFactory beanFactory;

    /**
     * Construct a new BeanGridPanel.
     * 
     * @param id
     *            the Wicket id for the panel.
     */
    public GridLayout(String id)
    {
        super(id);
        add(new LayoutView("r"));
    }

    public int getColumns()
    {
        return columns;
    }

    public void setColumns(int columns)
    {
        if (columns < 1) {
            throw new RuntimeException("Invalid columns parameter value: " + columns);
        }

        this.columns = columns;
    }

    public void setComponents(List<ParameterValueAST> components)
    {
        this.components = components;
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


    private final class LayoutView extends RepeatingView
    {
        private static final long serialVersionUID = -1956048113471004675L;

        LayoutView(String id)
        {
            super(id);
        }

        @Override
        protected void onPopulate()
        {
            //removeAll();

            int columnIndex = 0;
            int rowStartIndex = 0;
            int componentIndex = 0;
            for (ParameterValueAST componentParameterValue : components) {
                if (columnIndex >= columns) {
                    columnIndex = 0;
                    WebMarkupContainer container = new WebMarkupContainer(newChildId());
                    add(container);
                    container.add(new ColumnView("c", rowStartIndex, componentIndex));
                    rowStartIndex = componentIndex;
                }

                int colspan = componentParameterValue.getSubParameterValueAsInt(SPECIAL_PARAM_COLSPAN, 1);
                if (colspan < 1 || colspan > columns) {
                    throw new RuntimeException("Invalid colspan parameter value: " + colspan);
                }

                columnIndex += colspan;
                ++componentIndex;
            }

            if (rowStartIndex < componentIndex) {
                WebMarkupContainer container = new WebMarkupContainer(newChildId());
                add(container);
                container.add(new ColumnView("c", rowStartIndex, componentIndex));
            }
        }
    }


    private final class ColumnView extends RepeatingView
    {
        private static final long serialVersionUID = -1956048113471004675L;
        private int startIndex;
        private int endIndex;

        ColumnView(String id, int startIndex, int endIndex)
        {
            super(id);
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        protected void onPopulate()
        {
            for (int i = startIndex; i < endIndex; i++) {
                ParameterValueAST componentParameterValue = components.get(i);

                Component component = getBeanFactory().resolveComponent("c", componentParameterValue);

                WebMarkupContainer container = new WebMarkupContainer(newChildId());
                add(container);

                String fragmentId = component instanceof FormComponent ? "ci" : "c";
                Fragment componentFragment = new Fragment("frag", fragmentId, this);
                componentFragment.setRenderBodyOnly(true);
                componentFragment.add(component);
                container.add(componentFragment);

                int colspan = componentParameterValue.getSubParameterValueAsInt(SPECIAL_PARAM_COLSPAN, 1);
                int pct100 = (colspan * 10000) / columns;
                String width = "width: " + (pct100 / 100) + "." + (pct100 % 100) + "%;";
                container.add(new AttributeModifier("style", true, new Model(width)));
                if (colspan != 1) {
                    container.add(new AttributeModifier("colspan", true, new Model(String.valueOf(colspan))));
                }
            }
        }
    }
}
