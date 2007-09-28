package wicket.contrib.webbeans.examples;


import wicket.contrib.webbeans.examples.complex.ExpPage2;
import wicket.protocol.http.WebApplication;

public class LauncherApplication extends WebApplication {

	public LauncherApplication() {
	}

	@Override
	public Class getHomePage() {
		return LauncherPage.class;
	}
}
