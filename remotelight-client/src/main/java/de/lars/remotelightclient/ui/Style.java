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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

import org.tinylog.Logger;

import com.formdev.flatlaf.FlatLaf;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.font.FontManager;
import de.lars.remotelightclient.ui.font.FontResource;
import de.lars.remotelightclient.utils.ColorTool;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;
import jiconfont.IconCode;
import jiconfont.swing.IconFontSwing;

public class Style {
	
	private static int fontIconSize = 27;
	private static int fontHelpIconSize = 16;
	
	public static Supplier<Color> panelBackground() {
		return () -> getUIColor("Panel.background");
	}
	
	public static Supplier<Color> panelDarkBackground() {
		return () -> UiUtils.getElevationColor(panelBackground().get(), -20);
	}
	
	public static Supplier<Color> panelAccentBackground() {
		return () -> UiUtils.getElevationColor(panelBackground().get(), 20);
	}
	
	public static Supplier<Color> buttonBackground() {
		return () -> {
			Color btnGradientBg = getUIColor("Button.startBackground");
			return btnGradientBg != null ? btnGradientBg : getUIColor("Button.background");
		};
	}
	
	public static Supplier<Color> hoverBackground() {
		return () -> UiUtils.getElevationColor(buttonBackground().get(), 20);
	}
	
	public static Supplier<Color> accent() {
		return () -> {
			Color focusColor = getUIColor("Component.focusColor");
			// if focusColor is equal to the panel color, return focusedBorderColor
			return ColorTool.isEqual(focusColor, panelBackground().get()) ? getUIColor("Component.focusedBorderColor") : focusColor;
		};
	}
	
	public static Supplier<Color> textColor() {
		return () -> getUIColor("Label.foreground");
	}
	
	public static Supplier<Color> textColorDarker() {
		return () -> getUIColor("Label.disabledForeground");
	}
	
	
	// Notification colors
	
	public static Supplier<Color> error() {
		return () -> UIManager.getColor("Component.error.focusedBorderColor") != null ? 
				getUIColor("Component.error.focusedBorderColor") : new Color(242, 34, 34);
	}

	public static Supplier<Color> warn() {
		return () -> UIManager.getColor("Component.warning.focusedBorderColor") != null ? 
				getUIColor("Component.warning.focusedBorderColor") : new Color(242, 183, 34);
	}
	
	public static Supplier<Color> info() {
		return () -> Color.GRAY;
	}
	
	public static Supplier<Color> success() {
		return () -> new Color(40, 167, 69);
	}
	
	public static Supplier<Color> debug() {
		return () -> new Color(173, 154, 38);
	}
	
	public static Supplier<Color> notification() {
		return info();
	}
	
	public static Supplier<Color> important() {
		return () -> new Color(255, 200, 0);
	}
	
	/**
	 * This makes sure the color from the UIManager is from type
	 * {@link java.awt.Color} and does not return a {@link javax.swing.plaf.ColorUIResource}
	 * since this could cause some weird problems.
	 * @param key	UIManager resource key
	 * @return	{@link java.awt.Color}
	 */
	public static Color getUIColor(String key) {
		Color color = UIManager.getColor(key);
		if(color instanceof ColorUIResource) {
			return new Color(color.getRGB());
		}
		return color;
	}
	
	
	public static List<String> getLookAndFeels() {
		List<String> names = new ArrayList<String>();
		names.add("System default");
		names.addAll(getLookAndFeelInfo()
				.stream()
				.map(info -> info.getName())
				.collect(Collectors.toList()));
		return names;
	}
	
	/**
	 * Get a list of all installed Look and Feels
	 */
	public static List<LookAndFeelInfo> getLookAndFeelInfo() {
		return Arrays.asList(UIManager.getInstalledLookAndFeels());
	}
	
	public static boolean isDarkLaF() {
		LookAndFeel laf = UIManager.getLookAndFeel();
		if(laf instanceof FlatLaf) {
			return ((FlatLaf) laf).isDark();
		}
		return false;
	}
		
	public static Color getNotificationColor(NotificationType type) {
		switch (type) {
		case ERROR:
			return error().get();
		case WARN:
			return warn().get();
		case INFO:
			return info().get();
		case DEBUG:
			return debug().get();
		case NOTIFICATION:
			return notification().get();
		case SUCCESS:
			return success().get();
		case NONE:
			return info().get();
		case IMPORTANT:
			return important().get();
		default:
			return info().get();
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
			if(isDarkLaF()) {
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
		return getFontIcon(iconCode, Style.textColor().get());
	}
	
	/**
	 * Get font icon with specified color
	 * @param iconCode IconCode
	 * @param color custom color
	 * @return new icon from IconCode with specified color
	 */
	public static Icon getFontIcon(IconCode iconCode, Color color) {
		return IconFontSwing.buildIcon(iconCode, fontIconSize, color);
	}
	
	/**
	 * Get font icon with specified size
	 * @param iconCode IconCode
	 * @param fontSize custom font size
	 * @return new icon from IconCode with specified size
	 */
	public static Icon getFontIcon(IconCode iconCode, int fontSize) {
		return getFontIcon(iconCode, fontSize, Style.textColor().get());
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
	 * Get a rotated font icon with specified size and color
	 * @param iconCode IconCode
	 * @param fontSize custom font size
	 * @param color icon color
	 * @param rotation icon rotation in degree
	 * @return new icon from IconCode with specified size, color and rotation
	 */
	public static Icon getFontIcon(IconCode iconCode, int fontSize, Color color, double rotation) {
		BufferedImage image = (BufferedImage) IconFontSwing.buildImage(iconCode, fontSize, color);
		BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		Graphics2D g2 = out.createGraphics();
		g2.rotate(Math.toRadians(rotation), out.getWidth() / 2, out.getHeight() / 2);
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return new ImageIcon(out);
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
	
	public static void initFonts() {
		// register icon font
		UiUtils.registerIconFont("icons/menuicons.ttf");
		// load font from settings
		setSelectedFont();
	}
	
	/**
	 * Set UI font and size from setting value of {@code ui.font} and {@code ui.fontsize}.
	 */
	public static void setSelectedFont() {
		SettingsManager sm = Main.getInstance().getSettingsManager();
		SettingSelection settingFont = sm.getSetting(SettingSelection.class, "ui.font");
		SettingInt settingSize = sm.getSetting(SettingInt.class, "ui.fontsize");
		if(settingFont != null) {
			setUiFont(settingFont.getSelected());
		}
		if(settingSize != null) {
			UiUtils.setFontSize(settingSize.get());
		}
		FlatLaf.updateUILater();
	}
	
	/**
	 * Set the font of the UI. The font must be registered in
	 * the {@link FontManager} and must not be null.
	 * @param name	font family name
	 * @return		true if the font was set, false otherwise
	 */
	public static boolean setUiFont(String name) {
		FontResource fr = FontManager.getAllFonts().get(name);
		if(fr == null)
			return false;
		Font reg = fr.getRegular();
		if(reg != null)
			regular = reg;
		Font bol = fr.getBold();
		if(bol != null)
			bold = bol;
		Font ita = fr.getItalic();
		if(ita != null)
			light = ita;
		// set default ui font
		UiUtils.setUIFont(new FontUIResource(reg));
		return true;
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
