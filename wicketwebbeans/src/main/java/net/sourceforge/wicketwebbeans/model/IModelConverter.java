/*---
   Copyright 2007 Visual Systems Corporation.
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

import java.io.Serializable;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractConverter;
import org.apache.wicket.model.Model;

/**
 * Apache ConvertUtils Converter for IModels. <p>
 * 
 * @author Dan Syrstad
 */
// TODO Test
public class IModelConverter extends AbstractConverter
{
    public IModelConverter()
    {
        setDefaultValue(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object convertToType(Class type, Object value)
    {
        if (type.isAssignableFrom(value.getClass())) {
            return value;
        }

        if (value instanceof Serializable) {
            return new Model((Serializable)value);
        }

        throw new ConversionException("Cannot convert a value of " + value.getClass() + " to " + type
                        + ". The object must be Serailizable.");
    }

    @Override
    protected Class<?> getDefaultType()
    {
        return Model.class;
    }
}
