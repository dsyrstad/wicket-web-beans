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

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;

/**
 * Represents the Configuration for a Bean. 
 * <p/>
 *  
 * @author Dan Syrstad
 */
public class BeanConfig implements Serializable
{
    private static final String PARAMETER_NAME_EXTENDS = "extends";

    private static final String PARAMETER_NAME_CLASS = "class";

    private static final long serialVersionUID = -4705317346444856939L;

    private static Logger logger = Logger.getLogger(BeanConfig.class.getName());
    /** Cache of pre-parsed bean configs. */
    private static final Map<URL, CachedBeanConfigs> cachedBeanConfigs = new HashMap<URL, CachedBeanConfigs>();
    // Key is parameter name. 
    private Map<String, List<ParameterValueAST>> parameters = new LinkedHashMap<String, List<ParameterValueAST>>();

    private String beanName;
    private URL url;
    private ComponentRegistry componentRegistry;

    /**
     * Construct a BeanConfig. If beanName is null, metadata is derived from a bean
     * named "ROOT". 
     *
     * @param beanName the beanName to be used for metadata. May be null.
     * @param url the URL of the WWB configuration.
     * @param componentRegistry the ComponentRegistry used to determine visual components. May be null to use the default.
     */
    public BeanConfig(String beanName, URL url, ComponentRegistry componentRegistry)
    {
        assert url != null;

        if (beanName == null) {
            this.beanName = "ROOT";
        }
        else {
            this.beanName = beanName;
        }

        this.url = url;

        if (componentRegistry == null) {
            this.componentRegistry = new ComponentRegistry();
        }
        else {
            this.componentRegistry = componentRegistry;
        }

        collectFromBeanConfig();
    }

    /**
     * Collection Beans from the beanprops file, if any.
     */
    private void collectFromBeanConfig()
    {
        long timestamp = 0;
        if (url.getProtocol().equals("file")) {
            try {
                timestamp = new File(url.toURI()).lastModified();
            }
            catch (URISyntaxException e) {
                timestamp = -1;
            }
        }

        CachedBeanConfigs cachedConfig = cachedBeanConfigs.get(url);
        if (cachedConfig == null || cachedConfig.getModTimestamp() != timestamp) {
            if (cachedConfig != null) {
                logger.info("Re-reading changed file: " + url);
            }

            InputStream inStream = null;
            try {
                inStream = url.openStream();
                List<BeanConfigAST> asts = new BeanConfigParser(url.toString(), inStream).parse();
                cachedConfig = new CachedBeanConfigs(asts, timestamp);
                cachedBeanConfigs.put(url, cachedConfig);
            }
            catch (IOException e) {
                throw new RuntimeException("Error reading stream for URL: " + url, e);
            }
            finally {
                IOUtils.closeQuietly(inStream);
            }
        }

        boolean foundBean = false;
        for (BeanConfigAST beanConfigAst : cachedConfig.getAsts()) {
            if (beanName.equals(beanConfigAst.getName())) {
                foundBean = true;
                for (ParameterAST paramAST : beanConfigAst.getParameters()) {
                    setParameter(paramAST.getName(), paramAST.getValues());
                }
            }
        }

        if (!foundBean) {
            throw new RuntimeException("Could not find bean '" + beanName + " in URL " + url);
        }

        ParameterValueAST classValue = getParameterValue(PARAMETER_NAME_CLASS);
        ParameterValueAST extendsValue = getParameterValue(PARAMETER_NAME_EXTENDS);
        if (classValue == null && extendsValue == null) {
            throw new RuntimeException("Bean " + beanName + " in URL " + url + " must specify class or extends");
        }

        if (classValue != null && extendsValue != null) {
            throw new RuntimeException("Bean " + beanName + " in URL " + url + " cannot specify both class and extends");
        }
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
     * Gets the specified parameter's value as a String. If the parameter has multiple values, the first value is returned.
     *
     * @param parameterName the parameter name.
     * 
     * @return the parameter value, or null if not set.
     */
    public String getParameterValueAsString(String parameterName)
    {
        ParameterValueAST value = getParameterValue(parameterName);
        if (value == null) {
            return null;
        }

        return value.getValue();
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
        return parameters.get(parameterName);
    }

    public void setParameter(String parameterName, List<ParameterValueAST> values)
    {
        parameters.put(parameterName, values);
    }

    public ComponentRegistry getComponentRegistry()
    {
        return componentRegistry;
    }

    public Object newInstance(Object... args)
    {
        String beanClassName = getParameterValueAsString(PARAMETER_NAME_CLASS);
        Object bean;
        try {
            Class<?> beanClass = Class.forName(beanClassName);
            // TODO This does not handle null arguments. Need to find matching constructor like EnerJ
            bean = ConstructorUtils.invokeConstructor(beanClass, args);
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot create instance of bean '" + beanName + "' class: " + beanClassName, e);
        }

        for (Map.Entry<String, List<ParameterValueAST>> parameter : parameters.entrySet()) {
            String parameterName = parameter.getKey();
            if (parameterName.equals(PARAMETER_NAME_CLASS) || parameterName.equals(PARAMETER_NAME_EXTENDS)) {
                continue;
            }

            PropertyDescriptor propertyDescriptor;
            try {
                propertyDescriptor = PropertyUtils.getPropertyDescriptor(bean, parameterName);
            }
            catch (Exception e) {
                throw new RuntimeException("Cannot find property " + parameterName + " for bean '" + beanName
                                + "' class: " + beanClassName, e);
            }

            if (propertyDescriptor == null) {
                throw new RuntimeException("Cannot find property " + parameterName + " for bean '" + beanName
                                + "' class: " + beanClassName);
            }

            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod == null) {
                throw new RuntimeException("Property " + parameterName + " for bean '" + beanName + "' class "
                                + beanClassName + " does not have an exposed setter");
            }

            Object value;
            List<ParameterValueAST> values = parameter.getValue();
            // TODO need a convertTo on ParameterValueAST and test that. 
            if (values.isEmpty()) {
                value = null;
            }
            else {
                ParameterValueAST valueAst = values.get(0);
                Class<?> propertyType = propertyDescriptor.getPropertyType();
                propertyType = ClassUtils.primitiveToWrapper(propertyType);
                if (Double.class.isAssignableFrom(propertyType)) {
                    value = valueAst.getDoubleValue();
                }
                else if (Float.class.isAssignableFrom(propertyType)) {
                    value = valueAst.getDoubleValue().floatValue();
                }
                else if (Long.class.isAssignableFrom(propertyType)) {
                    value = valueAst.getLongValue();
                }
                else if (Integer.class.isAssignableFrom(propertyType)) {
                    value = valueAst.getIntegerValue();
                }
                else if (Short.class.isAssignableFrom(propertyType)) {
                    value = valueAst.getIntegerValue().shortValue();
                }
                else if (Boolean.class.isAssignableFrom(propertyType)) {
                    value = Boolean.valueOf(valueAst.getBooleanValue());
                }
                else if (propertyType.equals(String.class)) {
                    value = valueAst.getValue();
                }
                else {
                    throw new RuntimeException("Property type " + propertyType + " on property " + parameterName
                                    + " for bean '" + beanName + "' class " + beanClassName + " is not supported");
                }
            }

            try {
                writeMethod.invoke(bean, value);
            }
            catch (Exception e) {
                Throwable t = e;
                if (e instanceof InvocationTargetException) {
                    t = e.getCause();
                }

                throw new RuntimeException("Error setting property " + parameterName + " for bean '" + beanName
                                + "' class " + beanClassName, t);
            }
        }

        return bean;
    }


    /**
     * A Cached Beanprops file.
     */
    @SuppressWarnings("serial")
    private static final class CachedBeanConfigs implements Serializable
    {
        private List<BeanConfigAST> asts;
        private long modTimestamp;

        CachedBeanConfigs(List<BeanConfigAST> asts, long modTimestamp)
        {
            this.asts = asts;
            this.modTimestamp = modTimestamp;
        }

        List<BeanConfigAST> getAsts()
        {
            return asts;
        }

        long getModTimestamp()
        {
            return modTimestamp;
        }
    }
}
