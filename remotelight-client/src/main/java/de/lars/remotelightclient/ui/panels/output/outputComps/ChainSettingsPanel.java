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

package de.lars.remotelightclient.ui.panels.output.outputComps;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.devices.link.chain.Chain;
import de.lars.remotelightcore.lang.i18n;

public class ChainSettingsPanel extends DeviceSettingsPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2517187347534151946L;
	private Chain chain;
	private JTextField fieldId;
	private JPanel panelDevices;
	private JPanel panelAdd;

	public ChainSettingsPanel(Chain chain, boolean setup) {
		super(chain, setup);
		this.chain = chain;
		setBackground(Style.panelBackground);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel panelId = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelId.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelId.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelId.setBackground(Style.panelBackground);
		add(panelId);
		
		JLabel lblNameId = new JLabel(i18n.getString("OutputPanel.NameID")); //$NON-NLS-1$
		lblNameId.setForeground(Style.textColor);
		panelId.add(lblNameId);
		
		fieldId = new JTextField();
		panelId.add(fieldId);
		fieldId.setColumns(10);
		if(chain.getId() != null) {
			fieldId.setText(chain.getId());
		}
		
		panelDevices = new JPanel();
		panelDevices.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelDevices.setLayout(new WrapLayout(FlowLayout.LEFT));
		panelDevices.setBackground(Style.panelBackground);
		addDevicesToPanel();
		add(panelDevices);
		
		panelAdd = new JPanel();
		panelAdd.setLayout(new WrapLayout(FlowLayout.LEFT));
		panelAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelAdd.setBackground(Style.panelBackground);
		addControlsToPanel();
		add(panelAdd);
	}
	
	private void addDevicesToPanel() {
		panelDevices.removeAll();
		
		for(Device d : chain.getDevices()) {
			JPanel dPanel = new JPanel();
			dPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 1));
			dPanel.setBackground(Style.buttonBackground);
			
			JLabel name = new JLabel(d.getId());
			name.setForeground(Style.textColor);
			dPanel.add(name);
			
			JButton btnRemove = new JButton("X");
			btnRemove.setForeground(Color.RED);
			UiUtils.configureButton(btnRemove);
			btnRemove.addActionListener(e -> {
				chain.removeDevice(d);
				addDevicesToPanel();
			});
			dPanel.add(btnRemove);
			dPanel.setPreferredSize(new Dimension((int) dPanel.getPreferredSize().getWidth(), 27));
			panelDevices.add(dPanel);
		}
		updateUI();
	}
	
	private void addControlsToPanel() {
		JComboBox<String> boxOutputs = new JComboBox<>();
		for(Device d : RemoteLightCore.getInstance().getDeviceManager().getDevices()) {
			if(!(d instanceof Chain) && !chain.getDevices().contains(d)) {
				boxOutputs.addItem(d.getId());
			}
		}
		panelAdd.add(boxOutputs);
		
		JButton btnAdd = new JButton(i18n.getString("Basic.Add"));
		UiUtils.configureButton(btnAdd);
		btnAdd.addActionListener(e -> {
			if(boxOutputs.getSelectedItem() != null) {
				
				String selOutput = (String) boxOutputs.getSelectedItem();
				Device device = RemoteLightCore.getInstance().getDeviceManager().getDevice(selOutput);
				if(device != null) {
					chain.addDevices(device);
					boxOutputs.removeItem(selOutput);
					addDevicesToPanel();
				}
			}
		});
		panelAdd.add(btnAdd);
	}
	

	@Override
	public boolean save() {
		if(fieldId.getText().isEmpty()) {
			return false;
		}
		chain.setId(fieldId.getText());
		return true;
	}

	@Override
	public String getId() {
		return fieldId.getText();
	}

}
