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

import org.apache.wicket.model.IModel;


/**
 * A Field that presents a property's bean inline with other fields.
 *
 * @author Dan Syrstad
 */
public class BeanInlineField extends AbstractField implements UnlabeledField
{
    /**
     * Construct a new BeanGridField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     */
    public BeanInlineField(String id, IModel model, ElementMetaData metaData)
    {
        super(id, model, metaData);
        
        BeanMetaData beanMetaData = metaData.createBeanMetaData();
        
        if (model.getObject() == null) {
            // Create a blank instance for editing.
            model.setObject( metaData.createInstance() );
        }

        add( new BeanGridPanel("beanPanel", model, beanMetaData) );
    }
}
