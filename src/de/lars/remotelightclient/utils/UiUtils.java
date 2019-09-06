package de.lars.remotelightclient.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import de.lars.remotelightclient.ui.Style;

public class UiUtils {
	
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
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBackground(Style.buttonBackground);
        btn.setForeground(Style.textColor);
        btn.addMouseListener(buttonHoverListener);
	}
	
	public static void configureButtonWithBorder(JButton btn, Color border) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBackground(Style.buttonBackground);
        btn.setForeground(Style.textColor);
        btn.setBorder(BorderFactory.createLineBorder(border));
        btn.addMouseListener(buttonHoverListener);
	}
	
	private static MouseAdapter buttonHoverListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			btn.setBackground(Style.hoverBackground);
		}
		@Override
		public void mouseExited(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			btn.setBackground(Style.buttonBackground);
		}
	};
	
	public static void configureTabbedPane(JTabbedPane tp) {
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

}
