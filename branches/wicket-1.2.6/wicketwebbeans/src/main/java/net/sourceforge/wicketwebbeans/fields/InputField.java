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

import net.sourceforge.wicketwebbeans.model.ElementMetaData;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.form.TextField;
import wicket.markup.html.panel.Fragment;
import wicket.model.IModel;


/**
 * A Field that allows entry of text, numbers, etc. depending on the property type.
 * Accepts a config of "advOnEnter={true|false}", which means: advance to the next field when the
 * Enter key is pressed.  
 * 
 * @author Dan Syrstad
 */
public class InputField extends AbstractField
{
    /**
     * Construct a new InputField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     * @param viewOnly true if the component should be view-only.
     */
    public InputField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly);

        boolean advOnEnter = metaData.getBooleanParameter("advOnEnter");
        
        Fragment fragment;
        if (viewOnly) {
            fragment = new Fragment("frag", "viewer");
            fragment.add( new LabelWithMinSize("component", model) );
        }
        else {
            fragment = new Fragment("frag", "editor");

            TextField field = new TextField("component", model, metaData.getPropertyType());
            
            if (advOnEnter) {
                field.add( new SimpleAttributeModifier("onkeypress", "return inputField_HandleEnter(this, event)") );
            }
            
            setFieldParameters(field);
            fragment.add(field);
        }
        
        add(fragment);
    }
}
