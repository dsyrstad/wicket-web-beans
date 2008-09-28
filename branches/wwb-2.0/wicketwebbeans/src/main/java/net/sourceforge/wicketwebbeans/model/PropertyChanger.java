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

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;

/**
 * Static methods that delegate to a {@link PropertyChangeDispatcher} for the current Wicket Session, if any.
 * The dispatcher is created if one does not exist. 
 * A transient {@link PropertyChangeDispatcher} is returned if a session/request cycle does not exist.
 * The dispatcher can be set via {@link #setCurrent(PropertyChangeDispatcher)}. <p>
 * 
 * @author Dan Syrstad
 */
public class PropertyChanger
{
    private static final MetaDataKey DISPATCHER_KEY = new MetaDataKey(PropertyChangeDispatcher.class) {
        private static final long serialVersionUID = 5330887235643461508L;
    };

    private PropertyChanger()
    {
    }

    public static PropertyChangeDispatcher getCurrent()
    {
        PropertyChangeDispatcher dispatcher = null;
        RequestCycle requestCycle = RequestCycle.get();
        if (requestCycle != null) {
            Session session = requestCycle.getSession();
            dispatcher = (PropertyChangeDispatcher)session.getMetaData(DISPATCHER_KEY);
        }

        if (dispatcher == null) {
            dispatcher = new PropertyChangeDispatcher();
            setCurrent(dispatcher);
        }

        return dispatcher;
    }

    public static void setCurrent(PropertyChangeDispatcher dispatcher)
    {
        RequestCycle requestCycle = RequestCycle.get();
        if (requestCycle != null) {
            Session session = requestCycle.getSession();
            session.setMetaData(DISPATCHER_KEY, dispatcher);
            session.dirty();
        }
    }

    public static void dispatch(Object source, String propertyName)
    {
        getCurrent().dispatch(source, propertyName);
    }

}
