package net.sourceforge.wicketwebbeans.test;

import java.io.File;
import java.net.URL;

import net.sourceforge.wicketwebbeans.model.BeanFactory;

import org.apache.commons.io.FileUtils;

public class TestUtils
{
    private TestUtils()
    {
    }

    public static BeanFactory createBeanFactory(String configStr) throws Exception
    {
        return new BeanFactory().loadBeanConfig(createURL(configStr));
    }

    public static URL createURL(String configStr) throws Exception
    {
        File tmpFile = File.createTempFile("config", ".wwb");
        tmpFile.deleteOnExit();
        FileUtils.writeStringToFile(tmpFile, configStr);
        return tmpFile.toURI().toURL();
    }
}
