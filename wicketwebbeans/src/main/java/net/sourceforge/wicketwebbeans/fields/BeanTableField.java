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

package net.sourceforge.wicketwebbeans.fields;

import net.sourceforge.wicketwebbeans.containers.BeanTablePanel;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;
import net.sourceforge.wicketwebbeans.model.ElementMetaData;

import org.apache.wicket.model.IModel;

/**
 * Displays a property's list as a list of beans in a table. This Field accepts a single 
 * parameter "rows" which defines the number of rows to be displayed.<p>
 * 
 * @author Dan Syrstad
 */
public class BeanTableField extends AbstractField
{
    /**
     * Construct a new BeanTableField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     */
    public BeanTableField(String id, IModel model, ElementMetaData metaData)
    {
        super(id, model, metaData);
        
        Class elementType = metaData.getElementType( model.getObject() );
        if (elementType == null) {
            throw new RuntimeException("No elementType defined for property " + metaData.getPropertyName() + " on bean " + metaData.getComponentConfig().getBeanClass());
        }
        
        ComponentConfig elementMetaData = metaData.createComponentConfig(elementType);
        
        // Get Number of rows from parameters
        int rows = metaData.getIntParameter(ElementMetaData.PARAM_ROWS, 10);

        add( new BeanTablePanel("t", model, elementMetaData, rows) );
    }
}
