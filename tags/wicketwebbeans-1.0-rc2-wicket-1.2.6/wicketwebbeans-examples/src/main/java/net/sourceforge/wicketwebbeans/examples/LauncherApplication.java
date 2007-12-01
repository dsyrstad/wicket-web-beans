package net.sourceforge.wicketwebbeans.examples;


import net.sourceforge.wicketwebbeans.examples.complex.ExpPage2;
import wicket.protocol.http.WebApplication;

public class LauncherApplication extends WebApplication {

	public LauncherApplication() {
	}

	@Override
	public Class getHomePage() {
		return LauncherPage.class;
	}
}
