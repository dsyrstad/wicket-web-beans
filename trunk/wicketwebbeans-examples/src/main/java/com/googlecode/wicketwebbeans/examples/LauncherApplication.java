package com.googlecode.wicketwebbeans.examples;



import org.apache.wicket.protocol.http.WebApplication;

import com.googlecode.wicketwebbeans.examples.complex.ExpPage2;

public class LauncherApplication extends WebApplication {

	public LauncherApplication() {
	}

	@Override
	public Class getHomePage() {
		return LauncherPage.class;
	}
}
