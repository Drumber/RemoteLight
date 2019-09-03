package de.lars.remotelightclient.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

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

}
