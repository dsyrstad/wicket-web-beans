/*---
   Copyright 2006-2007 Visual Systems Corporation.
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

package wicket.contrib.webbeans.model;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import wicket.util.string.Strings;

/**
 * Common metadata methods. <p>
 * 
 * @author Dan Syrstad
 */
public class MetaData
{
    private Properties parameters = new Properties();
    private Set<String> consumedParameters = new HashSet<String>();

    /**
     * Construct a MetaData. 
     */
    public MetaData()
    {
    }

    /**
     * Consumes a parameter.
     *
     * @param param the parameter to consume.
     */
    public void consumeParameter(String param)
    {
        consumedParameters.add(param);
    }

    /**
     * @param unconsumedMsgs messages that report the parameter keys that were specified but not consumed.
     * @param context a context for the unconsumed message.
     * @return true if all parameters specified have been consumed.
     */
    public boolean areAllParametersConsumed(String context, Set<String> unconsumedMsgs)
    {
        boolean result = true;
        for (Object parameter : parameters.keySet()) {
            if (!consumedParameters.contains(parameter)) {
                unconsumedMsgs.add(context+ ": Parameter " + parameter + " was not consumed");
                result = false;
            }
        }
        
        return result;
    }

    /**
     * Gets the specified parameter.
     *
     * @param key the parameter key.
     * 
     * @return the parameter value, or null if not set.
     */
    public String getParameter(String key)
    {
        consumeParameter(key);
        return parameters.getProperty(key);
    }
    
    /**
     * Gets the specified parameter.
     *
     * @param key the parameter key.
     * 
     * @return the parameter value, or defaultValue if not set.
     */
    public String getParameter(String key, String defaultValue)
    {
        String value = getParameter(key);
        return value == null ? defaultValue : value;
    }
    
    /**
     * Gets a boolean parameter.
     *
     * @param key the parameter key.
     * 
     * @return true or false. False is returned if the parameter is not set.
     */
    public boolean getBooleanParameter(String key)
    {
        return Boolean.valueOf( getParameter(key, "false") );
    }
    
    /**
     * Gets an Integer parameter.
     *
     * @param key the parameter key.
     * 
     * @return the value, or null if not set.
     */
    public Integer getIntegerParameter(String key)
    {
        String value = getParameter(key);
        return value == null ? null : Integer.valueOf(value);
    }
    
    /**
     * Gets an int parameter.
     *
     * @param key the parameter key.
     * 
     * @return the value, or defaultValue if not set.
     */
    public int getIntParameter(String key, int defaultValue)
    {
        Integer value = getIntegerParameter(key);
        return value == null ? defaultValue : value;
    }
    
    /**
     * Sets a parameter.
     *
     * @param key the parameter key.
     * @param value the parameter value.
     */
    public void setParameter(String key, String value)
    {
        parameters.setProperty(key, value);
    }
    
    /**
     * Sets a parameter if the value is not empty or null.
     *
     * @param key the parameter key. If empty or null, the parameter is not set.
     * @param value the parameter value. If empty or null, the parameter is not set.
     */
    public void setParameterIfNotEmpty(String key, String value)
    {
        if (!Strings.isEmpty(key) && !Strings.isEmpty(value)) {
            setParameter(key,value);
        }
    }
    
    /**
     * Gets the parameters.
     *
     * @return the parameters.
     */
    public Properties getParameters()
    {
        return parameters;
    }
    
    /**
     * Sets the parameters.
     *
     * @param parameters the parameters to set.
     */
    public void setParameters(Properties parameters)
    {
        this.parameters = parameters;
    }

}
