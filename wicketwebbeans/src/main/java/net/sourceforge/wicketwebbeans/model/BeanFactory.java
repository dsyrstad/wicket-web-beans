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
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;

/**
 * Loads bean config files and creates new instances of named beans.
 * <p>
 * 
 * @author Dan Syrstad
 */
public class BeanFactory
{
    private static final String PARAMETER_NAME_EXTENDS = "extends";
    private static final String PARAMETER_NAME_CLASS = "class";

    /** Cache of pre-parsed bean configs. */
    private final Map<URL, CachedBeanConfigs> cachedBeanConfigs = new HashMap<URL, CachedBeanConfigs>();

    /** Maps bean name to BeanConfig. */
    private Map<String, BeanConfig> beanConfigMap = new HashMap<String, BeanConfig>();
    private String[] packageImports = {
        "",
        "java.lang.",
        "java.util.",
        "net.sourceforge.wicketwebbeans.",
        "net.sourceforge.wicketwebbeans.model.",
        "net.sourceforge.wicketwebbeans.fields.",
        "net.sourceforge.wicketwebbeans.containers.",
        "net.sourceforge.wicketwebbeans.actions.",
    // 
    };

    public BeanFactory()
    {
    }

    public String[] getPackageImports()
    {
        return packageImports;
    }

    /**
     * Sets the default package imports that make it easier to specify class names in config files.
     *
     * @param packageImports an array of package names. If a package name is not blank, it should end in "." - e.g., "java.util.".
     */
    public void setPackageImports(String[] packageImports)
    {
        this.packageImports = packageImports;
    }

    /**
     * Load a bean config file from a URL.
     * 
     * @return this instance for method chaining convenience.
     */
    public BeanFactory loadBeanConfig(URL url)
    {
        assert url != null;

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

        for (BeanConfigAST beanConfigAst : cachedConfig.getAsts()) {
            processBean(url, beanConfigAst);
        }

        return this;
    }

    private void processBean(URL url, BeanConfigAST beanConfigAst)
    {
        BeanConfig beanConfig = new BeanConfig(this, beanConfigAst);
        String beanName = beanConfigAst.getName();
        ParameterValueAST classValue = beanConfig.getParameterValue(PARAMETER_NAME_CLASS);
        ParameterValueAST extendsValue = beanConfig.getParameterValue(PARAMETER_NAME_EXTENDS);
        if (classValue == null && extendsValue == null) {
            throw new RuntimeException("Bean " + beanName + " in URL " + url + " must specify class or extends");
        }

        if (classValue != null && extendsValue != null) {
            throw new RuntimeException("Bean " + beanName + " in URL " + url + " cannot specify both class and extends");
        }

        beanConfigMap.put(beanName, beanConfig);
    }

    /**
     * Creates a new instance of the specified bean.
     * 
     * @param beanName
     *            the bean's name as defined in the bean configuration.
     * @param args
     *            optional arguments for the constructor.
     * 
     * @return the newly created bean.
     */
    public Object newInstance(String beanName, Object... args)
    {
        BeanConfig beanConfig = getBeanConfig(beanName);
        if (beanConfig == null) {
            throw new RuntimeException("Bean " + beanName + " not defined in factory");
        }

        Class<?> beanClass = loadClass(beanConfig);

        String beanClassName = beanClass.getName();

        Object bean;
        try {
            // TODO This does not handle null arguments. Need to find matching
            // constructor like EnerJ
            bean = ConstructorUtils.invokeConstructor(beanClass, args);
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot create instance of bean '" + beanName + "' class: " + beanClassName, e);
        }

        setBeanProperties(beanConfig, bean, beanClassName);

        return bean;
    }

    /**
     * Loads the class for the given BeanConfig. Throws a RuntimeException if the class cannot be found.
     */
    // TODO Test
    public Class<?> loadClass(BeanConfig beanConfig)
    {
        String beanClassName = beanConfig.getParameterValueAsString(PARAMETER_NAME_CLASS);
        Class<?> beanClass = null;
        Exception firstException = null;
        for (String packageImport : packageImports) {
            try {
                beanClass = Class.forName(packageImport + beanClassName);
                break;
            }
            catch (Exception e) {
                if (firstException == null) {
                    firstException = e;
                }
            }
        }

        if (beanClass == null) {
            throw new RuntimeException("Cannot find class " + beanClassName, firstException);
        }

        return beanClass;
    }

    private void setBeanProperties(BeanConfig beanConfig, Object bean, String beanClassName)
    {
        String beanName = beanConfig.getBeanName();

        for (Map.Entry<String, List<ParameterValueAST>> parameter : beanConfig.getParameters().entrySet()) {
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

            List<ParameterValueAST> values = parameter.getValue();
            // TODO need a convertTo on ParameterValueAST and test that.
            Object value;
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
                // TODO Test
                else if (List.class.isAssignableFrom(propertyType)) {
                    value = values;
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
    }

    /**
     * Gets the BeanConfig for the specified bean name.
     * 
     * @param beanName
     *            the bean's name as defined in the bean configuration.
     * 
     * @return the BeanConfig for beanName, or null if beanName is not defined.
     */
    public BeanConfig getBeanConfig(String beanName)
    {
        BeanConfig beanConfig = beanConfigMap.get(beanName);
        return beanConfig;
    }


    /**
     * A Cached Bean config file.
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
