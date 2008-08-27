package net.sourceforge.wicketwebbeans.examples;

import org.apache.wicket.protocol.http.WebApplication;

public class LauncherApplication extends WebApplication
{

    public LauncherApplication()
    {
    }

    @Override
    public Class getHomePage()
    {
        return LauncherPage.class;
    }
}
