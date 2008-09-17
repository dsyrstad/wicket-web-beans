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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.wicketwebbeans.model.BeanFactory;
import net.sourceforge.wicketwebbeans.model.ParameterValueAST;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import sun.awt.ComponentFactory;

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
    private Model rowListViewModel = new Model();
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
        add(new GridListView("r", rowListViewModel));
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
        rowListViewModel.setObject((Serializable)components);
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


    private final class GridListView extends ListView
    {
        private static final long serialVersionUID = -1956048113471004675L;
        private int columnIndex;

        GridListView(String id, IModel model)
        {
            super(id, model);
        }

        protected void populateItem(ListItem item)
        {
            int itemIndex = item.getIndex();
            if (itemIndex == 0 || columnIndex >= columns) {
                columnIndex = 0;
            }
            
            if (columnIndex == 0) {
                close last row if itemIndex != 0
                start new row
            }
            
            ParameterValueAST parameterValue = (ParameterValueAST)item.getModelObject();
            int colspan = parameterValue.getSubParameterValueAsInt(SPECIAL_PARAM_COLSPAN, 1);
            if (colspan < 1 || colspan > columns) {
                throw new RuntimeException("Invalid colspan parameter value: " + colspan);
            }

            Component component = getBeanFactory().resolveComponent("c", parameterValue);
            String fragmentId = component instanceof FormComponent ? "inputComponent" : "spanComponent";

            Fragment componentFragment = new Fragment("frag", fragmentId, this);
            componentFragment.setRenderBodyOnly(true);
            componentFragment.add(component);

            int pct100 = (colspan * 10000) / columns;
            String width = "width: " + (pct100 / 100) + "." + (pct100 % 100) + "%;";
            item.add(new AttributeModifier("style", true, new Model(width)));
            if (colspan != 1) {
                item.add(new AttributeModifier("colspan", true, new Model(String.valueOf(colspan))));
            }

            item.add(componentFragment);
        }
    }
}
