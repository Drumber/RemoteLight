/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightcore.lang;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class i18n {
	private static final String BUNDLE_NAME = "de.lars.remotelightcore.lang.bundles.bundle"; //$NON-NLS-1$
	private static final String SETTING_BUNDLE_NAME = "de.lars.remotelightcore.lang.setting_bundles.bundle"; //$NON-NLS-1$

	private static ResourceBundle userInterfaceBundle;
	private static ResourceBundle settingsBundle;

	private i18n() {
	}
	
	public static void initResourceBundle(Locale locale) {
		userInterfaceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
		settingsBundle = ResourceBundle.getBundle(SETTING_BUNDLE_NAME, locale);
	}
	
	public static void initResourceBundle() {
		initResourceBundle(Locale.getDefault());
	}
	
	public static void setLocale(String langCode) {
		Locale.setDefault(new Locale(LangUtil.langNameToCode(langCode)));
		initResourceBundle();
	}

	public static String getString(String key) {
		if(userInterfaceBundle == null)
			initResourceBundle();
		
		try {
			return userInterfaceBundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static String getSettingString(String key) {
		if(settingsBundle == null)
			initResourceBundle();
		
		try {
			return settingsBundle.getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}
	
}
