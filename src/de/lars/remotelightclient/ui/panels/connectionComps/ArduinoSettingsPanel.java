package de.lars.remotelightclient.ui.panels.connectionComps;

import de.lars.remotelightclient.devices.arduino.Arduino;
import de.lars.remotelightclient.devices.arduino.ComPort;
import de.lars.remotelightclient.ui.Style;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class ArduinoSettingsPanel extends DeviceSettingsPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3968905553069494626L;
	private Arduino arduino;
	private JTextField fieldId;
	private JComboBox<String> comboPorts;
	private Dimension size;

	/**
	 * Create the panel.
	 */
	public ArduinoSettingsPanel(Arduino arduino) {
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
		
		JLabel lblNameId = new JLabel("Name / ID:");
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
		
		JLabel lblComport = new JLabel("ComPort:");
		lblComport.setForeground(Style.textColor);
		panelPort.add(lblComport);
		
		comboPorts = new JComboBox<String>();
		panelPort.add(comboPorts);
		
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
			String pname = arduino.getSerialPort().getSystemPortName();
			if(ports.contains(pname)) {
				comboPorts.setSelectedItem(pname);
			}
		}
	}

	@Override
	public void save() {
		arduino.setId(fieldId.getText());
		arduino.setSerialPort(ComPort.getComPortByName((String) comboPorts.getSelectedItem()));
	}

}
