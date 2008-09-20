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

import junit.framework.TestCase;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Tests IModelConverter. <p>
 * 
 * @author Dan Syrstad
 */
public class IModelConverterTest extends TestCase
{
    private IModelConverter converter = new IModelConverter();

    public void testGetDefaultType()
    {
        assertEquals(Model.class, converter.getDefaultType());
    }

    public void testConvert()
    {
        IModel model = (IModel)converter.convert(IModel.class, "test");
        assertEquals("test", model.getObject());
    }

    public void testConvertWhenAlreadyIModel()
    {
        IModel origModel = new Model();
        IModel model = (IModel)converter.convert(IModel.class, origModel);
        assertSame(origModel, model);
    }

    public void testConvertWhenNull()
    {
        IModel model = (IModel)converter.convert(IModel.class, null);
        assertNull(model);
    }

    public void testConvertWhenNotSerializable()
    {
        IModel model = (IModel)converter.convert(IModel.class, new IModelConverterTest());
        assertEquals(
                        "Cannot convert a value of class net.sourceforge.wicketwebbeans.model.IModelConverterTest to interface org.apache.wicket.model.IModel. The object must be Serailizable.",
                        model.getObject());
    }
}
