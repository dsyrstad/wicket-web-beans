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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Dispatches {@link PropertyChangeEvent}s to matching {@link PropertyBinder}s.
 * This is used in lieu of adding {@link PropertyChangeListener}s to each bean which
 * would require dynamically adding/removing listeners as beans are created and dereferenced.<p>
 * 
 * @author Dan Syrstad
 */
public class PropertyChangeDispatcher implements Serializable
{
    private static final long serialVersionUID = -2703015686602735485L;

    private List<PropertyBinder> binders = new LinkedList<PropertyBinder>();

    public PropertyChangeDispatcher()
    {
    }

    public void add(PropertyBinder binder)
    {
        binders.add(binder);
    }

    public void remove(PropertyBinder binder)
    {
        binders.remove(binder);
    }

    public List<PropertyBinder> getPropertyBinders()
    {
        return Collections.unmodifiableList(binders);
    }

    public void dispatch(Object source, String propertyName)
    {
        dispatch(new PropertyChangeEvent(source, propertyName, null, null));
    }

    public void dispatch(PropertyChangeEvent event)
    {
        // TODO Test cycles - I put PropertyChanger in getter which caused cycles.  Also test A -> B ->A
        // TODO also two binders to/from same propety
        for (Iterator<PropertyBinder> iter = binders.iterator(); iter.hasNext();) {
            PropertyBinder binder = iter.next();
            if (!binder.isActive()) {
                // Dead binder - get rid of it.
                iter.remove();
            }
            else {
                if (binder.matchesListenBean(event)) {
                    binder.updateProperty();
                }
            }
        }
    }
}
