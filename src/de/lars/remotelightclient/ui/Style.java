package de.lars.remotelightclient.ui;

import java.awt.Color;
import javax.swing.ImageIcon;

import org.tinylog.Logger;

public class Style {
	
	private static String style = "Dark";
	
	public static void setStyle(String style) {
		if(style.equalsIgnoreCase("Light")) {
			setLightColors();
			Style.style = style;
		} else if(style.equalsIgnoreCase("Dark")) {
			setDarkColors();
			Style.style = style;
		}
	}
	
	public static String getStyle() {
		return style;
	}
	
	
	public static Color panelBackground = new Color(40, 40, 40);
	public static Color panelAccentBackground = new Color(60, 60, 60);
	public static Color panelDarkBackground = new Color(35, 35, 35);
	public static Color hoverBackground = new Color(100, 100, 100);
	public static Color buttonBackground = new Color(70, 70, 70);
	public static Color textColor = new Color(255, 255, 255);
	public static Color textColorDarker = new Color(220, 220, 220);
	public static Color accent = new Color(255, 160, 60);
	
	
	/*
	 * Light style
	 */
	private static void setLightColors() {
		panelBackground = new Color(245, 245, 245);
		panelAccentBackground = new Color(225, 225, 225);
		panelDarkBackground = new Color(180, 180, 180);
		hoverBackground = new Color(195, 195, 195);
		buttonBackground = new Color(205, 205, 205);
		textColor = new Color(0, 0, 0);
		textColorDarker = new Color(20, 20, 20);
		accent = new Color(255, 160, 60);
	}
	
	/*
	 * Dark style
	 */
	private static void setDarkColors() {
		panelBackground = new Color(40, 40, 40);
		panelAccentBackground = new Color(60, 60, 60);
		panelDarkBackground = new Color(35, 35, 35);
		hoverBackground = new Color(100, 100, 100);
		buttonBackground = new Color(70, 70, 70);
		textColor = new Color(255, 255, 255);
		textColorDarker = new Color(220, 220, 220);
		accent = new Color(255, 160, 60);
	}
	
	public static ImageIcon getMenuIcon(String filename) {
		return getIcon("menu", filename);
	}
	
	public static ImageIcon getSettingsIcon(String filename) {
		return getIcon("settings", filename);
	}
	
	public static ImageIcon getUiIcon(String filename) {
		return getIcon("ui", filename);
	}
	
	public static ImageIcon getIcon(String parent, String filename) {
		try {
			if(style.equalsIgnoreCase("Dark")) {
				return new ImageIcon(Style.class.getResource("/resourcen/" + parent + "/white/" + filename));
			}
			return new ImageIcon(Style.class.getResource("/resourcen/" + parent + "/black/" + filename));
			
		} catch(NullPointerException e) {
			Logger.error("Image '" + filename + "' not found!");
			return getIcon("ui", "error.png");
		}
	}

}
