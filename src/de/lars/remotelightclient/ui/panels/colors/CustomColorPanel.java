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
package de.lars.remotelightclient.ui.panels.colors;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import java.awt.BorderLayout;

public class CustomColorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4871730974494095833L;
	private static CustomColorPanel selectedPanel = null;
	private static Dimension size = new Dimension(80, 80);
	

	/**
	 * Create the panel.
	 */
	public CustomColorPanel(Color color) {
		setBackground(color);
		setPreferredSize(size);
		setMaximumSize(size);
		
		setLayout(new BorderLayout());
	}
	
	
	public static CustomColorPanel getSelectedPanel() {
		return selectedPanel;
	}
	
	public static void setSelectedPanel(CustomColorPanel panel) {
		selectedPanel = panel;
	}
	
	public static void resetPanelSize() {
		size = new Dimension(80, 80);
	}
	
	public static void setPanelSize(Dimension size) {
		CustomColorPanel.size = size;
	}
	
	public static Dimension getPanelSize() {
		return size;
	}

}
