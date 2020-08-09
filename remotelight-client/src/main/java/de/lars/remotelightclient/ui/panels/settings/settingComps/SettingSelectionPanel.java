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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;

public class SettingSelectionPanel extends SettingPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7949888285481977614L;
	private SettingSelection setting;
	private JComboBox<String> boxValues;
	private ButtonGroup group;

	/**
	 * Create the panel.
	 */
	public SettingSelectionPanel(SettingSelection setting) {
		super(setting);
		this.setting = setting;
		setBackground(Style.panelBackground);
		
		JLabel lblName = new JLabel("name");
		lblName.setText(setting.getName());
		lblName.setForeground(Style.textColor);
		add(lblName);
		
		if(setting.getModel() == Model.ComboBox) {
			boxValues = new JComboBox<String>();
			if(setting.getValues().length > 15)
				boxValues.setMaximumRowCount(15);
			boxValues.setModel(new DefaultComboBoxModel<String>(setting.getValues()));
			if(setting.getSelected() != null && Arrays.asList(setting.getValues()).contains(setting.getSelected())) {
				boxValues.setSelectedItem(setting.getSelected());
			}
			add(boxValues);
			
			boxValues.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SettingSelectionPanel.this.onChanged(SettingSelectionPanel.this);
				}
			});
			
		} else if(setting.getModel() == Model.RadioButton) {
			group = new ButtonGroup();
			for(String s : setting.getValues()) {
				
				JRadioButton btn = new JRadioButton(s);
				btn.setForeground(Style.textColor);
				btn.setActionCommand(s);
				if(setting.getSelected() != null && s.equals(setting.getSelected())) {
					btn.setSelected(true);
				}
				group.add(btn);
				add(btn);
				
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						SettingSelectionPanel.this.onChanged(SettingSelectionPanel.this);
					}
				});
			}
		}
		
		if(setting.getDescription() != null && !setting.getDescription().isEmpty()) {
			JLabel lblHelp = new JLabel("");
			lblHelp.setIcon(Style.getHelpIcon());
			lblHelp.setToolTipText(setting.getDescription());
			add(lblHelp);
		}
	}

	@Override
	public void setValue() {
		if(setting.getModel() == Model.ComboBox) {
			setting.setSelected((String) boxValues.getSelectedItem());
		} else if(setting.getModel() == Model.RadioButton) {
			setting.setSelected(group.getSelection().getActionCommand());
		}
	}

	@Override
	public void updateComponents() {
		if(setting.getModel() == Model.ComboBox) {
			boxValues.setSelectedItem(setting.getSelected());
		} else if(setting.getModel() == Model.RadioButton) {
			setting.setSelected(group.getSelection().getActionCommand());
			Enumeration<AbstractButton> elements = group.getElements();
			while(elements.hasMoreElements()) {
				AbstractButton button = elements.nextElement();
				if(button.getActionCommand().equals(setting.getSelected())) {
					button.setSelected(true);
				}
			}
		}
	}

}
