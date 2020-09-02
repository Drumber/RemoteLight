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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.devices.link.chain.Chain;
import de.lars.remotelightcore.devices.link.multi.DividingMethod;
import de.lars.remotelightcore.devices.link.multi.MultiOutput;
import de.lars.remotelightcore.lang.i18n;

public class MultiOutputSettingsPanel extends DeviceSettingsPanel {
	private static final long serialVersionUID = 2517187347534151946L;
	
	private MultiOutput multi;
	private JTextField fieldId;
	private JPanel panelDevices;
	private JPanel panelAdd;
	private JComboBox<String> boxOutputs;
	private JComboBox<DividingMethod> comboProcessing;
	private JLabel lblPixels;

	public MultiOutputSettingsPanel(MultiOutput multi, boolean setup) {
		super(multi, setup);
		this.multi = multi;
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
		if(multi.getId() != null) {
			fieldId.setText(multi.getId());
		}
		
		lblPixels = new JLabel("Pixels: " + multi.getPixels());
		lblPixels.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblPixels.setForeground(Style.textColor);
		lblPixels.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
		add(lblPixels);
		
		JPanel panelProcessing = new JPanel();
		FlowLayout flowLayout2 = (FlowLayout) panelProcessing.getLayout();
		flowLayout2.setAlignment(FlowLayout.LEFT);
		panelProcessing.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelProcessing.setBackground(Style.panelBackground);
		add(panelProcessing);
		
		JLabel lblProcessing = new JLabel("Dividing method: ");
		lblProcessing.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblProcessing.setForeground(Style.textColor);
		panelProcessing.add(lblProcessing);
		
		comboProcessing = new JComboBox<DividingMethod>();
		comboProcessing.setModel(new DefaultComboBoxModel<DividingMethod>(DividingMethod.values()));
		comboProcessing.setSelectedItem(multi.getProcessingMethod());
		panelProcessing.add(comboProcessing);
		
		JLabel lblDescription = new JLabel(multi.getProcessingMethod().getDescription());
		lblDescription.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblDescription.setForeground(Style.textColor);
		lblDescription.setBackground(Style.panelBackground);
		lblDescription.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
		add(lblDescription);
		comboProcessing.addActionListener(e -> {
			multi.setProcessingMethod((DividingMethod) comboProcessing.getSelectedItem());
			// update description text
			lblDescription.setText(multi.getProcessingMethod().getDescription());
			lblDescription.updateUI();
			// update pixel count
			updatePixelLabel();
		});
		
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
	
	private void updatePixelLabel() {
		lblPixels.setText("Pixels: " + multi.getPixels());
	}
	
	private void addDevicesToPanel() {
		panelDevices.removeAll();
		
		for(Device d : multi.getDevices()) {
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
				multi.removeDevice(d);
				addDevicesToPanel();
				updateComboDevices();
			});
			dPanel.add(btnRemove);
			dPanel.setPreferredSize(new Dimension((int) dPanel.getPreferredSize().getWidth(), 27));
			panelDevices.add(dPanel);
		}
		updatePixelLabel();
		updateUI();
	}
	
	private void addControlsToPanel() {
		boxOutputs = new JComboBox<>();
		updateComboDevices();
		panelAdd.add(boxOutputs);
		
		JButton btnAdd = new JButton(i18n.getString("Basic.Add"));
		UiUtils.configureButton(btnAdd);
		btnAdd.addActionListener(e -> {
			if(boxOutputs.getSelectedItem() != null) {
				
				String selOutput = (String) boxOutputs.getSelectedItem();
				Device device = RemoteLightCore.getInstance().getDeviceManager().getDevice(selOutput);
				if(device != null) {
					multi.addDevices(device);
					boxOutputs.removeItem(selOutput);
					addDevicesToPanel();
				}
			}
		});
		panelAdd.add(btnAdd);
	}
	
	private void updateComboDevices() {
		boxOutputs.removeAllItems();
		for(Device d : RemoteLightCore.getInstance().getDeviceManager().getDevices()) {
			if(!(d instanceof Chain) && !multi.getDevices().contains(d)) {
				boxOutputs.addItem(d.getId());
			}
		}
	}
	

	@Override
	public boolean save() {
		if(fieldId.getText().isEmpty()) {
			return false;
		}
		multi.setId(fieldId.getText());
		multi.setProcessingMethod((DividingMethod) comboProcessing.getSelectedItem());
		return true;
	}

	@Override
	public String getId() {
		return fieldId.getText();
	}

}
