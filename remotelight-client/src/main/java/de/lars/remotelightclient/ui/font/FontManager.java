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
