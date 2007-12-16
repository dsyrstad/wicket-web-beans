/*---
   Copyright 2007 The Scripps Research Institute
   http://www.scripps.edu

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

package net.sourceforge.wicketwebbeans.databinder;

import net.databinder.DataStaticService;
import net.databinder.components.hibernate.SearchPanel;
import net.databinder.models.DatabinderProvider;
import net.databinder.models.ICriteriaBuilder;
import net.sourceforge.wicketwebbeans.containers.BeanTablePanel;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hibernate.classic.Session;

/**
 * A basic Databinder/Hibernate Bean List panel. 
 * A subclass and corresponding beanprops file are required for customization.
 * 
 * @author Mark Southern (mrsouthern)
 */
public abstract class DataBeanListPanel extends Panel
{    
    private BeanTablePanel panel;
    private BeanMetaData metaData;
    
    public DataBeanListPanel(String id, Class<?> beanClass, ICriteriaBuilder criteriaBuilder)
    {
    	this(id,beanClass.getName(),criteriaBuilder);
    }
    
    public DataBeanListPanel(String id, Class<?> beanClass)
    {
    	this(id,beanClass.getName(),null);
    }
    
    public DataBeanListPanel(String id, String beanClass)
    {
    	this(id,beanClass,null);
    }
    
    /**
     *
     * @param beanClass the fully qualified class name of the bean to be edited 
     */
	public DataBeanListPanel(String id, String beanClass, ICriteriaBuilder criteriaBuilder)
	{
	    super(id);
	    try
	    {  
	        Class<?> clazz = Class.forName(beanClass);
	        DataStaticService.getHibernateSession().beginTransaction();
    		metaData = new BeanMetaData(clazz, null, this, null, true);
    		Label label = new Label("label", new Model(metaData.getParameter("label")));
    		add(label);

            SearchPanel search = newSearchPanel("search", new Model(null), panel);
            add(search);
    		
            DataSorter sorter;
            String orderBy = metaData.getParameter("orderBy");
            if( orderBy != null && orderBy.contains(" ") )
            {
            	String[] items = orderBy.split("\\s+");
            	boolean asc = ( "desc".equalsIgnoreCase(items[1]) ? false : true );
            		sorter = new DataSorter(items[0], asc);
            }
            else
            	sorter = new DataSorter(orderBy);
    		
    		String[] filters = metaData.getParameterValues("filter");
    		DataSearchFilter filter = new DataSearchFilter(search,filters);
    		if(criteriaBuilder != null)
    			filter.addCriteriaBuilder(criteriaBuilder);
    		IDataProvider provider = new DatabinderProvider(clazz, filter,sorter);
    		panel = new BeanTablePanel("beanTable", provider, sorter, metaData, true, 20);
    		panel.setOutputMarkupId(true);
    		Form form = new Form("form");
    		add(form);
    		form.add(panel);
	    }
	    catch(Exception ex)
	    {
	        ex.printStackTrace();
	        throw new RuntimeException(ex);
	    }
	}
	
	public void edit(AjaxRequestTarget target, Form form, Object bean)
	{
		
	}
	
	public void delete(AjaxRequestTarget target, Form form, Object bean)
    {
        // confirm message shown by wwb. If we get here we do a delete
	    Session session = DataStaticService.getHibernateSession();
	    session.beginTransaction();
	    session.delete(bean);
	    session.getTransaction().commit();
	    if( target != null ) // ajax request
	        target.addComponent(panel);
    }

    /**
     * Creates instance of new search panel, override to supply your own search panel
     *  
     * @param wicketId
     * @param model
     * @param componentToAddAsTarget
     *              component which will be added to AjaxRequestTarget
     * @return the SearchPanel.
     */
    protected SearchPanel newSearchPanel(String wicketId, IModel model, final Component componentToAddAsTarget){
        SearchPanel search = new SearchPanel(wicketId, model)
        {
             public void onUpdate(AjaxRequestTarget target) {
                     target.addComponent(componentToAddAsTarget);
             }
         };
         
         return search;
    }
}