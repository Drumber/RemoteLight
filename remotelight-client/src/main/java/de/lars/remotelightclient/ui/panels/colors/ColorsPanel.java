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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.lang.i18n;

public class ColorsPanel extends MenuPanel {
	private static final long serialVersionUID = 2572544853394733969L;
	
	private ColorPickerPanel colorPickerPanel;
	private GradientsPanel gradientsPanel;
	private JPanel btnColorPicker;
	private JPanel btnGradients;

	/**
	 * Create the panel.
	 */
	public ColorsPanel() {
		Main.getInstance().getMainFrame().showControlBar(true);
		Main.getInstance().getMainFrame().setControlBarPanel(new DefaultControlBar());
		
		colorPickerPanel = new ColorPickerPanel();
		gradientsPanel = new GradientsPanel();
		
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout(0, 0));
		
		JPanel panelHeader = new JPanel();
		panelHeader.setBackground(Style.panelBackground);
		panelHeader.setLayout(new GridLayout(1, 2));
		
		btnColorPicker = createHeaderButton("ColorPicker");
		btnColorPicker.setName("colorpicker");
		panelHeader.add(btnColorPicker);
		
		btnGradients = createHeaderButton("Gradients");
		btnGradients.setName("gradients");
		panelHeader.add(btnGradients);
		
		add(panelHeader, BorderLayout.NORTH);
		
		// ColorPicker is default panel
		selectMenu("colorpicker");
	}
	
	protected void selectMenu(String menuName) {
		Component centerComp = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
		
		if("colorpicker".equalsIgnoreCase(menuName)) {
			btnColorPicker.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Style.accent));
			btnGradients.setBorder(null);
			if(centerComp != null) {
				remove(centerComp);
			}
			add(colorPickerPanel, BorderLayout.CENTER);
		} else if("gradients".equalsIgnoreCase(menuName)) {
			btnGradients.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Style.accent));
			btnColorPicker.setBorder(null);
			if(centerComp != null) {
				remove(centerComp);
			}
			add(gradientsPanel, BorderLayout.CENTER);
		}
		
		updateUI();
	}
	
	private MouseAdapter onMenuButtonClicked = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			selectMenu(e.getComponent().getName());
		};
	};
	
	private JPanel createHeaderButton(String title) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Style.panelBackground);
		panel.setPreferredSize(new Dimension(0, 30));
		panel.addMouseListener(onMenuButtonClicked);
		UiUtils.addHoverColor(panel, panel.getBackground(), Style.hoverBackground);
		
		JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
		lblTitle.setForeground(Style.textColor);
		lblTitle.setFont(Style.getFontRegualar(14));
		panel.add(lblTitle, BorderLayout.CENTER);
		return panel;
	}
	
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
