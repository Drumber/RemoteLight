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

package de.lars.remotelightclient.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.tinylog.Logger;

import de.lars.colorpicker.utils.ColorPickerStyle;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.utils.ui.FlatLafThemesUtil;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.utils.color.ColorUtil;
import jiconfont.IconCode;
import jiconfont.swing.IconFontSwing;

public class Style {
	
	public static final String[] STYLES = {"Light", "LightBlue", "Dark", "DarkRed", "GrayGreen", "LookAndFeel"};
	private static String style = "Dark";
	private static boolean blackIcon = false;
	
	private static int fontIconSize = 27;
	private static int fontHelpIconSize = 16;
	
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
		ColorPickerStyle.colorButtonBackground = Style.buttonBackground;
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
	// notification colors
	public static Color error = new Color(242, 34, 34);
	public static Color warn = new Color(242, 183, 34);
	public static Color info = Color.GRAY;
	public static Color success = new Color(48, 237, 38);
	public static Color debug = new Color(173, 154, 38);
	public static Color notification = info;
	public static Color important = new Color(255, 200, 0);
	
	
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
	
	public static Color getNotificationColor(NotificationType type) {
		switch (type) {
		case ERROR:
			return error;
		case WARN:
			return warn;
		case INFO:
			return info;
		case DEBUG:
			return debug;
		case NOTIFICATION:
			return notification;
		case SUCCESS:
			return success;
		case NONE:
			return info;
		case IMPORTANT:
			return important;
		default:
			return info;
		}
	}
	
	/*
	 * Icons
	 */
	
	public static ImageIcon getUiIcon(String filename) {
		return getIcon("ui", filename);
	}
	
	public static ImageIcon getIcon(String parent, String filename) {
		try {
			if(!blackIcon) {
				return new ImageIcon(Style.class.getResource("/resources/" + parent + "/white/" + filename));
			}
			return new ImageIcon(Style.class.getResource("/resources/" + parent + "/black/" + filename));
			
		} catch(NullPointerException e) {
			Logger.error("Image '" + filename + "' not found!");
			return getIcon("ui", "error.png");
		}
	}
	
	/**
	 * Get font icon with default text color
	 * @param iconCode IconCode
	 * @return new icon from IconCode
	 */
	public static Icon getFontIcon(IconCode iconCode) {
		return getFontIcon(iconCode, Style.textColor);
	}
	
	/**
	 * Get font icon with specified color
	 * @param iconCode IconCode
	 * @param color custom color
	 * @return new icon from IconCode with specified color
	 */
	public static Icon getFontIcon(IconCode iconCode, Color color) {
		return IconFontSwing.buildIcon(iconCode, fontIconSize, Style.textColor);
	}
	
	/**
	 * Get font icon with specified size
	 * @param iconCode IconCode
	 * @param fontSize custom font size
	 * @return new icon from IconCode with specified size
	 */
	public static Icon getFontIcon(IconCode iconCode, int fontSize) {
		return getFontIcon(iconCode, fontSize, Style.textColor);
	}
	
	/**
	 * Get font icon with specified size and color
	 * @param iconCode IconCode
	 * @param fontSize custom font size
	 * @param color icon color
	 * @return new icon from IconCode with specified size and color
	 */
	public static Icon getFontIcon(IconCode iconCode, int fontSize, Color color) {
		return IconFontSwing.buildIcon(iconCode, fontSize, color);
	}
	
	/**
	 * Get Help icon
	 * @return new help icon
	 */
	public static Icon getHelpIcon() {
		return getFontIcon(MenuIcon.HELP, fontHelpIconSize);
	}
	
	/*
	 * FONT
	 */
	private static Font regular;
	private static Font bold;
	private static Font light;
	
	public static void loadFonts() {
		// register icon font
		UiUtils.registerIconFont("icons/menuicons.ttf");
		// load fonts
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
