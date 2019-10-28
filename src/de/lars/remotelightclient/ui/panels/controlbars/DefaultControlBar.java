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
package de.lars.remotelightclient.ui.panels.controlbars;

import javax.swing.JPanel;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.controlbars.comps.BrightnessSlider;
import de.lars.remotelightclient.ui.panels.controlbars.comps.OutputInfo;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.border.EmptyBorder;

public class DefaultControlBar extends ControlBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6631254065528102825L;
	private Dimension size = new Dimension(100, 45);
	private JPanel bgrAction;

	/**
	 * Create the panel.
	 */
	public DefaultControlBar() {
		setBorder(new EmptyBorder(0, 5, 0, 5));
		setBackground(Style.panelDarkBackground);
		setLayout(new GridLayout(0, 3, 2, 0));
		setPreferredSize(size);
		setMaximumSize(size);
		
		JPanel bgrBrightness = new JPanel();
		bgrBrightness.setBackground(Style.panelDarkBackground);
		add(bgrBrightness);
		bgrBrightness.setLayout(new BorderLayout(0, 0));
		bgrBrightness.add(new BrightnessSlider(Style.panelDarkBackground));
		
		JPanel bgrOutput = new JPanel();
		bgrOutput.setBackground(Style.panelDarkBackground);
		add(bgrOutput);
		bgrOutput.setLayout(new BorderLayout(0, 0));
		bgrOutput.add(new OutputInfo(Style.panelDarkBackground));
		
		bgrAction = new JPanel();
		bgrAction.setBackground(Style.panelDarkBackground);
		add(bgrAction);
		bgrAction.setLayout(new BorderLayout(0, 0));
	}
	
	public void setActionPanel(JPanel panel) {
		bgrAction.removeAll();
		bgrAction.add(panel);
		updateUI();
	}

}
