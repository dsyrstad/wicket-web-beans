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
package net.sourceforge.wicketwebbeans.model.annotations;

import static net.sourceforge.wicketwebbeans.annotations.Property.EMPTY;
import net.sourceforge.wicketwebbeans.annotations.Action;
import net.sourceforge.wicketwebbeans.annotations.Bean;
import net.sourceforge.wicketwebbeans.annotations.Beans;
import net.sourceforge.wicketwebbeans.annotations.Property;
import net.sourceforge.wicketwebbeans.fields.BeanGridField;
import net.sourceforge.wicketwebbeans.fields.BeanInlineField;
import net.sourceforge.wicketwebbeans.fields.TextAreaField;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

@Beans({
    @Bean(type = AnnotationTestBean.class, label = "My ${TestBean.title} Title",
          propertyNames = { "action.save", "firstName", "lastName",
            EMPTY, "activePrimitive", "color", "inlineBean",
            "dateTimestamp", "blockBean", "testBean2", "popupBean",
            "-subComponent" },
          properties = {
            @Property(name = "inlineBean", fieldType = BeanInlineField.class, colspan = 3),
            @Property(name = "blockBean", fieldType = BeanGridField.class, colspan = 3),
            @Property(name = "testBean2", colspan = 3),
            @Property(name = "popupBean", colspan = 3),
            @Property(name = "result"),
            @Property(name = "palette", elementType =  AnnotationTestBean.ColorEnum.class),
            @Property(name = "palette2", elementType =  AnnotationTestBean.ColorEnum.class),
            @Property(name = "description", fieldType = TextAreaField.class, rows = 5),
            @Property(name = "beans", rows = 20)
          },
          actions = {
            @Action(name = "save", colspan = 3),
            @Action(name = "addRow", colspan = 3)
          }
    ),
     
    @Bean(type = AnnotationTestBean.class, context = "view", 
          label = "Bean View",
          properties = {
            @Property(name = "action.save", colspan = 4),
            @Property(name = "firstName")
          }
    ),
     
    @Bean(type = AnnotationTestBean2.class, actionNames = "-doIt",
          properties = {
            @Property(name = "action.deleteRow", labelImage = "remove.gif"),
            @Property(name = "selected", label = "X"),
            @Property(name = "firstName"),
            @Property(name = "lastName")         
          }
    )
})
public class AnnotationTestPage extends WebPage
{
    public AnnotationTestPage()
    {
    }
    
    @Action(ajax = true)
    public void save(AjaxRequestTarget target, Form form, AnnotationTestBean bean)
    {
    }

    public void cancel(AjaxRequestTarget target, Form form, AnnotationTestBean bean)
    {
    }

    public void deleteRow(AjaxRequestTarget target, Form form, AnnotationTestBean2 rowBean)
    {
    }

    public void addRow(AjaxRequestTarget target, Form form, AnnotationTestBean rowBean)
    {
    }

    // Test generic Object
    public void doIt(AjaxRequestTarget target, Form form, Object rowBean)
    {
    }
}