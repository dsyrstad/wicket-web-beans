/*---
   Copyright 2006-2007 Visual Systems Corporation.
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

package wicket.contrib.webbeans.containers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import wicket.contrib.webbeans.actions.BeanActionButton;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.contrib.webbeans.model.ElementMetaData;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigatorLabel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Displays a list of beans as an editable or viewable table. <p>
 * 
 * @author Dan Syrstad
 */
public class BeanTablePanel extends Panel
{
    private static final long serialVersionUID = -5452855853289381110L;
    private BeanMetaData metaData;

    /**
     * Construct a new BeanTablePanel.
     *
     * @param id the Wicket id for the editor.
     * @param model the model, which must return a List for its object.
     * @param metaData the meta data for the bean/row.
     * @param numRows the number of rows to be displayed.
     */
    public BeanTablePanel(String id, IModel model, BeanMetaData metaData, int numRows)
    {
        this(id, model, metaData, metaData.isViewOnly(), numRows);
    }
    
    /**
     * Construct a new BeanTablePanel.
     *
     * @param id the Wicket id for the editor.
     * @param model the model, which must return a List for its object.
     * @param metaData the meta data for the bean/row.
     * @param numRows the number of rows to be displayed.
     */
    public BeanTablePanel(String id, IModel model, BeanMetaData metaData, boolean viewOnly, int numRows)
    {
        super(id, model);

        this.metaData = metaData;
        List<IColumn> columns = new ArrayList<IColumn>();

        for (ElementMetaData element : metaData.getDisplayedElements()) {
            columns.add(new BeanElementColumn(element, this) );
        }

        final BeanDataTable table = new BeanDataTable("t", columns, 
                    new BeanSortableDataProvider(metaData, model), numRows);
        add(table);

        final NavigatorLabel navigatorLabel = new NavigatorLabel("nl", table);
        navigatorLabel.setOutputMarkupId(true);
        add( navigatorLabel );
        add( new AjaxPagingNavigator("np", table) {
            protected void onAjaxEvent(AjaxRequestTarget target)
            {
                super.onAjaxEvent(target);
                target.addComponent(table);
                target.addComponent(navigatorLabel);
            }
        });
    }

    @Override
    protected void onComponentTag(ComponentTag tag)
    {
        super.onComponentTag(tag);
        metaData.warnIfAnyParameterNotConsumed(null);
    }

    public static class BeanSortableDataProvider extends SortableDataProvider
    {
        private static final long serialVersionUID = 6712923396580294568L;

        private IModel listModel;
        private BeanMetaData metaData;
        private SortParam lastSortParam = null;
        
        public BeanSortableDataProvider(BeanMetaData metaData, IModel listModel)
        {
            this.metaData = metaData;
            this.listModel = listModel;
        }
        
        List getList()
        {
            List list = (List)listModel.getObject();
            if (list == null) {
                list = new ArrayList();
            }
            
            return list;
        }

        public Iterator iterator(int first, int count)
        {
            List list = getList();
            final SortParam sortParam = getSort();
            if (sortParam != null) {
                if (lastSortParam == null || 
                    !lastSortParam.getProperty().equals(sortParam.getProperty()) ||
                     lastSortParam.isAscending() != sortParam.isAscending()) {
                    
                    lastSortParam = new SortParam(sortParam.getProperty(), sortParam.isAscending());
                    ElementMetaData property = metaData.findElement(sortParam.getProperty());
                    Collections.sort(list, new PropertyComparator(property, sortParam.isAscending()));
                }
            }
            
            return list.subList(first, first + count).iterator();
        }

        public int size()
        {
            return getList().size();
        }

        /**
         * @see org.apache.wicket.extensions.markup.html.repeater.data.IDataProvider#model(java.lang.Object)
         */
        public IModel model(Object object)
        {
            return new Model((Serializable)object);
        }

        @Override
        public void detach()
        {
            super.detach();
            listModel.detach();
        }
    }
    
    public static class BeanElementColumn implements IColumn
    {
        private static final long serialVersionUID = -7194001360552686010L;

        private ElementMetaData element;
        private Component parentComponent;
        
        public BeanElementColumn(ElementMetaData property, Component parentComponent)
        {
            this.element = property;
            this.parentComponent = parentComponent;
        }

        public Component getHeader(String componentId)
        {
            //if (element.isAction()) {
            //    return new Label(componentId, "");
            //}
            
            return element.getLabelComponent(componentId);
        }

        public String getSortProperty()
        {
            return element.getPropertyName();
        }

        public boolean isSortable()
        {
            return !element.isAction() && Comparable.class.isAssignableFrom(element.getPropertyType());
        }

        public void populateItem(Item cellItem, String componentId, IModel rowModel)
        {
            Object bean = rowModel.getObject();
            Component component;
            if (element.isAction()) {
                Form form = (Form)parentComponent.findParent(Form.class);
                component = new BeanActionButton(componentId, element, form, bean);
            }
            else {
                component = element.getBeanMetaData().getComponentRegistry()
                        .getComponent(bean, componentId, element);
            }

            cellItem.add(component);
        }
        
        public void detach()
        {
            
        }
    }
    
    private static final class PropertyComparator implements Comparator
    {
        private ElementMetaData property;
        private boolean isAscending;
        
        public PropertyComparator(ElementMetaData property, boolean isAscending)
        {
            // We already know by this point that the property type is Comparable. BeanPropertyColumn.isSortable enforces this.
            this.property = property;
            this.isAscending = isAscending;
        }
        
        public int compare(Object o1, Object o2) 
        {
            Object v1 = property.getPropertyValue(o1);
            Object v2 = property.getPropertyValue(o2);
            
            if (v1 == v2) {
                return 0;
            }
            
            int rc;
            // Nulls sort smaller
            if (v1 == null) {
                rc = -1;
            }
            else if (v2 == null) {
                rc = 1;
            }
            else {
                rc = ((Comparable)v1).compareTo(v2);
            }
            
            if (!isAscending) {
                rc = -rc;
            }
            
            return rc;
        }
    }
}
