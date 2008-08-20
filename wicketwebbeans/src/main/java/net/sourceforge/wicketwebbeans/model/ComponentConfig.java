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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;


/**
 * Represents the Configuration for a Component. 
 * <p/>
 *  
 * @author Dan Syrstad
 */
public class ComponentConfig extends MetaData implements Serializable
{
    private static final long serialVersionUID = -4705317346444856939L;

    private static Logger logger = Logger.getLogger(ComponentConfig.class.getName());
    /** Cache of pre-parsed component configs. */
    private static final Map<URL, CachedComponentConfigs> cachedComponentConfigs = new HashMap<URL, CachedComponentConfigs>();
    
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
            catch (URISyntaxException e) { /* Ignore - treat as zero */ }
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
                logger.severe("Error reading stream for URL: " + url);
            }
            finally {
                IOUtils.closeQuietly(inStream);
            }
        }
        
        for (ComponentConfigAST componentConfigAst : cachedConfig.getAsts()) {
            if (componentName.equals(componentConfigAst.getName())) {
                for (ParameterAST paramAST : componentConfigAst.getParameters()) {
                    setParameter(paramAST.getName(), paramAST.getValues());
                }
            }
        }
    }

    /**
     * Determines if all parameters specified have been consumed.
     * 
     * @param unconsumedMsgs a set of messages that are returned with the parameter keys that were specified but not consumed.
     * 
     * @return true if all parameters specified have been consumed.
     */
    public boolean areAllParametersConsumed(Set<String> unconsumedMsgs)
    {
        return super.areAllParametersConsumed("Component " + componentName, unconsumedMsgs);
    }

    /**
     * Logs a warning if any parameter specified have not been consumed.
     */
    public void warnIfAnyParameterNotConsumed()
    {
        Set<String> msgs = new HashSet<String>();
        if (!areAllParametersConsumed(msgs)) {
            for (String msg : msgs) {
                logger.warning(msg);
            }
        }
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
