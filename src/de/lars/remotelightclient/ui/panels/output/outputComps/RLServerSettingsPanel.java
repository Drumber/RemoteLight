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
package de.lars.remotelightclient.ui.panels.output.outputComps;

import de.lars.remotelightclient.devices.arduino.ComPort;
import de.lars.remotelightclient.devices.remotelightserver.RemoteLightServer;
import de.lars.remotelightclient.lang.i18n;
import de.lars.remotelightclient.ui.Style;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.Component;
import java.awt.Dimension;

public class RLServerSettingsPanel extends DeviceSettingsPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3968905553069494626L;
	private RemoteLightServer rlServer;
	private JTextField fieldId;
	private JSpinner spinnerPixels;
	private Dimension size;
	private JTextField fieldHostname;

	/**
	 * Create the panel.
	 */
	public RLServerSettingsPanel(RemoteLightServer rlServer, boolean setup) {
		super(rlServer, setup);
		this.rlServer = rlServer;
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
		
		JLabel lblHostname = new JLabel(i18n.getString("OutputPanel.HostnameIP")); //$NON-NLS-1$
		lblHostname.setForeground(Style.textColor);
		panelPort.add(lblHostname);
		
		fieldHostname = new JTextField();
		panelPort.add(fieldHostname);
		fieldHostname.setColumns(10);
		
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
		spinnerPixels.setPreferredSize(new Dimension(50, 20));
		spinnerPixels.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		panelPixels.add(spinnerPixels);
		
		setValues();
	}
	
	private void setValues() {
		List<String> ports = new ArrayList<String>();
		for(SerialPort port : ComPort.getComPorts()) {
			ports.add(port.getSystemPortName());
		}
		
		if(rlServer.getId() != null) {
			fieldId.setText(rlServer.getId());
		}
		if(rlServer.getIp() != null) {
			fieldHostname.setText(rlServer.getIp());
		}
		if(rlServer.getPixels() <= 0) {
			rlServer.setPixels(1);
		}
		spinnerPixels.setValue(rlServer.getPixels());
	}

	@Override
	public boolean save() {
		if(fieldId.getText().isEmpty()) {
			return false;
		}
		rlServer.setId(fieldId.getText());
		rlServer.setIp(fieldHostname.getText());
		rlServer.setPixels((int) spinnerPixels.getValue());
		return true;
	}

	@Override
	public String getId() {
		return fieldId.getText();
	}

}
