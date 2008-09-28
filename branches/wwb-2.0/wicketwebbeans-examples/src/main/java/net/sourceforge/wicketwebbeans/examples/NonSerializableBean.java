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

import java.math.BigDecimal;

import net.sourceforge.wicketwebbeans.model.PropertyChanger;

/**
 * A test bean that is not Serializable. <p>
 * 
 * @author Dan Syrstad
 */
public class NonSerializableBean
{
    private String name;
    private String serialNumber;
    private BigDecimal operand1;
    private BigDecimal operand2;
    private BigDecimal result;

    /**
     * Construct a NonSerializableBean. 
     *
     * @param name
     * @param serialNumber
     */
    public NonSerializableBean(String name, String serialNumber)
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

}
