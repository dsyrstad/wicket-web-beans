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
import java.beans.PropertyChangeListener;

/**
 * Binds a property on one bean to a property on another bean. If the first bean's 
 * property changes, the second bean's property is updated. If an Ajax-based RequestCycle
 * is active and the second bean is a Wicket Component, the component is added as
 * a component to update.<p>
 *  
 * The first bean must support ProperyChangeListeners as specified by the JavaBeans
 * spec in order for the updating to work.<p>
 * 
 * A weak reference is made to both beans so that they can be collected independent
 * of this object. If either bean is collected, this object is essentially dead.
 * If the second bean is collected first, this object will remove itself as a
 * listener to the first bean.<p>
 * 
 * TODO Later: Doing the Wicket component/RequestCycle here may need to be revisited
 * later. It might be better to isolate this via another interface.
 * 
 * @author Dan Syrstad
 */
public class PropertyBinder implements PropertyChangeListener
{
    /**
     * Construct a PropertyBinder. See {@link #create(Object, Object, PropertyProxy, PropertyProxy, String)}.
     */
    private PropertyBinder(Object listenBean, Object updateBean, PropertyProxy listenProperty,
                    PropertyProxy updateProperty, String eventPropertyName)
    {

    }

    /**
     * Construct a PropertyBinder. 
     *
     * @param listenBean the bean to listen to. For successful creation this bean must
     *  support {@link PropertyChangeListener}s via the JavaBeans addPropertyChangeListener(). 
     * @param updateBean the bean to be updated when listenBean's listenProperty changes.
     * @param listenProperty the property on listenBean to listen to.
     * @param updateProperty the property on updateBean to update.
     * @param eventPropertyName the property's name. PropertyChangeEvents are filtered
     *  based on this name.
     * 
     * @return a new PropertyBinder or null if listenBean cannot be listened to.
     */
    public PropetyBinder create(Object listenBean, Object updateBean, PropertyProxy listenProperty,
                    PropertyProxy updateProperty, String eventPropertyName)
    {
        return null;
    }

    /** 
     * {@inheritDoc}
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        // TODO Auto-generated method stub

    }
}
