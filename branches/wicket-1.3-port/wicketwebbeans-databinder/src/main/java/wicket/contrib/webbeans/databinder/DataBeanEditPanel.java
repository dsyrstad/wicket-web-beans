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

package wicket.contrib.webbeans.databinder;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;

import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.contrib.webbeans.containers.BeanForm;

import net.databinder.DataStaticService;

import org.hibernate.Session;

/**
 * A basic Databinder/Hibernate Bean editing panel. 
 * A subclass and matching beanprops file are required for customization. 
 * 
 * @author Mark Southern (mrsouthern)
 */
public abstract class DataBeanEditPanel extends Panel
{
    private Page returnPage;
    private BeanForm beanForm;
    private BeanMetaData metaData;
    
    /**
     *
     * @param bean the Bean to be edited
     * @param returnPage the page to return to after saving
     */
    public DataBeanEditPanel(String id, Object bean, Page returnPage)
    {
        super(id);
	    this.returnPage = returnPage;
        metaData = new BeanMetaData(bean.getClass(), null, this, null, false);
        beanForm = new BeanForm("beanForm", bean, metaData);
        add(beanForm);
    }
    
	public void save(AjaxRequestTarget target, Form form, Object bean)
    {
	    Session session = DataStaticService.getHibernateSession();
        session.saveOrUpdate(bean);
        session.getTransaction().commit();
        returnPage.info("Saved.");
        setResponsePage(returnPage);
    }
	
	public void cancel(AjaxRequestTarget target, Form form, Object bean)
	{
	    Session session = DataStaticService.getHibernateSession();
	    session.evict(bean);
	    returnPage.info("Canceled edit.");
	    setResponsePage(returnPage);
	}
}
