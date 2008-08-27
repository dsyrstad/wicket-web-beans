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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

/**
 * Represents the Configuration for a Component. 
 * <p/>
 *  
 * @author Dan Syrstad
 */
public class ComponentConfig implements Serializable
{
    private static final long serialVersionUID = -4705317346444856939L;

    private static Logger logger = Logger.getLogger(ComponentConfig.class.getName());
    /** Cache of pre-parsed component configs. */
    private static final Map<URL, CachedComponentConfigs> cachedComponentConfigs = new HashMap<URL, CachedComponentConfigs>();
    // Key is parameter name. 
    private Map<String, List<ParameterValueAST>> parameters = new HashMap<String, List<ParameterValueAST>>();

    private String componentName;
    private URL url;
    private ComponentRegistry componentRegistry;

    /**
     * Construct a ComponentConfig. If componentName is null, metadata is derived from a component
     * named "ROOT". 
     *
     * @param componentName the componentName to be used for metadata. May be null.
     * @param url the URL of the WWB configuration.
     * @param componentRegistry the ComponentRegistry used to determine visual components. May be null to use the default.
     */
    public ComponentConfig(String componentName, URL url, ComponentRegistry componentRegistry)
    {
        assert url != null;

        if (componentName == null) {
            this.componentName = "ROOT";
        }
        else {
            this.componentName = componentName;
        }

        this.url = url;

        if (componentRegistry == null) {
            this.componentRegistry = new ComponentRegistry();
        }
        else {
            this.componentRegistry = componentRegistry;
        }

        collectFromComponentConfig();
    }

    /**
     * Collection Beans from the beanprops file, if any.
     */
    private void collectFromComponentConfig()
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

        CachedComponentConfigs cachedConfig = cachedComponentConfigs.get(url);
        if (cachedConfig == null || cachedConfig.getModTimestamp() != timestamp) {
            if (cachedConfig != null) {
                logger.info("Re-reading changed file: " + url);
            }

            InputStream inStream = null;
            try {
                inStream = url.openStream();
                List<ComponentConfigAST> asts = new ComponentConfigParser(url.toString(), inStream).parse();
                cachedConfig = new CachedComponentConfigs(asts, timestamp);
                cachedComponentConfigs.put(url, cachedConfig);
            }
            catch (IOException e) {
                throw new RuntimeException("Error reading stream for URL: " + url, e);
            }
            finally {
                IOUtils.closeQuietly(inStream);
            }
        }

        boolean foundComponent = false;
        for (ComponentConfigAST componentConfigAst : cachedConfig.getAsts()) {
            if (componentName.equals(componentConfigAst.getName())) {
                foundComponent = true;
                for (ParameterAST paramAST : componentConfigAst.getParameters()) {
                    setParameter(paramAST.getName(), paramAST.getValues());
                }
            }
        }

        if (!foundComponent) {
            throw new RuntimeException("Could not find component '" + componentName + " in URL " + url);
        }

        ParameterValueAST classValue = getParameterValue("class");
        ParameterValueAST extendsValue = getParameterValue("extends");
        if (classValue == null && extendsValue == null) {
            throw new RuntimeException("Component " + componentName + " in URL " + url
                            + " must specify class or extends");
        }

        if (classValue != null && extendsValue != null) {
            throw new RuntimeException("Component " + componentName + " in URL " + url
                            + " cannot specify both class and extends");
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


    /**
     * A Cached Beanprops file.
     */
    @SuppressWarnings("serial")
    private static final class CachedComponentConfigs implements Serializable
    {
        private List<ComponentConfigAST> asts;
        private long modTimestamp;

        CachedComponentConfigs(List<ComponentConfigAST> asts, long modTimestamp)
        {
            this.asts = asts;
            this.modTimestamp = modTimestamp;
        }

        List<ComponentConfigAST> getAsts()
        {
            return asts;
        }

        long getModTimestamp()
        {
            return modTimestamp;
        }
    }
}
