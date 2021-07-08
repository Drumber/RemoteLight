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

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightcore.settings.types.SettingInt;

public class SettingIntPanel extends SettingPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7949888285481977614L;
	private SettingInt setting;
	private JSpinner spinner;

	/**
	 * Create the panel.
	 */
	public SettingIntPanel(SettingInt setting) {
		super(setting);
		this.setting = setting;
		
		JLabel lblName = new JLabel("name");
		lblName.setText(setting.getName());
		add(lblName);
		
		if(setting.get() > setting.getMax()) {
			setting.setValue(setting.getMax());
		}
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(setting.get().intValue(), setting.getMin(), setting.getMax(), setting.getStepsize()));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "#");
		spinner.setEditor(editor); // set new spinner editor without thousands separation
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				SettingIntPanel.this.onChanged(SettingIntPanel.this);
			}
		});
		add(spinner);
		
		if(setting.getDescription() != null && !setting.getDescription().isEmpty()) {
			JLabel lblHelp = new JLabel("");
			lblHelp.setIcon(Style.getHelpIcon());
			lblHelp.setToolTipText(setting.getDescription());
			add(lblHelp);
		}
	}

	@Override
	public void setValue() {
		setting.setValue((int) spinner.getValue());
	}

	@Override
	public void updateComponents() {
		spinner.setModel(new SpinnerNumberModel(setting.get().intValue(), setting.getMin(), setting.getMax(), setting.getStepsize()));
	}

}
