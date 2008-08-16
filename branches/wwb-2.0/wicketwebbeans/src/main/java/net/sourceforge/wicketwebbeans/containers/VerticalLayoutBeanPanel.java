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
package net.sourceforge.wicketwebbeans.containers;

import java.io.Serializable;
import java.util.List;

import net.sourceforge.wicketwebbeans.actions.BeanActionButton;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import net.sourceforge.wicketwebbeans.model.ElementMetaData;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


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
    private boolean showLabels;

    /**
     * Construct a new VerticalLayoutBeanPanel.
     *
     * @param id the Wicket id for the panel.
     * @param bean the bean to be displayed. This may be an IModel or regular bean object.
     * @param beanMetaData the meta data for the bean
     */
    public VerticalLayoutBeanPanel(String id, final Object bean, final BeanMetaData beanMetaData)
    {
        this(id, bean, beanMetaData, true);
    }
    
    /**
     * Construct a new VerticalLayoutBeanPanel.
     *
     * @param id the Wicket id for the panel.
     * @param bean the bean to be displayed. This may be an IModel or regular bean object.
     * @param beanMetaData the meta data for the bean
     * @param showLabels if true, property labels will be displayed, otherwise they won't. 
     */
    public VerticalLayoutBeanPanel(String id, final Object bean, final BeanMetaData beanMetaData, final boolean showLabels)
    {
        super(id);

        this.bean = bean;
        this.beanMetaData = beanMetaData;
        this.showLabels = showLabels;

        beanMetaData.applyCss(bean, beanMetaData, this);

        List<ElementMetaData> displayedProperties = beanMetaData.getDisplayedElements();
        
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
        beanMetaData.warnIfAnyParameterNotConsumed();
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

            beanMetaData.applyCss(bean, element, item);
        }
    }
}
