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

import java.util.Arrays;

import net.sourceforge.wicketwebbeans.model.ElementMetaData;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;


/**
 * A field for Java Enum types. Presents the values as a drop-down list.
 * 
 * @author Dan Syrstad
 */
// TODO Go away - can be supported via dynamic parameter values.
public class JavaEnumField extends EnumField
{
    /**
     * Construct a new EnumField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     */
    public JavaEnumField(String id, IModel model, ElementMetaData metaData)
    {
        super(id, model, metaData, Arrays.asList(metaData.getPropertyType().getEnumConstants()));
    }
}