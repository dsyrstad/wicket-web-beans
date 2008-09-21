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

package net.sourceforge.wicketwebbeans.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;

/**
 * Tests ComponentRegistry. <p>
 * 
 * @author Dan Syrstad
 */
public class ComponentRegistryTest extends TestCase
{
    private ComponentRegistry componentRegistry = new ComponentRegistry();

    public void testCopyConstructorAndRegisterByClass()
    {
        componentRegistry.register(this.getClass(), TextField.class);
        ComponentRegistry copiedRegistry = new ComponentRegistry(componentRegistry);
        assertEquals(TextField.class, copiedRegistry.getComponentClass(this.getClass(), null));

        // Changes to copied registry should not affect original.
        copiedRegistry.register(this.getClass(), Label.class);
        copiedRegistry.register(DateFormat.class, Label.class);
        assertEquals(Label.class, copiedRegistry.getComponentClass(this.getClass(), null));
        assertEquals(Label.class, copiedRegistry.getComponentClass(DateFormat.class, null));

        assertEquals(TextField.class, componentRegistry.getComponentClass(this.getClass(), null));
        assertNull(componentRegistry.getComponentClass(DateFormat.class, null));
    }

    public void testRegisterByClassName()
    {
        componentRegistry.register("java.text.DateFormat", null, "org.apache.wicket.markup.html.form.TextField");
        // List<Date>
        componentRegistry.register("java.util.List", "java.util.Date", "org.apache.wicket.markup.html.basic.Label");
        assertEquals(TextField.class, componentRegistry.getComponentClass(DateFormat.class, null));
        assertNull(componentRegistry.getComponentClass(List.class, null));
        assertEquals(Label.class, componentRegistry.getComponentClass(List.class, Date.class));
    }

    public void testGetComponentClass()
    {
        componentRegistry.register(DateFormat.class, TextField.class);
        // List<Date>
        componentRegistry.register(List.class, Date.class, Label.class);
        // Collection of anything
        componentRegistry.register(Collection.class, null, CheckBox.class);
        // Date[]
        componentRegistry.register(Object[].class, Date.class, Label.class);

        assertEquals(TextField.class, componentRegistry.getComponentClass(DateFormat.class, null));
        assertNull(componentRegistry.getComponentClass(Object[].class, null));
        assertEquals(Label.class, componentRegistry.getComponentClass(List.class, Date.class));
        // A more specific type should match a more general registered type
        assertEquals(Label.class, componentRegistry.getComponentClass(ArrayList.class, Date.class));
        // A more specific element type should match a more general registered element type
        assertEquals(Label.class, componentRegistry.getComponentClass(ArrayList.class, java.sql.Date.class));
        // A more specific type without an element type should match a more general registered type without an element type.
        assertEquals(CheckBox.class, componentRegistry.getComponentClass(ArrayList.class, null));
    }
}
