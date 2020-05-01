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
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.tinylog.Logger;

import de.lars.colorpicker.utils.ColorPickerStyle;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.utils.ColorUtil;
import de.lars.remotelightclient.utils.FlatLafThemesUtil;
import de.lars.remotelightclient.utils.UiUtils;

public class Style {
	
	public static final String[] STYLES = {"Light", "LightBlue", "Dark", "DarkRed", "GrayGreen", "LookAndFeel"};
	private static String style = "Dark";
	private static boolean blackIcon = false;
	
	public static void setStyle(String style) {
		if(style.equalsIgnoreCase("Light")) {
			setLightColors();
		} else if(style.equalsIgnoreCase("Dark")) {
			setDarkColors();
		} else if(style.equalsIgnoreCase("DarkRed")) {
			setDarkRedColors();
		} else if(style.equalsIgnoreCase("GrayGreen")) {
			setGrayGreenColors();
		} else if(style.equalsIgnoreCase("LightBlue")) {
			setLightBlueColors();
		} else if(style.equalsIgnoreCase("LookAndFeel")) {
			setLaFColors();
		} else {
			return;
		}
		Style.style = style;
		// update color picker style
		ColorPickerStyle.setBackgrounds(Style.panelBackground);
		ColorPickerStyle.colorText = Style.textColor;
	}
	
	public static void setStyle() {
		String style = ((SettingSelection) Main.getInstance().getSettingsManager().getSettingFromId("ui.style")).getSelected();
		setStyle(style);
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
		blackIcon = true;
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
		blackIcon = false;
		panelBackground = Color.decode("#212121");
		panelAccentBackground = Color.decode("#333333");
		panelDarkBackground = Color.decode("#1C1C1C");
		hoverBackground = Color.decode("#3E3E3E");
		buttonBackground = Color.decode("#2E2E2E");
		textColor = new Color(255, 255, 255);
		textColorDarker = new Color(200, 200, 200);
		accent = new Color(255, 160, 60);
	}
	
	/*
	 * DarkRed style
	 */
	private static void setDarkRedColors() {
		blackIcon = false;
		panelBackground = Color.decode("#1A1A1D");
		panelAccentBackground = Color.decode("#282828");
		panelDarkBackground = Color.decode("#141414");
		hoverBackground = Color.decode("#4E4E50");
		buttonBackground = Color.decode("#282828");
		textColor = new Color(255, 255, 255);
		textColorDarker = new Color(200, 200, 200);
		accent = Color.decode("#d42219");
	}
	
	/*
	 * GrayGreen style
	 */
	private static void setGrayGreenColors() {
		blackIcon = false;
		panelBackground = Color.decode("#36363F");
		panelAccentBackground = Color.decode("#31323C");
		panelDarkBackground = Color.decode("#26262E");
		hoverBackground = Color.decode("#41414F");
		buttonBackground = Color.decode("#31323C");
		textColor = new Color(255, 255, 255);
		textColorDarker = new Color(200, 200, 200);
		accent = Color.decode("#1EB980");
	}
	
	/*
	 * LightBlue style
	 */
	private static void setLightBlueColors() {
		blackIcon = true;
		panelBackground = new Color(245, 245, 245);
		panelAccentBackground = Color.decode("#B3E5FC");
		panelDarkBackground = Color.decode("#a6cfe0");
		hoverBackground = Color.decode("#81D4FA");
		buttonBackground = Color.decode("#B3E5FC");
		textColor = new Color(0, 0, 0);
		textColorDarker = new Color(30, 30, 30);
		accent = Color.decode("#03A9F4");
	}
	
	/*
	 * Look and Feel Colors
	 */
	private static void setLaFColors() {
		panelBackground = UIManager.getColor("Panel.background");
		panelAccentBackground = UIManager.getColor("Panel.background").brighter();
		panelDarkBackground = UIManager.getColor("Panel.background").darker();
		hoverBackground = UIManager.getColor("Button.hoverBackground");
		Color btnGradientBg = UIManager.getColor("Button.startBackground");
		buttonBackground = btnGradientBg != null ? btnGradientBg : UIManager.getColor("Button.background");
		textColor = UIManager.getColor("Label.foreground");
		textColorDarker = UIManager.getColor("Label.disabledForeground");
		accent = UIManager.getColor("Component.focusedBorderColor");
		blackIcon = ColorUtil.getAvgRgbValue(panelBackground) > 180;
		fixButtonBackgroud();
	}
	
	/**
	 * Fix for some themes that have the same button background as panel background
	 */
	private static void fixButtonBackgroud() {
		if(panelBackground.getRGB() == buttonBackground.getRGB())
			buttonBackground = buttonBackground.brighter();
	}
	
	
	public static List<String> getLookAndFeels() {
		List<String> laFs = new ArrayList<String>();
		laFs.add("System default");
		laFs.add("Java default");
		laFs.addAll(FlatLafThemesUtil.getAllThemeNames());
		return laFs;
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
			if(!blackIcon) {
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
