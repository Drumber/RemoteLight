/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.plaf.FontUIResource;

import org.tinylog.Logger;

import de.lars.remotelightclient.utils.UiUtils;

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
	public static Color textColorDarker = new Color(200, 200, 200);
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
		textColorDarker = new Color(30, 30, 30);
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
		textColorDarker = new Color(200, 200, 200);
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
	
	/*
	 * FONT
	 */
	private static Font regular;
	private static Font bold;
	private static Font light;
	
	public static void loadFonts() {
		regular = UiUtils.loadFont("muli/Muli-Regular.ttf", Font.PLAIN);
		bold = UiUtils.loadFont("muli/Muli-ExtraBold.ttf", Font.BOLD);
		light = UiUtils.loadFont("muli/Muli-Light.ttf", Font.ITALIC);
		if(regular != null) {
			UiUtils.setUIFont(new FontUIResource(regular));
		}
	}
	
	public static Font getFontRegualar(int size) {
		if(regular != null) {
			return regular.deriveFont(Font.PLAIN, size);
		}
		return new Font("Tahoma", Font.PLAIN, size);
	}
	
	public static Font getFontBold(int size) {
		if(bold != null) {
			return bold.deriveFont(Font.PLAIN, size);
		}
		return new Font("Tahoma", Font.BOLD, size);
	}
	
	public static Font getFontLight(int size) {
		if(bold != null) {
			return light.deriveFont(Font.ITALIC, size);
		}
		return new Font("Tahoma", Font.ITALIC, size);
	}

}
