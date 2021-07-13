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

package de.lars.remotelightclient.utils.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.events.FontSizeEvent;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ColorTool;
import de.lars.remotelightcore.event.Listener;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.utils.DirectoryUtil;
import jiconfont.swing.IconFontSwing;

public class UiUtils {
	
	public static final int NORMAL_FONT_SIZE = 11;
	
	@Deprecated
	private static boolean disableTheming = true;
	
	public static void setThemingEnabled(boolean themingEnabled) {
		disableTheming = !themingEnabled;
	}
	
	public static boolean isThemingEnabled() {
		return !disableTheming;
	}
	
	public static Font loadFont(String name, int style) {
		String fName = DirectoryUtil.RESOURCES_CLASSPATH + "fonts/" + name;
		InputStream is = UiUtils.class.getResourceAsStream(fName);
		Font out = null;
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			out = font.deriveFont(style, getFontSize());
		} catch (FontFormatException | IOException e) {
			Logger.error(e, "Could not load font: " + fName);
		}
		return out;
	}
	
	/**
	 * Set the default font family for the UI. Does not update the font size.
	 * @param f		the new font resource
	 */
	public static void setUIFont(FontUIResource f) {
		Font newFont = f.deriveFont((float) getFontSize());
		UIManager.put("defaultFont", newFont);
	}
	
	/**
	 * Set the default font size.
	 * @param size	font size
	 */
	public static void setFontSize(int size) {
		Font font = UIManager.getFont("defaultFont");
		if(font == null) return;
		Font newFont = font.deriveFont((float) size);
		UIManager.put("defaultFont", newFont);
		Main.getInstance().getCore().getEventHandler().call(new FontSizeEvent(size));
	}
	
	/**
	 * Get the used default font size.
	 * @return	the default font size
	 */
	public static int getFontSize() {
		Font font = UIManager.getFont("defaultFont");
		return font != null ? font.getSize() : NORMAL_FONT_SIZE;
	}
	
	public static void registerIconFont(String path) {
		IconFontSwing.register(new MenuIconFont(DirectoryUtil.RESOURCES_CLASSPATH + "fonts/" + path));
	}
	
	public static String[] getAvailableFonts() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(Locale.US);
	}
	
	
	public static Component getComponentByName(JPanel panel, Object type, String name) {
		Component[] comp = panel.getComponents();
		for(int i = 0; i < comp.length; i++) {
			if(comp[i].getClass().isInstance(type)) {
				if(comp[i].getName().equals(name)) {
					return comp[i];
				}
			}
		}
		return null;
	}
	
	public static void bindBackground(Component comp, Supplier<Color> colorSupplier) {
		comp.addPropertyChangeListener("UI", e -> comp.setBackground(colorSupplier.get()));
		comp.setBackground(colorSupplier.get());
	}
	
	public static void bindElevation(Component comp, final int alpha) {
		comp.addPropertyChangeListener("UI", event -> {
			Component c = (Component) event.getSource();
			setElevation(c, alpha);
		});
		setElevation(comp, alpha);
	}
	
	public static void setElevation(Component comp, int alpha) {
		setElevation(comp, UIManager.getColor("Panel.background"), alpha);
	}
	
	/**
	 * Set the elevation of a component by blending the background color with
	 * a semi-transparent overlay.
	 * @param comp			the target component
	 * @param background	the background color that should be blended with the overlay
	 * @param alpha			the alpha value of the overlay (0..255)
	 */
	public static void setElevation(Component comp, Color background, int alpha) {
		Color c = getElevationColor(background, alpha);
		comp.setBackground(c);
	}
	
	/**
	 * Get the elevation color by blending the specified background color
	 * with a semi-transparent overlay (white on dark themes, black otherwise).
	 * @param background	the background color that should be blended with the overlay
	 * @param alpha			the alpha value of the overlay (0..255), negative to invert overlay
	 * @return	the resulting color
	 */
	public static Color getElevationColor(Color background, int alpha) {
		boolean isDark = Style.isDarkLaF() && alpha >= 0; // invert if alpha is negative
		alpha = Math.abs(alpha);
		// on dark themes use WHITE overlay, on light themes use BLACK overlay
		Color overlay = isDark ? new Color(255, 255, 255, alpha) : new Color(0, 0, 0, alpha);
		return ColorTool.alphaBlending(overlay, background);
	}
	
	public static void bindFont(JComponent comp, Font font) {
		bindFont(comp, font, false);
	}
	
	public static void bindFont(JComponent comp, Font font, boolean colorDarker) {
		Listener<FontSizeEvent> listener = e -> setFont(comp, font, colorDarker);
		// remove font size listener when component is removed
		comp.addPropertyChangeListener("ancestor", event -> {
			if(event.getNewValue() == null) {
				Main.getInstance().getCore().getEventHandler().unregister(FontSizeEvent.class, listener);
			}
		});
		// register font size listener
		Main.getInstance().getCore().getEventHandler().register(FontSizeEvent.class, listener);
		setFont(comp, font, colorDarker);
	}
	
	public static void setFont(JComponent comp, Font font, boolean colorDarker) {
		int sizeDifference = font.getSize() - NORMAL_FONT_SIZE;
		int newFontSize = getFontSize() + sizeDifference;
		comp.setFont(font.deriveFont((float) newFontSize));
		
		if(colorDarker) {
			comp.setForeground(UIManager.getColor("Label.disabledForeground"));
		}
	}
	
	public static void bindForeground(JComponent comp, Supplier<Color> colorSupplier) {
		comp.addPropertyChangeListener("UI", e -> comp.setForeground(colorSupplier.get()));
		comp.setForeground(colorSupplier.get());
	}
	
	public static void configureSpinner(JSpinner spinner) {
		// set width (columns)
		JComponent editor = spinner.getEditor();
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) editor).getTextField();
		jftf.setColumns(4);
		spinner.setEditor(editor);
	}
	
	public static void addHoverColor(JComponent comp, Color main, Color hover) {
		comp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				((JComponent) e.getSource()).setBackground(hover);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				((JComponent) e.getSource()).setBackground(main);
			}
		});
	}
	
	public static void addSliderMouseWheelListener(JSlider slider) {
		slider.addMouseWheelListener(sliderWheelListener);
	}
	
	private static MouseWheelListener sliderWheelListener = new MouseWheelListener() {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			JSlider slider = (JSlider) e.getSource();
			int notches = e.getWheelRotation();
			if (notches < 0) {
				slider.setValue(slider.getValue() + 1);
			} else if(notches > 0) {
				slider.setValue(slider.getValue() - 1);
			}
		}
	};

	
	public static void addWebsiteHyperlink(JLabel lbl, String url) {
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl.setToolTipText(url);
        lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (URISyntaxException | IOException ex) {
					Main.getInstance().showNotification(NotificationType.ERROR, "Could not open " + url);
				}
			}
		});
	}
	
	
	public static Component findChildWithName(Container container, String name) {
		if(container == null || name == null) return null;
		for(Component comp : container.getComponents()) {
			if(name.equals(comp.getName())) {
				return comp;
			}
			if(comp instanceof Container) {
				Component nestedFound = findChildWithName((Container) comp, name);
				if(nestedFound != null) {
					return nestedFound;
				}
			}
		}
		return null;
	}
	
}
