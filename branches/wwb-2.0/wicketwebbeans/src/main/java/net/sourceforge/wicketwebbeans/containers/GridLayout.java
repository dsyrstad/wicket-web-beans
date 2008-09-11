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

import net.sourceforge.wicketwebbeans.model.BeanConfig;
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

/**
 * A panel for generically displaying Java Beans in a grid-style layout. The
 * Bean config may specify the number of columns as "cols". The default is 3.
 * Elements within the grid may specify a config parameter of "colspan" which
 * indicates the number of columns to span in the grid. These parameters, along
 * with EMPTY fields, allow for flexible layout.
 * 
 * @author Dan Syrstad
 */
public class GridLayout extends Panel
{
    private static final long serialVersionUID = -2149828837634944417L;

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
        add(new RowListView("r", rowListViewModel));
    }

    public int getColumns()
    {
        return columns;
    }

    public void setColumns(int columns)
    {
        this.columns = columns;
    }

    public void setComponents(List<ParameterValueAST> components)
    {
        List<BeanConfig> gridComponents = new ArrayList<BeanConfig>();
        if (components != null) {
            for (ParameterValueAST componentParam : components) {
                // TODO should be refactored to common code. This will eventually handle properties/ComponentRegistry
                String componentName = componentParam.getValue();
                BeanConfig componentConfig = getBeanFactory().getBeanConfig(componentName,
                                componentParam.getSubParameters());
                if (componentConfig == null) {
                    throw new RuntimeException("Cannot find bean named " + componentName);
                }

                gridComponents.add(componentConfig);
            }
        }

        // Break out the rows and columns ahead of time.
        List<List<BeanConfig>> rowsAndCols = new ArrayList<List<BeanConfig>>();
        int colPos = 0;
        List<BeanConfig> currRow = null;
        for (BeanConfig component : gridComponents) {
            int colspan = 1; // TODO Check component property, was: component.getIntParameterValue(PARAM_COLSPAN, 1);
            if (colspan < 1 || colspan > columns) {
                throw new RuntimeException("Invalid colspan parameter value: " + colspan);
            }

            // If colspan > number of columns left, start a new row.
            if ((colPos + colspan) > columns) {
                colPos = 0;
            }

            if (colPos == 0) {
                currRow = new ArrayList<BeanConfig>();
                rowsAndCols.add(currRow);
            }

            currRow.add(component);
            colPos += colspan;
            if (colPos >= columns) {
                colPos = 0;
            }
        }

        rowListViewModel.setObject((Serializable)rowsAndCols);
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


    private final class RowListView extends ListView
    {
        private static final long serialVersionUID = 2709967037379449675L;

        RowListView(String id, IModel model)
        {
            super(id, model);
        }

        @SuppressWarnings("unchecked")
        protected void populateItem(ListItem item)
        {
            List<BeanConfig> columns = (List<BeanConfig>)item.getModelObject();
            item.add(new ColListView("c", new Model((Serializable)columns)));
        }
    }


    private final class ColListView extends ListView
    {
        private static final long serialVersionUID = -1956048113471004675L;

        ColListView(String id, IModel model)
        {
            super(id, model);
        }

        protected void populateItem(ListItem item)
        {
            BeanConfig config = (BeanConfig)item.getModelObject();
            int colspan = 1; // TODO check config param - see above, was: config.getIntParameterValue(PARAM_COLSPAN, 1);

            BeanFactory beanFactory = config.getBeanFactory();
            Class<?> componentClass = beanFactory.loadClass(config);
            String fragmentId = FormComponent.class.isAssignableFrom(componentClass) ? "inputComponent"
                            : "spanComponent";

            Fragment fragment = new Fragment("frag", fragmentId, this);
            fragment.setRenderBodyOnly(true);

            final String id = "c";
            Component component = (Component)beanFactory.newInstance(config, id);
            fragment.add(component);

            int pct100 = (colspan * 10000) / columns;
            String width = "width: " + (pct100 / 100) + "." + (pct100 % 100) + "%;";
            item.add(new AttributeModifier("style", true, new Model(width)));
            if (colspan != 1) {
                item.add(new AttributeModifier("colspan", true, new Model(String.valueOf(colspan))));
            }

            item.add(fragment);
        }
    }
}
