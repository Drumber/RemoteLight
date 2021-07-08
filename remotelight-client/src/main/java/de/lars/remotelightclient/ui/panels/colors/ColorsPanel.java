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

package de.lars.remotelightclient.ui.panels.colors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.TabButtons;
import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightclient.ui.panels.colors.gradients.GradientsPanel;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightcore.lang.i18n;

public class ColorsPanel extends MenuPanel {
	private static final long serialVersionUID = 2572544853394733969L;
	
	private ColorPickerPanel colorPickerPanel;
	private GradientsPanel gradientsPanel;

	/**
	 * Create the panel.
	 */
	public ColorsPanel() {
		Main.getInstance().getMainFrame().showControlBar(true);
		Main.getInstance().getMainFrame().setControlBarPanel(new DefaultControlBar());
		
		colorPickerPanel = new ColorPickerPanel();
		gradientsPanel = new GradientsPanel();
		
		setLayout(new BorderLayout(0, 0));
		
		TabButtons tabBtns = new TabButtons();
		tabBtns.setFont(Style.getFontRegualar(14));
		tabBtns.addButton("ColorPicker");
		tabBtns.addButton("Gradients");
		tabBtns.setActionListener(onMenuButtonClicked);
		add(tabBtns, BorderLayout.NORTH);
		
		// ColorPicker is default panel
		tabBtns.selectButton("ColorPicker");
	}
	
	protected void selectMenu(String menuName) {
		Component centerComp = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
		
		if("colorpicker".equalsIgnoreCase(menuName)) {
			if(centerComp != null) {
				remove(centerComp);
			}
			add(colorPickerPanel, BorderLayout.CENTER);
		} else if("gradients".equalsIgnoreCase(menuName)) {
			if(centerComp != null) {
				remove(centerComp);
			}
			add(gradientsPanel, BorderLayout.CENTER);
		}
		
		updateUI();
	}
	
	private ActionListener onMenuButtonClicked = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			selectMenu(e.getActionCommand());
		}
	};
	
	@Override
	public void onEnd(MenuPanel newPanel) {
		colorPickerPanel.onEnd();
		gradientsPanel.onEnd();
		super.onEnd(newPanel);
	}
	
	@Override
	public String getName() {
		return i18n.getString("Basic.Colors");
	}

}
