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

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Method;

/**
 * A write-only {@link PropertyProxy} for a property that is directly associated with a bean. No sub-properties or
 * property paths are supported. <p>
 * 
 * @author Dan Syrstad
 */
public class WriteOnlyPropertyProxy implements PropertyProxy
{
    private static final long serialVersionUID = 1283272898456177588L;
    static final String UNSUPPORTED_MSG = "Not Supported. Intended for updatable objects, not for objects that are listened to.";

    private Method writeMethod;
    private Class<?> parameterType;

    public WriteOnlyPropertyProxy(Method writeMethod)
    {
        this.writeMethod = writeMethod;
        this.parameterType = writeMethod.getParameterTypes()[0];
    }

    public Object getValue(Object bean)
    {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    public boolean matches(Object rootBean, PropertyChangeEvent event)
    {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    public void setValue(Object bean, Object value)
    {
        value = SessionConvertUtils.getCurrent().convert(value, parameterType);
        try {
            writeMethod.invoke(bean, value);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to invoke setter '" + writeMethod.getName() + "' on " + bean.getClass(),
                            e);
        }
    }
}
