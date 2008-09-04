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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Configuration for a Bean. <p/>
 * 
 * @author Dan Syrstad
 */
public class BeanConfig implements Serializable {
	private static final long serialVersionUID = -4705317346444856939L;

	// Key is parameter name.
	private Map<String, List<ParameterValueAST>> parameters = new LinkedHashMap<String, List<ParameterValueAST>>();
	private String beanName;

	/**
	 * Construct a BeanConfig.
	 * 
	 * @param beanConfig
	 *            the configuration for the bean.
	 */
	public BeanConfig(BeanConfigAST beanConfig) {
		this.beanName = beanConfig.getName();
		for (ParameterAST paramAST : beanConfig.getParameters()) {
			setParameter(paramAST.getName(), paramAST.getValues());
		}
	}

	/**
	 * Gets the specified parameter's value. If the parameter has multiple
	 * values, the first value is returned.
	 * 
	 * @param parameterName
	 *            the parameter name.
	 * 
	 * @return the parameter value, or null if not set.
	 */
	public ParameterValueAST getParameterValue(String parameterName) {
		List<ParameterValueAST> values = getParameterValues(parameterName);
		if (values == null || values.isEmpty()) {
			return null;
		}

		return values.get(0);
	}

	/**
	 * Gets the specified parameter's value as a String. If the parameter has
	 * multiple values, the first value is returned.
	 * 
	 * @param parameterName
	 *            the parameter name.
	 * 
	 * @return the parameter value, or null if not set.
	 */
	public String getParameterValueAsString(String parameterName) {
		ParameterValueAST value = getParameterValue(parameterName);
		if (value == null) {
			return null;
		}

		return value.getValue();
	}

	// TODO Test
	public int getIntParameterValue(String parameterName, int defaultValue) {
		ParameterValueAST parameterValue = getParameterValue(parameterName);
		Integer value = null;
		if (parameterValue != null) {
			value = parameterValue.getIntegerValue();
		}

		return value == null ? defaultValue : value;
	}

	/**
	 * Gets the specified parameter's value(s).
	 * 
	 * @param parameterName
	 *            the parameter name.
	 * 
	 * @return the parameter's values, or null if not set.
	 */
	public List<ParameterValueAST> getParameterValues(String parameterName) {
		return parameters.get(parameterName);
	}

	public void setParameter(String parameterName,
			List<ParameterValueAST> values) {
		parameters.put(parameterName, values);
	}
}
