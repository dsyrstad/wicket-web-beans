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
/*
 * $Id: DefaultDataTable.java 4013 2006-01-30 21:56:23 +0000 (Mon, 30 Jan 2006)
 * ivaynberg $ $Revision: 1.1 $ $Date: 2006-01-30 21:56:23 +0000 (Mon, 30 Jan
 * 2006) $
 * 
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.wicketwebbeans.containers;

import java.util.List;

import net.sourceforge.wicketwebbeans.model.BeanMetaData;

import wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import wicket.extensions.markup.html.repeater.data.IDataProvider;
import wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import wicket.extensions.markup.html.repeater.data.table.DataTable;
import wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import wicket.extensions.markup.html.repeater.data.table.IColumn;
import wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import wicket.extensions.markup.html.repeater.refreshing.Item;
import wicket.extensions.markup.html.repeater.refreshing.OddEvenItem;
import wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import wicket.model.IModel;

/**
 * An implementation of the DataTable that aims to solve the 90% usecase by
 * adding headers, an no-records-found toolbars to a standard
 * {@link DataTable}. Unlike {@link AjaxFallbackDefaultDataTable}, this class
 * does not add the {@link NavigationToolbar} as part of the table. Hence the table can scroll
 * horizontally independent of the navigation.
 * <p>
 * The {@link HeadersToolbar} is added as a top
 * toolbar, while the {@link NoRecordsToolbar} toolbar is added as a bottom
 * toolbar.
 * 
 * @see DataTable
 * @see HeadersToolbar
 * @see NavigationToolbar
 * @see NoRecordsToolbar
 * 
 * @author Igor Vaynberg ( ivaynberg )
 * @author Dan Syrstad (From AjaxFallbackDefaultDataTable).
 * @author Mark Southern (mrsouthern)
 */
public class BeanDataTable extends DataTable
{
    private static final long serialVersionUID = 1L;

    private BeanMetaData beanMetaData;

    /**
     * Constructor
     * 
     * @param id
     *            component id
     * @param columns
     *            list of columns
     * @param dataProvider
     *            data provider
     * @param rowsPerPage
     *            number of rows per page
     */
    public BeanDataTable(String id, final List<IColumn>columns, SortableDataProvider dataProvider, int rowsPerPage, BeanMetaData metaData)
    {
        this(id, (IColumn[])columns.toArray(new IColumn[columns.size()]), dataProvider, dataProvider, rowsPerPage, metaData);
    }
    
    /**
     * Constructor
     * 
     * @param id
     *            component id
     * @param columns
     *            List of columns
     * @param dataProvider
     *            data provider
     * @param sortStateLocator
     *            sorter           
     * @param rowsPerPage
     *            number of rows per page
     */
    public BeanDataTable(String id, final List<IColumn> columns,
            IDataProvider dataProvider, ISortStateLocator sortStateLocator, int rowsPerPage, BeanMetaData metaData)
    {
        this(id, (IColumn[])columns.toArray(new IColumn[columns.size()]), dataProvider, sortStateLocator, rowsPerPage, metaData);
    }
    
    /**
     * Constructor
     * 
     * @param id
     *            component id
     * @param columns
     *            array of columns
     * @param dataProvider
     *            data provider
     * @param sortStateLocator
     *            sorter           
     * @param rowsPerPage
     *            number of rows per page
     */
    public BeanDataTable(String id, final IColumn[] columns,
            IDataProvider dataProvider, ISortStateLocator sortSateLocator, int rowsPerPage, BeanMetaData metaData)
    {
        super(id, columns, dataProvider, rowsPerPage);
        this.beanMetaData = metaData;
        setOutputMarkupId(true);
        setVersioned(false);
        addTopToolbar(new AjaxFallbackHeadersToolbar(this, sortSateLocator));
        addBottomToolbar(new NoRecordsToolbar(this));
    }
    
    protected Item newRowItem(String id, int index, IModel model)
    {
        Item item = new OddEvenItem(id, index, model);
        beanMetaData.applyCss(model.getObject(this), beanMetaData, item);
        return item;
    }
}