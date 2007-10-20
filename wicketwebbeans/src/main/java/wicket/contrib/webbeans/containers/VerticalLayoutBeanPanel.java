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
import java.util.List;

import wicket.behavior.SimpleAttributeModifier;
import wicket.contrib.webbeans.actions.BeanActionButton;
import wicket.contrib.webbeans.model.BeanMetaData;
import wicket.contrib.webbeans.model.ElementMetaData;
import wicket.contrib.webbeans.model.TabMetaData;
import wicket.markup.ComponentTag;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Form;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * A panel for generically displaying Java Beans in a vertical layout.
 * 
 * @author Dan Syrstad
 */
public class VerticalLayoutBeanPanel extends Panel
{
    private static final long serialVersionUID = -2149828837634944417L;

    private Object bean; 
    private BeanMetaData beanMetaData;
    private TabMetaData tabMetaData;
    private boolean showLabels;

    /**
     * Construct a new VerticalLayoutBeanPanel.
     *
     * @param id the Wicket id for the panel.
     * @param bean the bean to be displayed. This may be an IModel or regular bean object.
     * @param beanMetaData the meta data for the bean
     * @param groupMetaData the tab to be displayed. If this is null, all displayed properties 
     *  for the bean will be displayed.
     */
    public VerticalLayoutBeanPanel(String id, final Object bean, final BeanMetaData beanMetaData, TabMetaData groupMetaData)
    {
        this(id, bean, beanMetaData, groupMetaData, true);
    }
    
    /**
     * Construct a new VerticalLayoutBeanPanel.
     *
     * @param id the Wicket id for the panel.
     * @param bean the bean to be displayed. This may be an IModel or regular bean object.
     * @param beanMetaData the meta data for the bean
     * @param tabMetaData the tab to be displayed. If this is null, all displayed properties 
     *  for the bean will be displayed.
     * @param showLabels if true, property labels will be displayed, otherwise they won't. 
     */
    public VerticalLayoutBeanPanel(String id, final Object bean, final BeanMetaData beanMetaData, TabMetaData tabMetaData, 
                final boolean showLabels)
    {
        super(id);

        this.bean = bean;
        this.beanMetaData = beanMetaData;
        this.tabMetaData = tabMetaData;
        this.showLabels = showLabels;

        List<ElementMetaData> displayedProperties;
        if (tabMetaData == null) {
            displayedProperties = beanMetaData.getDisplayedElements();
        }
        else {
            displayedProperties = beanMetaData.getTabElements(tabMetaData);
        }
        
        Model propModel = new Model((Serializable)displayedProperties);
        add( new RowListView("r", propModel) );
    }

    @Override
    public void detachModels()
    {
        super.detachModels();
        if (bean instanceof IModel) {
            ((IModel)bean).detach();
        }
    }

    @Override
    protected void onComponentTag(ComponentTag tag)
    {
        super.onComponentTag(tag);
        beanMetaData.warnIfAnyParameterNotConsumed(tabMetaData);
    }
    
    private final class RowListView extends ListView
    {
        RowListView(String id, IModel model)
        {
            super(id, model);
        }

        protected void populateItem(ListItem item)
        {
            ElementMetaData element = (ElementMetaData)item.getModelObject();
            
            if (element.isAction()) {
                Form form = (Form)findParent(Form.class);
                item.add( new SimpleAttributeModifier("class", "beanActionButtonCell") );
                item.add( new Label("l", "") );
                item.add( new BeanActionButton("c", element, form, bean) );
            }
            else {
                item.add(showLabels ? element.getLabelComponent("l") : new Label("l", ""));
                item.add( beanMetaData.getComponentRegistry().getComponent(bean, "c", element) );
            }
        }
    }
}
