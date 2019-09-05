package de.lars.remotelightclient.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class i18n {
	public final static String[] LANGUAGES = {"en", "de"};
	private static final String BUNDLE_NAME = "de.lars.remotelightclient.lang.mainFrame.basic"; //$NON-NLS-1$

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
	
	public static String[] langCodeToName(String[] codes) {
		List<String> tmp = new ArrayList<>();
		for(String s : codes) {
			tmp.add(new Locale(s).getDisplayLanguage());
		}
		return tmp.toArray(new String[tmp.size()]);
	}
	
	public static String langNameToCode(String name) {
		for(String s : langCodeToName(LANGUAGES)) {
			if(name.equalsIgnoreCase(s)) {
				return s;
			}
		}
		return "en"; //prevent NullPointException
	}
}
