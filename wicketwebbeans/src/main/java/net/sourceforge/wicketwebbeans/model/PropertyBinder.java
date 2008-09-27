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
import java.io.Serializable;
import java.lang.ref.WeakReference;

import org.apache.wicket.model.IModel;

/**
 * Binds a property on one bean to a property on another bean. If the first bean's 
 * property changes, the second bean's property is updated. Either bean may be wrapped
 * by IModels.<p>
 * 
 * A weak reference is made to both beans so that they can be collected independent
 * of this object. If either bean is collected, this object becomes inactive and
 * isActive() will return false. This binding can then be removed from any container
 * that it is contained in.<p>
 * 
 * @author Dan Syrstad
 */
public class PropertyBinder implements Serializable
{
    private static final long serialVersionUID = -440788310859881508L;

    private WeakReference<Object> listenBeanRef;
    private WeakReference<Object> updateBeanRef;
    private PropertyProxy listenProperty;
    private PropertyProxy updateProperty;

    /**
     * Construct a PropertyBinder. 
     *
     * @param listenBean the bean to listen to. This may be an {@link IModel}. 
     * @param updateBean the bean to be updated when listenBean's listenProperty changes. This may be an {@link IModel}.
     * @param listenProperty the property on listenBean to listen to.
     * @param updateProperty the property on updateBean to update.
     */
    public PropertyBinder(Object listenBean, Object updateBean, PropertyProxy listenProperty,
                    PropertyProxy updateProperty)
    {
        this.listenBeanRef = new WeakReference<Object>(listenBean);
        this.updateBeanRef = new WeakReference<Object>(updateBean);
        this.listenProperty = listenProperty;
        this.updateProperty = updateProperty;
    }

    /**
     * @return the bean being listened to. May be null if the bean has been collected.
     */
    private Object getListenBean()
    {
        checkReferences();
        Object listenBean = listenBeanRef.get();
        if (listenBean instanceof IModel) {
            listenBean = ((IModel)listenBean).getObject();
        }

        return listenBean;
    }

    /**
     * @return the bean being updated. May be null if the bean has been collected.
     */
    private Object getUpdateBean()
    {
        checkReferences();
        Object updateBean = updateBeanRef.get();
        if (updateBean instanceof IModel) {
            updateBean = ((IModel)updateBean).getObject();
        }

        return updateBean;
    }

    public boolean isActive()
    {
        return checkReferences();
    }

    /**
     * Checks if the listen bean matches the {@link PropertyChangeEvent}.
     *
     * @param event the {@link PropertyChangeEvent}. If the property name of the event is null and
     *  the source object matches any element in the property path, true will be returned. 
     *  
     * @return true if the listen bean matches the event, otherwise false.
     */
    public boolean matchesListenBean(PropertyChangeEvent event)
    {
        Object listenBean = getListenBean();
        return listenBean == null ? false : listenProperty.matches(listenBean, event);
    }

    /**
     * Set the update bean's property to be set from the listen bean's property.
     * If either bean has been collected, nothing happens.
     */
    public void updateProperty()
    {
        Object listenBean = getListenBean();
        Object updateBean = getUpdateBean();
        if (listenBean == null || updateBean == null) {
            return;
        }

        updateProperty.setValue(updateBean, listenProperty.getValue(listenBean));
    }

    /**
     * @return true if both references are set.
     */
    private boolean checkReferences()
    {
        boolean bothSet = listenBeanRef.get() != null && updateBeanRef.get() != null;
        if (!bothSet) {
            // No reason to keep either of these around if one is gone.
            updateBeanRef.clear();
            listenBeanRef.clear();
        }

        return bothSet;
    }
}
