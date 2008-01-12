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

import org.apache.wicket.Page;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;


import net.databinder.DataStaticService;
import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;

import org.hibernate.Session;

/**
 * A basic Databinder/Hibernate Bean editing panel. A subclass and matching
 * beanprops file are required for customization.
 * 
 * @author Mark Southern (mrsouthern)
 * @author Vitek Rozkovec - replacement component
 */
public abstract class DataBeanEditPanel extends Panel {
	private Page returnPage;
	private Component replacementComponent;
	private BeanForm beanForm;
	private BeanMetaData metaData;

	/**
	 * @param id
	 *            wicket id
	 * @param bean
	 *            the Bean to be edited
	 * @param replacementComponent
	 *            component which will be switched with this panel after save
	 */
	public DataBeanEditPanel(String id, Object bean,
			Component replacementComponent) {
		this(id, bean);
		this.replacementComponent = replacementComponent;
	}

	/**
	 * @param id
	 *            wicket id
	 * @param bean
	 *            the Bean to be edited
	 * @param returnPage
	 *            the page to return to after saving
	 */
	public DataBeanEditPanel(String id, Object bean, Page returnPage) {
		this(id, bean);
		this.returnPage = returnPage;
	}

	private DataBeanEditPanel(String id, Object bean) {
		super(id);
		metaData = new BeanMetaData(bean.getClass(), null, this, null, false);
		beanForm = new BeanForm("beanForm", bean, metaData);
		add(beanForm);
	}

	public void save(AjaxRequestTarget target, Form form, Object bean) {
		Session session = DataStaticService.getHibernateSession();
		session.saveOrUpdate(bean);
		session.getTransaction().commit();
		if (returnPage != null) {
			returnPage.info("Saved.");
			setResponsePage(returnPage);
		} else {
			getPage().info("Saved.");
			if (replacementComponent != null)
				replaceWith(replacementComponent);
		}

	}

	public void cancel(AjaxRequestTarget target, Form form, Object bean) {
		DataStaticService.getHibernateSession().evict(bean);
		if (returnPage != null) {
			returnPage.info("Canceled edit.");
			setResponsePage(returnPage);
		} else {
			getPage().info("Canceled edit.");
			if (replacementComponent != null)
				replaceWith(replacementComponent);
		}
	}
}