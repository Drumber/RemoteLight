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
