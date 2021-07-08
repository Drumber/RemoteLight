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

package de.lars.remotelightclient.ui.panels.settings.settingComps;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.lars.colorpicker.ColorPicker;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ColorTool;
import de.lars.remotelightcore.settings.types.SettingColor;

public class SettingColorPanel extends SettingPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7949888285481977614L;
	private SettingColor setting;
	private JPanel panelColor;

	/**
	 * Create the panel.
	 */
	public SettingColorPanel(SettingColor setting) {
		super(setting);
		this.setting = setting;
		
		JLabel lblName = new JLabel("name");
		lblName.setText(setting.getName());
		add(lblName);
		
		panelColor = new JPanel();
		panelColor.setBackground(ColorTool.convert(setting.get()));
		panelColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Color color = ColorPicker.showSimpleDialog("Choose a color", panelColor.getBackground(), true);
				if(color != null) {
					panelColor.setBackground(color);
					SettingColorPanel.this.onChanged(SettingColorPanel.this);
				}
			}
		});
		panelColor.setPreferredSize(new Dimension(15, 15));
		add(panelColor);
		
		if(setting.getDescription() != null && !setting.getDescription().isEmpty()) {
			JLabel lblHelp = new JLabel("");
			lblHelp.setIcon(Style.getHelpIcon());
			lblHelp.setToolTipText(setting.getDescription());
			add(lblHelp);
		}
	}

	@Override
	public void setValue() {
		setting.setValue(ColorTool.convert(panelColor.getBackground()));
	}

	@Override
	public void updateComponents() {
		panelColor.setBackground(ColorTool.convert(setting.get()));
	}

}
