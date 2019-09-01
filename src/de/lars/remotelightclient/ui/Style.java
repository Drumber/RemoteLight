package de.lars.remotelightclient.ui;

import java.awt.Color;
import javax.swing.ImageIcon;

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
	public static Color hoverBackground = new Color(100, 100, 100);
	public static Color textColor = new Color(255, 255, 255);
	public static Color textColorDarker = new Color(220, 220, 220);
	public static Color accent = new Color(50, 125, 255);
	
	
	/*
	 * Light style
	 */
	private static void setLightColors() {
		panelBackground = new Color(245, 245, 245);
		panelAccentBackground = new Color(225, 225, 225);
		hoverBackground = new Color(200, 200, 200);
		textColor = new Color(0, 0, 0);
		textColorDarker = new Color(20, 20, 20);
		accent = new Color(50, 125, 255);
	}
	
	/*
	 * Dark style
	 */
	private static void setDarkColors() {
		panelBackground = new Color(40, 40, 40);
		panelAccentBackground = new Color(60, 60, 60);
		hoverBackground = new Color(100, 100, 100);
		textColor = new Color(255, 255, 255);
		textColorDarker = new Color(220, 220, 220);
		accent = new Color(50, 125, 255);
	}
	
	public static ImageIcon getMenuIcon(String filename) {
		if(style.equalsIgnoreCase("Dark")) {
			return new ImageIcon(Style.class.getResource("/resourcen/menu/white/" + filename));
		}
		return new ImageIcon(Style.class.getResource("/resourcen/menu/black/" + filename));
	}

}
