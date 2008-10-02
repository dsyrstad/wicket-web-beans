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

package net.sourceforge.wicketwebbeans.model.jxpath;

import java.beans.PropertyChangeEvent;

import net.sourceforge.wicketwebbeans.model.PropertyPathBeanCreator;
import net.sourceforge.wicketwebbeans.model.PropertyProxy;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * A PropertyProxy for Apache Commons JXPath. Created by JXPathPropertyResolver. <p>
 * 
 * @author Dan Syrstad
 */
public class JXPathPropertyProxy implements PropertyProxy
{
    private static final long serialVersionUID = -504835121389856794L;

    private String propertyExpression;
    private JXPathObjectFactory objectFactory;
    transient private CompiledExpression compiledExpression;

    JXPathPropertyProxy(PropertyPathBeanCreator beanCreator, String propertyExpression)
    {
        this.objectFactory = new JXPathObjectFactory(beanCreator);
        this.propertyExpression = propertyExpression;
    }

    /** 
     * {@inheritDoc}
     * @see net.sourceforge.wicketwebbeans.model.PropertyProxy#getValue(org.apache.wicket.model.IModel)
     */
    public Object getValue(Object bean)
    {
        if (bean == null) {
            return bean;
        }

        JXPathContext context = getContext(bean);
        return getCompiledExpression().getValue(context);
    }

    private JXPathContext getContext(Object bean)
    {
        // TODO use a pre-configured super-context without a bean to create these contexts. 
        JXPathContext context = JXPathContext.newContext(bean);
        // LATER - Unfortunately unrecognized properties are not caught by this.
        context.setLenient(true);
        context.setFactory(objectFactory);
        return context;
    }

    public void setValue(Object bean, Object value)
    {
        // TODO JXPath supports conversion of the value to the proper type. Maybe we should use it.
        // TODO create the path, determine the property type (see JXPathObjectFactory), then set the value.  Test conversion
        //        value = SessionConvertUtils.getCurrent().convert(value, parameterType);
        getCompiledExpression().createPathAndSetValue(getContext(bean), value);
    }

    private CompiledExpression getCompiledExpression()
    {
        if (compiledExpression == null) {
            compiledExpression = JXPathContext.compile(propertyExpression);
        }

        return compiledExpression;
    }

    public boolean matches(Object rootBean, PropertyChangeEvent event)
    {
        Object eventSource = event.getSource();
        // This may be null which means match any property.
        String eventPropertyName = event.getPropertyName();

        JXPathContext context = getContext(rootBean);
        String previousPropertyName = null;
        Pointer previousPointer = null;
        for (NodePointer pointer = (NodePointer)context.getPointer(propertyExpression); pointer != null; pointer = pointer
                        .getParent()) {

            if (previousPointer != null && pointer.getValue() == eventSource
                            && (eventPropertyName == null || eventPropertyName.equals(previousPropertyName))) {
                return true;
            }

            previousPointer = pointer;
            QName propertyQName = pointer.getName();
            if (propertyQName != null) {
                previousPropertyName = propertyQName.getName();
            }
        }

        return false;
    }
}
