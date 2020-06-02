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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fazecast.jSerialComm.SerialPort;

import de.lars.remotelightclient.lang.i18n;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.devices.arduino.Arduino;
import de.lars.remotelightcore.devices.arduino.ComPort;
import de.lars.remotelightcore.devices.arduino.RgbOrder;

public class ArduinoSettingsPanel extends DeviceSettingsPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3968905553069494626L;
	private Arduino arduino;
	private JTextField fieldId;
	private JSpinner spinnerPixels;
	private JComboBox<String> comboPorts;
	private JComboBox<RgbOrder> comboOrder;
	private JSpinner spinnerShift;
	private JSpinner spinnerClone;
	private JCheckBox checkboxCloneMirrored;
	private Dimension size;

	/**
	 * Create the panel.
	 */
	public ArduinoSettingsPanel(Arduino arduino, boolean setup) {
		super(arduino, setup);
		this.arduino = arduino;
		size = new Dimension(800, 40);
		setBackground(Style.panelBackground);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel panelId = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelId.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelId.setPreferredSize(size);
		panelId.setMaximumSize(size);
		panelId.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelId.setBackground(Style.panelBackground);
		add(panelId);
		
		JLabel lblNameId = new JLabel(i18n.getString("OutputPanel.NameID")); //$NON-NLS-1$
		lblNameId.setForeground(Style.textColor);
		panelId.add(lblNameId);
		
		fieldId = new JTextField();
		panelId.add(fieldId);
		fieldId.setColumns(10);
		
		JPanel panelPort = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panelPort.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panelPort.setPreferredSize(size);
		panelPort.setMaximumSize(size);
		panelPort.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelPort.setBackground(Style.panelBackground);
		add(panelPort);
		
		JLabel lblComport = new JLabel(i18n.getString("OutputPanel.ComPort")); //$NON-NLS-1$
		lblComport.setForeground(Style.textColor);
		panelPort.add(lblComport);
		
		comboPorts = new JComboBox<String>();
		panelPort.add(comboPorts);
		
		JPanel panelPixels = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panelPixels.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		panelPixels.setPreferredSize(size);
		panelPixels.setMaximumSize(size);
		panelPixels.setBackground(Style.panelBackground);
		panelPixels.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panelPixels);
		
		JLabel lblPixels = new JLabel(i18n.getString("OutputPanel.Pixels")); //$NON-NLS-1$
		lblPixels.setForeground(Style.textColor);
		panelPixels.add(lblPixels);
		
		spinnerPixels = new JSpinner();
		spinnerPixels.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		UiUtils.configureSpinner(spinnerPixels);
		panelPixels.add(spinnerPixels);
		
		JPanel panelOrder = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panelOrder.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		panelOrder.setPreferredSize(size);
		panelOrder.setMaximumSize(size);
		panelOrder.setBackground(Style.panelBackground);
		panelOrder.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panelOrder);
		
		JLabel lblRgbOrder = new JLabel("RGB order:"); //$NON-NLS-1$
		lblRgbOrder.setForeground(Style.textColor);
		panelOrder.add(lblRgbOrder);
		
		comboOrder = new JComboBox<RgbOrder>();
		comboOrder.setModel(new DefaultComboBoxModel<>(RgbOrder.values()));
		panelOrder.add(comboOrder);
		
		JLabel lblOutputPatch = new JLabel("Output patch", SwingConstants.LEFT);
		lblOutputPatch.setFont(Style.getFontRegualar(11));
		lblOutputPatch.setForeground(Style.textColor);
		add(lblOutputPatch);
		
		JPanel panelShift = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) panelShift.getLayout();
		flowLayout_4.setAlignment(FlowLayout.LEFT);
		panelShift.setPreferredSize(new Dimension(800, 40));
		panelShift.setMaximumSize(new Dimension(800, 40));
		panelShift.setBackground(Style.panelBackground);
		panelShift.setAlignmentX(0.0f);
		add(panelShift);
		
		JLabel lblShift = new JLabel("Shift pixels:");
		lblShift.setForeground(Style.textColor);
		panelShift.add(lblShift);
		
		spinnerShift = new JSpinner();
		spinnerShift.setModel(new SpinnerNumberModel(arduino.getOutputPatch().getShift(), -arduino.getPixels(), arduino.getPixels(), 1));
		UiUtils.configureSpinner(spinnerShift);
		spinnerShift.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int max = (int) spinnerPixels.getValue() - 1;
				spinnerShift.setModel(new SpinnerNumberModel((int) spinnerShift.getValue(), -max, max, 1));
			}
		});
		panelShift.add(spinnerShift);
		
		JLabel lblClone = new JLabel("Clone:");
		lblClone.setForeground(Style.textColor);
		panelShift.add(lblClone);
		
		spinnerClone = new JSpinner();
		spinnerClone.setModel(new SpinnerNumberModel(arduino.getOutputPatch().getClone(), 0, arduino.getPixels() / 2, 1));
		UiUtils.configureSpinner(spinnerClone);
		spinnerClone.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				spinnerClone.setModel(new SpinnerNumberModel((Number) spinnerClone.getValue(), 0, arduino.getPixels() / 2, 1));
			}
		});
		panelShift.add(spinnerClone);
		
		checkboxCloneMirrored = new JCheckBox("Mirror");
		checkboxCloneMirrored.setBackground(Style.panelBackground);
		checkboxCloneMirrored.setForeground(Style.textColor);
		checkboxCloneMirrored.setSelected(arduino.getOutputPatch().isCloneMirrored());
		panelShift.add(checkboxCloneMirrored);
		
		setValues();
	}
	
	private void setValues() {
		List<String> ports = new ArrayList<String>();
		for(SerialPort port : ComPort.getComPorts()) {
			ports.add(port.getSystemPortName());
		}
		comboPorts.setModel(new DefaultComboBoxModel<String>(ports.toArray(new String[ports.size()])));
		
		if(arduino.getId() != null) {
			fieldId.setText(arduino.getId());
		}
		if(arduino.getSerialPort() != null) {
			String pname = arduino.getSerialPort();
			if(ports.contains(pname)) {
				comboPorts.setSelectedItem(pname);
			}
		}
		if(arduino.getPixels() <= 0) {
			arduino.setPixels(1);
		}
		spinnerPixels.setValue(arduino.getPixels());
		
		if(arduino.getRgbOrder() == null) {
			arduino.setRgbOrder(RgbOrder.GRB);
		}
		comboOrder.setSelectedItem(arduino.getRgbOrder());
	}

	@Override
	public boolean save() {
		if(fieldId.getText().isEmpty()) {
			return false;
		}
		arduino.setId(fieldId.getText());
		arduino.setSerialPort((String) comboPorts.getSelectedItem());
		arduino.setPixels((int) spinnerPixels.getValue());
		arduino.setRgbOrder((RgbOrder) comboOrder.getSelectedItem());
		arduino.getOutputPatch().setShift((int) spinnerShift.getValue());
		arduino.getOutputPatch().setClone((int) spinnerClone.getValue());
		arduino.getOutputPatch().setCloneMirrored(checkboxCloneMirrored.isSelected());
		return true;
	}

	@Override
	public String getId() {
		return fieldId.getText();
	}

}
