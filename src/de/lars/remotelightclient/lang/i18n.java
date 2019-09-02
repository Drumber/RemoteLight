package de.lars.remotelightclient.lang;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class i18n {
	private static final String BUNDLE_NAME = "de.lars.remotelightclient.lang.mainFrame.localtest"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private i18n() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
