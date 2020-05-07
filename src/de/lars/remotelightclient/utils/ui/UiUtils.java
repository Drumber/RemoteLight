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
package de.lars.remotelightclient.utils.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.plaf.FontUIResource;

import org.tinylog.Logger;

import de.lars.remotelightclient.ui.Style;

public class UiUtils {
	
	private static boolean disableTheming = true;
	
	public static void setThemingEnabled(boolean themingEnabled) {
		disableTheming = !themingEnabled;
	}
	
	public static Font loadFont(String name, int style) {
		String fName = "/resourcen/fonts/" + name;
		InputStream is = UiUtils.class.getResourceAsStream(fName);
		Font out = null;
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			out = font.deriveFont(style, 11);
		} catch (FontFormatException | IOException e) {
			Logger.error(e, "Could not load font: " + fName);
		}
		return out;
	}
	
	//https://stackoverflow.com/a/7434935
	public static void setUIFont(FontUIResource f) {
		Enumeration<?> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, f);
			}
		}
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
	
	public static void configureButton(JButton btn) {
		configureButton(btn, true);
	}
	
	public static void configureButton(JButton btn, boolean hoverListener) {
		if(disableTheming) {
			btn.setContentAreaFilled(true);
			btn.setFocusable(false);
			return;
		}
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFocusable(true);
        btn.setOpaque(true);
        btn.setBackground(Style.buttonBackground);
        btn.setForeground(Style.textColor);
        if(hoverListener)
        	btn.addMouseListener(buttonHoverListener);
	}
	
	public static void configureButtonWithBorder(JButton btn, Color border) {
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createLineBorder(border));
		if(disableTheming) return;
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBackground(Style.buttonBackground);
        btn.setForeground(Style.textColor);
        btn.addMouseListener(buttonHoverListener);
	}
	
	private static MouseAdapter buttonHoverListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			if(disableTheming) return;
			JButton btn = (JButton) e.getSource();
			btn.setBackground(Style.hoverBackground);
		}
		@Override
		public void mouseExited(MouseEvent e) {
			if(disableTheming) return;
			JButton btn = (JButton) e.getSource();
			btn.setBackground(Style.buttonBackground);
		}
	};
	
	public static void configureTabbedPane(JTabbedPane tp) {
		if(disableTheming) return;
		tp.setBackground(Style.panelBackground);
		tp.setBorder(BorderFactory.createEmptyBorder());
		tp.setOpaque(true);
		tp.setFocusable(false);
		for(int i = 0; i < tp.getTabCount(); i++) {
			tp.getComponentAt(i).setBackground(Style.panelBackground);
			tp.setBackgroundAt(i, Style.panelBackground);
			if(tp.getComponentAt(i) instanceof JPanel) {
				JPanel p = (JPanel) tp.getComponentAt(i);
				p.setOpaque(true);
				p.setBorder(BorderFactory.createEmptyBorder());
				for(Component co : p.getComponents()) {
					co.setBackground(Style.panelBackground);
					if(co instanceof AbstractColorChooserPanel) {
						AbstractColorChooserPanel ac = (AbstractColorChooserPanel) co;
						ac.setBorder(BorderFactory.createEmptyBorder());
						for(Component com : ac.getComponents()) {
							if(com instanceof JComponent) {
								JComponent jc = (JComponent) com;
								jc.setBackground(Style.panelBackground);
								jc.setOpaque(true);
								jc.setBorder(BorderFactory.createEmptyBorder());
								jc.setFocusable(false);
								jc.setForeground(Style.textColor);
								for(Component comp : jc.getComponents()) {
									if(comp instanceof JComponent) {
										JComponent jco = (JComponent) comp;
										jco.setBackground(Style.panelBackground);
										jco.setForeground(Style.textColor);
										jco.setOpaque(true);
										jco.setFocusable(false);
									}
								}
							}
						}
					}
				}
			}
		}
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
				}
			}
		});
	}
	
}
