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
package wicket.contrib.webbeans.containers;

import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.markup.repeater.data.IDataProvider;

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
    public BeanDataTable(String id, final List<IColumn>columns, ISortableDataProvider dataProvider, int rowsPerPage)
    {
        this(id, (IColumn[])columns.toArray(new IColumn[columns.size()]), dataProvider, dataProvider, rowsPerPage);
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
            IDataProvider dataProvider, ISortStateLocator sortStateLocator, int rowsPerPage)
    {
        this(id, (IColumn[])columns.toArray(new IColumn[columns.size()]), dataProvider, sortStateLocator, rowsPerPage);
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
            IDataProvider dataProvider, ISortStateLocator sortSateLocator, int rowsPerPage)
    {
        super(id, columns, dataProvider, rowsPerPage);
        setOutputMarkupId(true);
        setVersioned(false);
        addTopToolbar(new AjaxFallbackHeadersToolbar(this, sortSateLocator));
        addBottomToolbar(new NoRecordsToolbar(this));
    }
    
    protected Item newRowItem(String id, int index, IModel model)
    {
        return new OddEvenItem(id, index, model);
    }
}