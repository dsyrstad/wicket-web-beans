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
 * 
 */
package net.sourceforge.wicketwebbeans.fields;

import net.sourceforge.wicketwebbeans.containers.BeanGridPanel;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import net.sourceforge.wicketwebbeans.model.ElementMetaData;
import wicket.model.IModel;


/**
 * A Field that presents a property's bean in a BeanGridPanel. 
 *
 * @author Dan Syrstad
 */
public class BeanGridField extends AbstractField
{
    /**
     * Construct a new BeanGridField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     * @param viewOnly true if the component should be view-only.
     */
    public BeanGridField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly);
        
        BeanMetaData beanMetaData = metaData.createBeanMetaData(viewOnly);
        
        if (!viewOnly && model.getObject(this) == null) {
            // Create a blank instance for editing.
            model.setObject(this, metaData.createInstance() );
        }

        add( new BeanGridPanel("p", model, beanMetaData) );
    }
}