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

package de.lars.remotelightclient.ui.font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontManager {
	
	private static Map<String, FontResource> fontMap = new HashMap<String, FontResource>();
	
	/**
	 * Register a new font
	 * @param name		font family name
	 * @param resource	font resource interface used for
	 * 					loading the font files
	 */
	public static void addFont(String name, FontResource resource) {
		fontMap.put(name, resource);
	}
	
	/**
	 * Remove a font from the list
	 * @param name		font family name
	 */
	public static void removeFont(String name) {
		fontMap.remove(name);
	}
	
	/**
	 * Get all registered fonts
	 * @return			HashMap including the FontResource interface for the font family
	 */
	public static Map<String, FontResource> getAllFonts() {
		return fontMap;
	}
	
	/**
	 * Get all registered font family names
	 * @return			ArrayList of all font names
	 */
	public static List<String> getAllFontNames() {
		return new ArrayList<String>(fontMap.keySet());
	}

}
