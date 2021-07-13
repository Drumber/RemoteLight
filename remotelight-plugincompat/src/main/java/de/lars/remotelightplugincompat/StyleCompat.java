package de.lars.remotelightplugincompat;

import java.awt.Color;
import java.lang.reflect.Field;

import org.tinylog.Logger;

import de.lars.remotelightclient.ui.Style;

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
	
	
	public static Color error() {
		return getColor("error", () -> Style.error().get());
	}
	
	public static Color warn() {
		return getColor("warn", () -> Style.warn().get());
	}
	
	public static Color info() {
		return getColor("info", () -> Style.info().get());
	}
	
	public static Color success() {
		return getColor("success", () -> Style.success().get());
	}
	
	public static Color debug() {
		return getColor("debug", () -> Style.debug().get());
	}
	
	public static Color notification() {
		return getColor("notification", () -> Style.notification().get());
	}
	
	public static Color important() {
		return getColor("important", () -> Style.important().get());
	}
	
	
	private interface AltColor {
		public Color getAltColor();
	}

}
