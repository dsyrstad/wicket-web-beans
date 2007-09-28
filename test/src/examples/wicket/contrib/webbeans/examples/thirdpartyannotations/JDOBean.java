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

package wicket.contrib.webbeans.examples.thirdpartyannotations;

import java.io.Serializable;

import javax.jdo.annotations.Column;



/**
 * A test bean that uses JDO 2.0 annotations - really just @Column. <p>
 * 
 * @author Dan Syrstad
 */
public class JDOBean implements Serializable
{
    private String name;
    private String serialNumber;
    
    /**
     * Construct a JPABean. 
     *
     * @param name
     * @param serialNumber
     */
    public JDOBean(String name, String serialNumber)
    {
        this.name = name;
        this.serialNumber = serialNumber;
    }

    @Column(length=20, allowsNull="false")
    public String getJdoName()
    {
        return name;
    }

    public void setJdoName(String name)
    {
        this.name = name;
    }

    public String getJdoSerialNumber()
    {
        return serialNumber;
    }

    // Test annotation on setter.
    @Column(length=10)
    public void setJdoSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }
}
