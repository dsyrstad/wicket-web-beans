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
import java.math.BigDecimal;
import java.util.Date;

import net.sourceforge.wicketwebbeans.model.PropertyChanger;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * A test bean. <p>
 * 
 * @author Dan Syrstad
 */
public class BeanWithEvents implements Serializable
{
    private static final long serialVersionUID = -4561485257546568056L;

    private String name;
    private String serialNumber;
    private BigDecimal operand1;
    private BigDecimal operand2;
    private BigDecimal result;
    private String saveMsg;

    /**
     * Construct a SerializableBean. 
     *
     * @param name
     * @param serialNumber
     */
    public BeanWithEvents(String name, String serialNumber)
    {
        setName(name);
        setSerialNumber(serialNumber);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber == null ? null : serialNumber.toUpperCase();
        PropertyChanger.dispatch(this, "serialNumber");
    }

    public BigDecimal getOperand1()
    {
        return operand1;
    }

    public void setOperand1(BigDecimal operand1)
    {
        this.operand1 = operand1;
        recalc();
    }

    public BigDecimal getOperand2()
    {
        return operand2;
    }

    public void setOperand2(BigDecimal operand2)
    {
        this.operand2 = operand2;
        recalc();
    }

    private void recalc()
    {
        if (operand1 != null && operand2 != null) {
            result = operand1.add(operand2);
            PropertyChanger.dispatch(this, "result");
        }
    }

    public BigDecimal getResult()
    {
        return result;
    }

    public void setResult(BigDecimal result)
    {
        this.result = result;
    }

    public Date getStamp()
    {
        return new Date();
    }

    public void save(String msg, int value)
    {
        //RequestCycle.get().redirectTo(new ExperimentalPage());
        final AjaxRequestTarget ajaxRequestTarget = AjaxRequestTarget.get();
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
