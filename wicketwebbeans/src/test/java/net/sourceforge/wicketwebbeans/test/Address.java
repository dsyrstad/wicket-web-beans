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

package net.sourceforge.wicketwebbeans.test;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Address implements Serializable
{
    private String[] addressLines;
    private String city;
    private String state;
    private String postalCode;
    private Address relatedAddress;

    public Address()
    {
    }

    public Address(String city, String postalCode, String state, String... addressLines)
    {
        this.city = city;
        this.postalCode = postalCode;
        this.state = state;
        this.addressLines = addressLines;
    }

    public String[] getAddressLines()
    {
        return addressLines;
    }

    public void setAddressLines(String[] addressLine)
    {
        this.addressLines = addressLine;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }

    public void setRelatedAddress(Address relatedAddress)
    {
        this.relatedAddress = relatedAddress;
    }

    public Address getRelatedAddress()
    {
        return relatedAddress;
    }
}
