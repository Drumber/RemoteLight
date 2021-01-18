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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LangUtil {
	
	public final static String[] LANGUAGES = {"en", "de", "fr"};
	
	
	public static String[] langCodeToName(String[] codes) {
		List<String> tmp = new ArrayList<>();
		for(String s : codes) {
			tmp.add(langCodeToName(s));
		}
		return tmp.toArray(new String[tmp.size()]);
	}
	
	public static String langCodeToName(String code) {
		return new Locale(code).getDisplayName(new Locale("en"));
	}
	
	public static String langNameToCode(String name) {
		for(int i = 0; i < LANGUAGES.length; i++) {
			if(name.equalsIgnoreCase(langCodeToName(LANGUAGES[i]))) {
				return LANGUAGES[i];
			}
		}
		return "en"; //prevent NullPointException
	}

}
