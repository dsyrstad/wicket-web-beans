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
import java.beans.PropertyDescriptor;

import junit.framework.TestCase;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Tests {@link WriteOnlyPropertyProxy}. <p>
 * 
 * @author Dan Syrstad
 */
public class WriteOnlyPropertyProxyTest extends TestCase
{
    private WriteOnlyPropertyProxy proxy;
    private String property;

    public WriteOnlyPropertyProxyTest() throws Exception
    {
        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(this, "property");
        proxy = new WriteOnlyPropertyProxy(PropertyUtils.getWriteMethod(descriptor));
    }

    public void testGetValueThrowsException()
    {
        try {
            proxy.getValue(this);
            fail();
        }
        catch (RuntimeException e) {
            // Expected
        }
    }

    public void testMatchesThrowsException()
    {
        try {
            proxy.matches(this, new PropertyChangeEvent(this, "property", null, null));
            fail();
        }
        catch (RuntimeException e) {
            // Expected
        }
    }

    public void testSetValue()
    {
        assertNull(getProperty());
        proxy.setValue(this, "test");
        assertEquals("test", getProperty());
    }

    public void testSetValueConvertsValue()
    {
        assertNull(getProperty());
        proxy.setValue(this, Integer.valueOf(5));
        assertEquals("5", getProperty());
    }

    public void testSetValueThrowsExceptionWhenMethodInvalid() throws Exception
    {
        proxy = new WriteOnlyPropertyProxy(MethodUtils.getAccessibleMethod(this.getClass(), "someOtherMethod",
                        new Class[] { String.class, String.class }));
        try {
            proxy.setValue(this, "test");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
        }
    }

    public void setProperty(String property)
    {
        this.property = property;
    }

    public String getProperty()
    {
        return property;
    }

    public void someOtherMethod(String arg1, String arg2)
    {
    }
}
