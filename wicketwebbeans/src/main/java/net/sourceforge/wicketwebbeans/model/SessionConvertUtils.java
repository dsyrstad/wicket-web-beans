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

import java.io.Serializable;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;

/**
 * Gets a {@link ConvertUtilsBean} that is associated with the current RequestCycle Session. If no
 * request cycle is active, a default {@link ConvertUtilsBean} is returned. <p>
 * 
 * @author Dan Syrstad
 */
public class SessionConvertUtils extends ConvertUtilsBean implements Serializable
{
    private static final long serialVersionUID = 9016863280786431161L;

    private static final MetaDataKey SESSION_KEY = new MetaDataKey(ConvertUtilsBean.class) {
        private static final long serialVersionUID = -7109763335881231272L;
    };

    public SessionConvertUtils()
    {
        register(new IModelConverter(), IModel.class);
        register(false, true, -1);
    }

    public static SessionConvertUtils getCurrent()
    {
        SessionConvertUtils convertUtilsBean = null;
        RequestCycle requestCycle = RequestCycle.get();
        if (requestCycle != null) {
            Session session = requestCycle.getSession();
            convertUtilsBean = (SessionConvertUtils)session.getMetaData(SESSION_KEY);
        }

        if (convertUtilsBean == null) {
            convertUtilsBean = new SessionConvertUtils();
            setCurrent(convertUtilsBean);
        }

        return convertUtilsBean;
    }

    public static void setCurrent(SessionConvertUtils convertUtilsBean)
    {
        RequestCycle requestCycle = RequestCycle.get();
        if (requestCycle != null) {
            Session session = requestCycle.getSession();
            session.setMetaData(SESSION_KEY, convertUtilsBean);
            session.dirty();
        }
    }

}
