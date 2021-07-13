package de.lars.remotelightclient.ui;

import java.awt.Color;
import java.lang.reflect.Field;

import org.tinylog.Logger;

/**
 * Compatibility class for {@link Style} before commit #ff537c41d3f651c59ca619337fff5fc8771c0c25.
 */
public class StyleCompat {
	
	private static Field getField(String name) {
		try {
			return Style.class.getField(name);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static Color getColor(String name) {
		Field field = getField(name);
		if(field != null) {
			try {
				return (Color) field.get(null);
			} catch (Exception e) {
				Logger.error("Could not get value of field '" + name + "'.");
			}
		}
		return null;
	}
	
	private static Color getColor(String name, AltColor alternative) {
		Color color = getColor(name);
		if(color != null) {
			return color;
		}
		return alternative.getAltColor();
	}
	
	public static Color panelBackground() {
		return getColor("panelBackground", () -> Style.panelBackground().get());
	}
	
	public static Color panelAccentBackground() {
		return getColor("panelAccentBackground", () -> Style.panelAccentBackground().get());
	}
	
	public static Color panelDarkBackground() {
		return getColor("panelDarkBackground", () -> Style.panelDarkBackground().get());
	}
	
	public static Color hoverBackground() {
		return getColor("hoverBackground", () -> Style.hoverBackground().get());
	}
	
	public static Color buttonBackground() {
		return getColor("buttonBackground", () -> Style.buttonBackground().get());
	}
	
	public static Color textColor() {
		return getColor("textColor", () -> Style.textColor().get());
	}
	
	public static Color textColorDarker() {
		return getColor("textColorDarker", () -> Style.textColorDarker().get());
	}
	
	public static Color accent() {
		return getColor("accent", () -> Style.accent().get());
	}
	
	
	private interface AltColor {
		public Color getAltColor();
	}

}
