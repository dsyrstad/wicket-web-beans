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

package net.sourceforge.wicketwebbeans.examples;

import java.io.Serializable;
import java.util.Date;

import net.sourceforge.wicketwebbeans.model.PropertyChanger;

import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * A test bean that is Serializable. <p>
 * 
 * @author Dan Syrstad
 */
public class SerializableBean extends NonSerializableBean implements Serializable
{
    private static final long serialVersionUID = -4561485257546568056L;
    private String saveMsg;

    /**
     * Construct a SerializableBean. 
     *
     * @param name
     * @param serialNumber
     */
    public SerializableBean(String name, String serialNumber)
    {
        super(name, serialNumber);
    }

    public Date getStamp()
    {
        return new Date();
    }

    public void save(String msg, int value)
    {
        //Passing parameters like URL...
        //RequestCycle.get().redirectTo(new ExperimentalPage());
        final AjaxRequestTarget ajaxRequestTarget = (AjaxRequestTarget)RequestCycle.get().getRequestTarget();
        Page page = ajaxRequestTarget.getPage();
        // TODO How to update a specific feedback panel? Attach property to feedbackpanel model?
        page.info("Saved " + msg + " value=" + value);
        ajaxRequestTarget.addChildren(page, FeedbackPanel.class);
        setSaveMsg("Saved it on " + new Date());
    }

    public void setSaveMsg(String saveMsg)
    {
        this.saveMsg = saveMsg;
        PropertyChanger.dispatch(this, "saveMsg");
    }

    public String getSaveMsg()
    {
        return saveMsg;
    }
}
