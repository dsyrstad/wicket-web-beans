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

package net.sourceforge.wicketwebbeans.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Common metadata methods. <p>
 * 
 * @author Dan Syrstad
 */
abstract public class MetaData implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    // Key is parameter name. 
    private Map<String, List<ParameterValueAST>> parameters = new HashMap<String, List<ParameterValueAST>>();
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
     * @param parameterName the parameter name to consume.
     */
    public void consumeParameter(String parameterName)
    {
        consumedParameters.add(parameterName);
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
     * Gets the specified parameter's value. If the parameter has multiple values, the first value is returned.
     *
     * @param parameterName the parameter name.
     * 
     * @return the parameter value, or null if not set.
     */
    public ParameterValueAST getParameterValue(String parameterName)
    {
        List<ParameterValueAST> values = getParameterValues(parameterName);
        if (values == null || values.isEmpty()) {
            return null;
        }
        
        return values.get(0);
    }
    
    /**
     * Gets the specified parameter's value(s).
     *
     * @param parameterName the parameter name.
     * 
     * @return the parameter's values, or null if not set.
     */
    public List<ParameterValueAST> getParameterValues(String parameterName)
    {
        consumeParameter(parameterName);
        return parameters.get(parameterName);
    }

    public void setParameter(String parameterName, List<ParameterValueAST> values)
    {
        parameters.put(parameterName, values);
    }
}
