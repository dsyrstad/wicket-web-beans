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
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * Binds a property on one bean to a property on another bean. If the first bean's 
 * property changes, the second bean's property is updated. 
 * The first bean must support ProperyChangeListeners as specified by the JavaBeans
 * spec in order for the updating to work.<p>
 * 
 * A weak reference is made to both beans so that they can be collected independent
 * of this object. If either bean is collected, this object is essentially dead.
 * If the second bean is collected first, this object will remove itself as a
 * listener to the first bean.<p>
 * 
 * @author Dan Syrstad
 */
public class PropertyBinder implements PropertyChangeListener, Serializable
{
    private static final long serialVersionUID = -440788310859881508L;

    private static final Class<?>[] PROP_CHANGE_LISTENER_ARG = new Class<?>[] { PropertyChangeListener.class };

    private WeakReference<Object> listenBeanRef;
    private WeakReference<Object> updateBeanRef;
    private PropertyProxy listenProperty;
    private PropertyProxy updateProperty;
    private String eventPropertyName;

    /**
     * Construct a PropertyBinder. 
     *
     * @param listenBean the bean to listen to. For successful construction this bean must
     *  support {@link PropertyChangeListener}s via the JavaBeans addPropertyChangeListener(). 
     * @param updateBean the bean to be updated when listenBean's listenProperty changes.
     * @param listenProperty the property on listenBean to listen to.
     * @param updateProperty the property on updateBean to update.
     * @param eventPropertyName the property's name. PropertyChangeEvents are filtered
     *  based on this name.
     */
    public PropertyBinder(Object listenBean, Object updateBean, PropertyProxy listenProperty,
                    PropertyProxy updateProperty, String eventPropertyName)
    {

        // TODO WAIT! This must be the parent object of the nested property -- See Pointer. if null, how do we monitor?????
        // TODO If dependent property in path changes, how do we monitor?
        Method addPropertyChangeListener = getAddPropertyChangeListenerMethod(listenBean.getClass());
        if (addPropertyChangeListener == null) {
            return;
        }

        try {
            addPropertyChangeListener.invoke(listenBean, this);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not add PropertyChangeListener to " + listenBean.getClass());
        }

        this.listenBeanRef = new WeakReference<Object>(listenBean);
        this.updateBeanRef = new WeakReference<Object>(updateBean);
        this.listenProperty = listenProperty;
        this.updateProperty = updateProperty;
        this.eventPropertyName = eventPropertyName;
    }

    /**
     * @return the bean being listened to. May be null if the bean has been collected.
     */
    public Object getListenBean()
    {
        checkReferences();
        return listenBeanRef.get();
    }

    /**
     * @return the bean being updated. May be null if the bean has been collected.
     */
    public Object getUpdateBean()
    {
        checkReferences();
        return updateBeanRef.get();
    }

    private void checkReferences()
    {
        Object listenBean = listenBeanRef.get();
        if (listenBean == null) {
            // No reason to keep this around.
            updateBeanRef.clear();
        }

        if (updateBeanRef.get() == null) {
            if (listenBean != null) {
                Method removePropertyChangeListener = getRemovePropertyChangeListenerMethod(listenBean.getClass());
                if (removePropertyChangeListener != null) {
                    try {
                        removePropertyChangeListener.invoke(listenBean, this);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // No reason to keep this around.
            listenBeanRef.clear();
        }
    }

    public PropertyProxy getListenProperty()
    {
        return listenProperty;
    }

    public PropertyProxy getUpdateProperty()
    {
        return updateProperty;
    }

    public String getEventPropertyName()
    {
        return eventPropertyName;
    }

    private static Method getAddPropertyChangeListenerMethod(Class<?> listenBeanClass)
    {
        try {
            return listenBeanClass.getMethod("addPropertyChangeListener", PROP_CHANGE_LISTENER_ARG);
        }
        catch (Exception e) {
            // Assume we don't have it.
            return null;
        }
    }

    private static Method getRemovePropertyChangeListenerMethod(Class<?> listenBeanClass)
    {
        try {
            return listenBeanClass.getMethod("removePropertyChangeListener", PROP_CHANGE_LISTENER_ARG);
        }
        catch (Exception e) {
            // Assume we don't have it.
            return null;
        }
    }

    /** 
     * {@inheritDoc}
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event)
    {
        // According to the spec a null property name means multiple properties may have changed. Assume the one we're interested in did
        // as well.
        Object updateBean = getUpdateBean();
        Object listenBean = getListenBean();
        String propertyName = event.getPropertyName();
        if (updateBean != null && listenBean != null
                        && (propertyName == null || eventPropertyName.equals(propertyName))) {
            Object value = listenProperty.getValue(listenBean);
            // TODO Need a ConvertUtils reference.
            updateProperty.setValue(updateBeanRef, value);
        }

    }
}
