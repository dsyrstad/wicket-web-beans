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
import net.sourceforge.wicketwebbeans.model.ParameterValueAST;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
public class GridLayout extends Panel {
	private static final long serialVersionUID = -2149828837634944417L;

	public static final String PARAM_COLSPAN = "colspan";
	public static final String PARAM_COLS = "columns";

	private static final String PARAM_COMPONENTS = "components";

	private Object bean;
	private boolean showLabels;
	private int columns;

	/**
	 * Construct a new BeanGridPanel.
	 * 
	 * @param id
	 *            the Wicket id for the panel.
	 * @param bean
	 *            the bean to be displayed. This may be an IModel or regular
	 *            bean object.
	 * @param beanConfig
	 *            the bean config for this component.
	 */
	public GridLayout(String id, final Object bean, final BeanConfig beanConfig) {
		super(id);

		this.bean = bean;

		// Get Number of rows from config
		columns = beanConfig.getIntParameterValue(PARAM_COLS, 3);
		if (columns < 1) {
			throw new RuntimeException("Invalid columns config value: "
					+ columns);
		}

		// TODO get elements. Instantiate fields based on component registry. If
		// component implements Labeled type, get the label from it.
		// if no elements but components, get each and do newInstance
		// should have common super class that handles this for all layouts
		List<ParameterValueAST> components = beanConfig
				.getParameterValues(PARAM_COMPONENTS);
		if (components != null) {
			// Bean config must load all bean configs or somehow register them
			// maybe a separate "mount" or load url that loads them into a
			// registry of
			// beanconfigs, then the context/registry only needs to be passed
			// around. Or i'd be cool if it were discoverable via a custom
			// RequestCycle handler - e.g., a ThreadLocal is set with it.
			// or it's session/application assoc. Application has "setMetaData"
			// So does RequestCycle, but what does that mean? Does it last for the 
			// request or session?
		}

		getPage().getApplication().setMetaData(key, object);
		getRequestCycle().setMetaData(key, object)

		// Break out the rows and columns ahead of time.
		List<List<ElementMetaData>> rowsAndCols = new ArrayList<List<ElementMetaData>>();
		int colPos = 0;
		List<ElementMetaData> currRow = null;
		for (ElementMetaData element : displayedProperties) {
			int colspan = element.getIntParameter(PARAM_COLSPAN, 1);
			if (colspan < 1 || colspan > columns) {
				throw new RuntimeException("Invalid colspan parameter value: "
						+ colspan);
			}

			// If colspan > number of columns left, start a new row.
			if ((colPos + colspan) > columns) {
				colPos = 0;
			}

			if (colPos == 0) {
				currRow = new ArrayList<ElementMetaData>();
				rowsAndCols.add(currRow);
			}

			currRow.add(element);
			colPos += colspan;
			if (colPos >= columns) {
				colPos = 0;
			}
		}

		Model propModel = new Model((Serializable) rowsAndCols);
		add(new RowListView("r", propModel));
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if (bean instanceof IModel) {
			((IModel) bean).detach();
		}
	}

	private final class RowListView extends ListView {

		RowListView(String id, IModel model) {
			super(id, model);
		}

		protected void populateItem(ListItem item) {
			List<ElementMetaData> columns = (List<ElementMetaData>) item
					.getModelObject();

			item.add(new ColListView("c", new Model((Serializable) columns)));
		}
	}

	private final class ColListView extends ListView {
		ColListView(String id, IModel model) {
			super(id, model);
		}

		protected void populateItem(ListItem item) {
			ElementMetaData element = (ElementMetaData) item.getModelObject();
			int colspan = element.getIntParameter(PARAM_COLSPAN, 1);

			Component component;
			if (element.isAction()) {
				Form form = (Form) findParent(Form.class);
				component = new BeanActionButton("c", element, form, bean);
				item.add(new SimpleAttributeModifier("class",
						"beanActionButtonCell"));
			} else {
				component = beanMetaData.getComponentRegistry().getComponent(
						bean, "c", element);
				if (!(component instanceof UnlabeledField) && showLabels) {
					component = new LabeledField("c", element
							.getLabelComponent("l"), component);
				}
			}

			beanMetaData.applyCss(bean, element, component);

			item.add(new AttributeModifier(PARAM_COLSPAN, true, new Model(
					String.valueOf(colspan))));
			int pct100 = (colspan * 10000) / columns;
			String width = "width: " + (pct100 / 100) + "." + (pct100 % 100)
					+ "%;";
			item.add(new AttributeModifier("style", true, new Model(width)));
			item.add(component);
		}
	}
}
