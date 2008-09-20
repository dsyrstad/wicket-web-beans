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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.wicketwebbeans.model.jxpath.JXPathPropertyResolver;
import net.sourceforge.wicketwebbeans.util.WwbClassUtils;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Loads bean config files and creates new instances of named beans.
 * <p>
 * 
 * @author Dan Syrstad
 */
public class BeanFactory
{
    private static final String SPECIAL_PARAM_TYPE = "_type";
    private static final String PARAMETER_NAME_EXTENDS = "extends";
    private static final String PARAMETER_NAME_CLASS = "class";

    private PropertyResolver propertyResolver = new JXPathPropertyResolver();
    private ComponentRegistry componentRegistry = new ComponentRegistry();

    /** Cache of pre-parsed bean configs. TODO This cache should be static and be a LRU Cache.*/
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

    private IModel beanModel;
    private ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
    {
        convertUtilsBean.register(new IModelConverter(), IModel.class);
        convertUtilsBean.register(false, true, -1);
    }

    /**
     * Construct a BeanFactory with no bean model. 
     */
    public BeanFactory()
    {
        this(new Model());
    }

    /**
     * Construct a BeanFactory with a Serializable bean. 
     *
     * @param bean the bean.
     */
    public BeanFactory(Serializable bean)
    {
        this(new Model(bean));
    }

    /**
     * Construct a BeanFactory with a IModel representing the bean. 
     *
     * @param beanModel the bean's model.
     */
    public BeanFactory(IModel beanModel)
    {
        this.beanModel = beanModel;
    }

    public PropertyResolver getPropertyResolver()
    {
        return propertyResolver;
    }

    public void setPropertyResolver(PropertyResolver propertyResolver)
    {
        this.propertyResolver = propertyResolver;
    }

    public ComponentRegistry getComponentRegistry()
    {
        return componentRegistry;
    }

    public void setComponentRegistry(ComponentRegistry componentRegistry)
    {
        this.componentRegistry = componentRegistry;
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

        return newInstance(beanConfig, args);
    }

    /**
     * Creates a new instance of the specified bean.
     * 
     * @param beanConfig
     *            the bean's config.
     * @param args
     *            optional arguments for the constructor.
     * 
     * @return the newly created bean.
     */
    public Object newInstance(BeanConfig beanConfig, Object... args)
    {
        Class<?> beanClass = loadClass(beanConfig);
        String beanClassName = beanClass.getName();

        Object bean;
        try {
            bean = WwbClassUtils.invokeMostSpecificConstructor(beanClass, args);
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot create instance of bean '" + beanConfig.getBeanName() + "' class: "
                            + beanClassName, e);
        }

        setBeanFactory(bean);
        setBeanProperties(beanConfig, bean, beanClass);
        return bean;
    }

    /**
     * Loads the class for the given BeanConfig. Throws a RuntimeException if the class cannot be found.
     */
    public Class<?> loadClass(BeanConfig beanConfig)
    {
        String beanClassName = beanConfig.getParameterValueAsString(PARAMETER_NAME_CLASS);
        return loadClass(beanClassName);
    }

    /**
     * Loads the class for the given name, using imports to resolve the name. Throws a RuntimeException if the class cannot be found.
     */
    private Class<?> loadClass(String beanClassName)
    {
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

    private void setBeanProperties(BeanConfig beanConfig, Object bean, Class<?> beanClass)
    {
        String beanName = beanConfig.getBeanName();
        Map<String, List<ParameterValueAST>> parameters = beanConfig.getParameters();
        for (Map.Entry<String, List<ParameterValueAST>> parameter : parameters.entrySet()) {
            String parameterName = parameter.getKey();
            setBeanProperty(beanName, bean, parameterName, parameter.getValue());
        }
    }

    private void setBeanProperty(String beanName, Object bean, String parameterName, List<ParameterValueAST> values)
    {
        Class<?> beanClass = bean.getClass();
        String beanClassName = beanClass.getName();
        if (parameterName.equals(PARAMETER_NAME_CLASS) || parameterName.equals(PARAMETER_NAME_EXTENDS)
                        || parameterName.charAt(0) == '_') {
            return;
        }

        PropertyDescriptor propertyDescriptor;
        try {
            propertyDescriptor = PropertyUtils.getPropertyDescriptor(bean, parameterName);
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot find property " + parameterName + " for bean '" + beanName + "' class: "
                            + beanClassName, e);
        }

        if (propertyDescriptor == null) {
            throw new RuntimeException("Cannot find property " + parameterName + " for bean '" + beanName + "' class: "
                            + beanClassName);
        }

        Class<?> propertyType = propertyDescriptor.getPropertyType();
        Method writeMethod = propertyDescriptor.getWriteMethod();
        if (writeMethod == null) {
            // Try to find a setter with a return value. This is common in Wicket for builder patterns.
            String setterName = "set"
                            + (parameterName.length() > 1 ? Character.toUpperCase(parameterName.charAt(0))
                                            + parameterName.substring(1) : parameterName.toUpperCase());
            try {
                writeMethod = ClassUtils.getPublicMethod(beanClass, setterName, new Class[] { propertyType });
            }
            catch (NoSuchMethodException e) {
                // Handled below.
            }

            if (writeMethod == null) {
                throw new RuntimeException("Property " + parameterName + " for bean '" + beanName + "' class "
                                + beanClassName + " does not have an exposed setter");
            }
        }

        propertyType = ClassUtils.primitiveToWrapper(propertyType);
        Object value;
        if (values.isEmpty()) {
            value = null;
        }
        else if (List.class.isAssignableFrom(propertyType)) {
            value = values;
        }
        else {
            ParameterValueAST valueAst = values.get(0);
            String stringValue = valueAst.getValue();
            boolean hasPropertyValue = stringValue != null && stringValue.charAt(0) == '$';
            if (hasPropertyValue) {
                PropertyProxyModel propertyProxyModel = resolvePropertyProxyModel(stringValue);
                if (IModel.class.isAssignableFrom(propertyType)) {
                    value = propertyProxyModel;
                }
                else {
                    value = propertyProxyModel.getObject();
                }
            }
            else if (valueAst.isLiteral() || stringValue == null) {
                value = stringValue;
            }
            else {
                // Component name
                BeanConfig subBean = getBeanConfig(stringValue, valueAst.getSubParameters());
                if (subBean == null) {
                    throw new RuntimeException("Cannot find bean named: " + stringValue);
                }

                value = newInstance(subBean);
            }
        }

        value = convertUtilsBean.convert(value, propertyType);
        try {
            writeMethod.invoke(bean, value);
        }
        catch (Exception e) {
            Throwable t = e;
            if (e instanceof InvocationTargetException) {
                t = e.getCause();
            }

            throw new RuntimeException("Error setting property '" + parameterName + "' for bean '" + beanName
                            + "' class " + beanClassName, t);
        }
    }

    /**
     * Resolves a property specification string to a PropertyProxyModel
     *
     * @param propertySpec the specification string with leading '$'.
     * @return a PropertyProxyModel.
     */
    public PropertyProxyModel resolvePropertyProxyModel(String propertySpec)
    {
        PropertyProxy propertyProxy = propertyResolver.createPropertyProxy(propertySpec.substring(1));
        return new PropertyProxyModel(propertyProxy, beanModel);
    }

    /**
     * Resolves a Wicket Component from a ParameterValueAST. The value of the parameter is either
     * a Bean name or a property specification starting with '$'. A Bean parameterValue can have sub-parameters
     * that are used to configure the component. <p/>
     * 
     * Property spec parameterValues can also have sub-parameters, one
     * of which can be "_type: classname" which indicates the class of the property. It can also have "_component: classname"
     * which indicates the component class to use, rather than using ComponentRegistry. The component's model becomes the
     * a PropertyProxyModel representing the property.
     *
     * @param wicketId the Wicket id for the component.
     * @param parameterValue
     * 
     * @return a Component.
     */
    public Component resolveComponent(String wicketId, ParameterValueAST parameterValue)
    {
        // TODO Test
        String valueString = parameterValue.getValue();
        if (valueString.charAt(0) == '$') {
            PropertyProxyModel propertyProxyModel = resolvePropertyProxyModel(valueString);
            Class<?> propertyType = null;
            // If we have _type, we don't have to try to get it from the model.
            ParameterAST propertyTypeParam = parameterValue.getSubParameter(SPECIAL_PARAM_TYPE);
            if (propertyTypeParam != null) {
                // TODO Test imports
                propertyType = (Class<?>)loadClass(propertyTypeParam.getValuesAsStrings()[0]);
            }

            if (propertyType == null) {
                // Get property type from model.
                Object propertyValue = propertyProxyModel.getObject();
                if (propertyValue == null) {
                    throw new RuntimeException("Cannot determine property type because the expression '" + valueString
                                    + "' evaluates to null. Specify " + SPECIAL_PARAM_TYPE
                                    + " as a sub-parameter to the property or use a Bean declaration.");
                }

                propertyType = propertyValue.getClass();
            }

            // Get component class from ComponentRegistry
            // TODO Handle elementType as special param and pass to component registry.
            Class<? extends Component> componentClass = getComponentRegistry().getComponentClass(propertyType, null);
            if (componentClass == null) {
                throw new RuntimeException("Cannot find component in the ComponentRegistry for the expression '"
                                + valueString + "' and type of " + propertyType + ". Specify " + SPECIAL_PARAM_TYPE
                                + " as a sub-parameter to the property or use a Bean declaration.");
            }

            // Create component
            Component component;
            try {
                component = (Component)WwbClassUtils.invokeMostSpecificConstructor(componentClass, wicketId);
            }
            catch (Exception e) {
                throw new RuntimeException(
                                "Cannot create component of "
                                                + componentClass
                                                + ". Component must have a public single-argument constructor whose parameter is a Wicket id");
            }

            component.setModel(propertyProxyModel);

            for (ParameterAST parameter : parameterValue.getSubParameters()) {
                setBeanProperty(component.getClass().getName(), component, parameter.getName(), parameter.getValues());
            }

            return component;
        }
        else {
            BeanConfig beanConfig = getBeanConfig(valueString, parameterValue.getSubParameters());
            return (Component)newInstance(beanConfig, wicketId);
        }
    }

    /** 
     * If bean has a public setBeanFactory(BeanFactory) method, call it with this BeanFactory.
     */
    private void setBeanFactory(Object bean)
    {
        Method setBeanFactoryMethod;
        try {
            setBeanFactoryMethod = bean.getClass().getMethod("setBeanFactory", BeanFactory.class);
        }
        catch (Exception e) {
            // No method...
            return;
        }

        try {
            setBeanFactoryMethod.invoke(bean, this);
        }
        catch (Exception e) {
            throw new RuntimeException("Exception calling setBeanFactory", e);
        }
    }

    /**
     * Gets the BeanConfig for the specified bean name.
     * 
     * @param beanName
     *            the bean's name as defined in the bean configuration.
     * 
     * @return the BeanConfig for beanName, or null if beanName is not defined. The returned BeanConfig is a new cloned instance so it
     *  may be modified if desired.
     */
    public BeanConfig getBeanConfig(String beanName)
    {
        BeanConfig config = beanConfigMap.get(beanName);
        return config == null ? null : config.clone();
    }

    /**
     * Gets the BeanConfig for the specified bean name and overrides it with the specified parameters.
     * 
     * @param beanName
     *            the bean's name as defined in the bean configuration.
     * @param overrideParameters some parameters to override the bean configuration.
     * 
     * @return the BeanConfig for beanName, or null if beanName is not defined. The returned BeanConfig is a new cloned instance so it
     *  may be modified if desired.
     */
    public BeanConfig getBeanConfig(String beanName, Collection<ParameterAST> overrideParameters)
    {
        BeanConfig config = beanConfigMap.get(beanName);
        if (config != null) {
            config = config.clone();
            config.setParameters(overrideParameters);
        }

        return config;
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
