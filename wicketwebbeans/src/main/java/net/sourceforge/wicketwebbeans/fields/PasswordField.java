/*---
   Copyright 2008 Visual Systems Corporation.
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

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;


/**
 * A Password Field.
 * 
 * @author Dan Syrstad
 */
@SuppressWarnings("serial")
public class PasswordField extends AbstractField
{
    /**
     * Construct a new PasswordField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     */
    public PasswordField(String id, IModel model, ElementMetaData metaData)
    {
        super(id, model, metaData);

        Fragment fragment;
        if (false/*viewOnly*/) {
            fragment = new Fragment("frag", "viewer", this);
            fragment.add( new LabelWithMinSize("component", "*****") );
        }
        else {
            fragment = new Fragment("frag", "editor", this);

            PasswordTextField field = new PasswordTextField("component", model);
            field.setResetPassword(false);
            field.setRequired(false);
            
            setFieldParameters(field);
            fragment.add(field);
        }
        
        add(fragment);
    }
}
