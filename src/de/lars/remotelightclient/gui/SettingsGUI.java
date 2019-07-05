package de.lars.remotelightclient.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;

import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.arduino.ComPort;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.Color;

public class SettingsGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7702854412414882868L;
	private JPanel contentPane;
	private JTextField fieldServerIP;
	private JLabel lblComPortStatus;
	private JButton btnComOpen;


	/**
	 * Create the frame.
	 */
	public SettingsGUI() {
		setFocusable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(SettingsGUI.class.getResource("/resourcen/Icon-128x128.png")));
		setResizable(false);
		standardSettings();
		setTitle("RemoteLight " + Main.VERSION + " | Settings");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setBounds(100, 100, 450, 410);
		contentPane = new JPanel();
		contentPane.setFocusable(false);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setFocusable(false);
		panel.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_1 = new JPanel();
		panel_1.setFocusable(false);
		panel_1.setBorder(new TitledBorder(null, "WS281x Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JButton btnOk = new JButton("OK");
		btnOk.setFocusable(false);
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(fieldServerIP.getText() != "")
					DataStorage.store(DataStorage.IP_STOREKEY, fieldServerIP.getText());
				DataStorage.save();
				dispose();
			}
		});
		btnOk.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		
		JPanel panel_2 = new JPanel();
		panel_2.setFocusable(false);
		panel_2.setBorder(new TitledBorder(null, "Arduino Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JLabel lblRemotelightByLars = new JLabel("RemoteLight by Lars O.");
		lblRemotelightByLars.setFont(new Font("Source Sans Pro Light", Font.PLAIN, 10));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblRemotelightByLars)
							.addPreferredGap(ComponentPlacement.RELATED, 318, Short.MAX_VALUE)
							.addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
					.addGap(7)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOk)
						.addComponent(lblRemotelightByLars)))
		);
		
		JLabel lblComPort = new JLabel("COM Port:");
		lblComPort.setFocusable(false);
		lblComPort.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		
		JComboBox<String> boxComPort = new JComboBox<String>();
		boxComPort.setFocusable(false);
		boxComPort.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				DataStorage.store(DataStorage.SETTINGS_COMPORT, boxComPort.getSelectedItem());
			}
		});
		boxComPort.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		String lastPort = "";
		if(DataStorage.isStored(DataStorage.SETTINGS_COMPORT))
			lastPort = (String) DataStorage.getData(DataStorage.SETTINGS_COMPORT);
		for(int i = 0; i < ComPort.getComPorts().length; i++) {
			boxComPort.addItem(ComPort.getComPorts()[i].getSystemPortName());
			if(lastPort.equals(ComPort.getComPorts()[i].getSystemPortName()))
				boxComPort.setSelectedIndex(i);
		}
		
		
		btnComOpen = new JButton("Open");
		btnComOpen.setFocusable(false);
		btnComOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!ComPort.isOpen()) {
					if(boxComPort.getItemCount() != 0) {
						for(int i = 0; i < ComPort.getComPorts().length; i++) {
							if(boxComPort.getSelectedItem().equals(ComPort.getComPorts()[i].getSystemPortName())) {
								ComPort.openPort(ComPort.getComPorts()[i]);
								if(ComPort.isOpen()) {
									btnComOpen.setText("Close");
									Main.getInstance().getWS281xGUI().setTitle("WS281x | Arduiono >> " + ComPort.getPortName());
								}
							}
						}
					}
				} else {
					ComPort.closePort();
					btnComOpen.setText("Open");
					Main.getInstance().getWS281xGUI().setTitle("WS281x Control");
				}
			}
		});
		btnComOpen.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		if(ComPort.isOpen())
			btnComOpen.setText("Close");
		
		lblComPortStatus = new JLabel("");
		lblComPortStatus.setFocusable(false);
		lblComPortStatus.setForeground(Color.RED);
		lblComPortStatus.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		
		JCheckBox chckbxAutoOpen = new JCheckBox("Auto open");
		chckbxAutoOpen.setFocusable(false);
		chckbxAutoOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DataStorage.store(DataStorage.SETTINGS_COMPORT_AUTOOPEN, chckbxAutoOpen.isSelected());
			}
		});
		chckbxAutoOpen.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		if(DataStorage.isStored(DataStorage.SETTINGS_COMPORT_AUTOOPEN))
			chckbxAutoOpen.setSelected((boolean) DataStorage.getData(DataStorage.SETTINGS_COMPORT_AUTOOPEN));
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(lblComPort)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(boxComPort, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnComOpen)
							.addContainerGap(159, Short.MAX_VALUE))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(lblComPortStatus, GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
							.addGap(159))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(chckbxAutoOpen)
							.addContainerGap(309, Short.MAX_VALUE))))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblComPort)
						.addComponent(boxComPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnComOpen))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblComPortStatus)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chckbxAutoOpen)
					.addContainerGap(10, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		
		JLabel lblLedNumber = new JLabel("LED number:");
		lblLedNumber.setFocusable(false);
		lblLedNumber.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		
		JSpinner spinnerLEDnum = new JSpinner();
		spinnerLEDnum.setFocusable(false);
		spinnerLEDnum.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		spinnerLEDnum.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				DataStorage.store(DataStorage.SETTINGS_LED_NUM, spinnerLEDnum.getValue());
			}
		});
		spinnerLEDnum.setModel(new SpinnerNumberModel(new Integer(60), new Integer(1), null, new Integer(1)));
		if(DataStorage.isStored(DataStorage.SETTINGS_LED_NUM)) {
			spinnerLEDnum.setValue(DataStorage.getData(DataStorage.SETTINGS_LED_NUM));
		}
		
		JCheckBox chckbxShowLast = new JCheckBox("Show last");
		chckbxShowLast.setVisible(false);
		chckbxShowLast.setFocusable(false);
		chckbxShowLast.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				DataStorage.store(DataStorage.SETTINGS_BOOT_SHOWLAST, chckbxShowLast.isSelected());
			}
		});
		if(DataStorage.isStored(DataStorage.SETTINGS_BOOT_SHOWLAST))
			chckbxShowLast.setSelected((boolean) DataStorage.getData(DataStorage.SETTINGS_BOOT_SHOWLAST));
		chckbxShowLast.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		chckbxShowLast.setToolTipText("Shows the last color/animation");
		
		JCheckBox chckbxBootAnimation = new JCheckBox("Boot Animation");
		chckbxBootAnimation.setVisible(false);
		chckbxBootAnimation.setFocusable(false);
		chckbxBootAnimation.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				DataStorage.store(DataStorage.SETTINGS_BOOT_ANI, chckbxBootAnimation.isSelected());
				Client.send(new String[] {Identifier.STNG_BOOT_ANI, String.valueOf(DataStorage.getData(DataStorage.SETTINGS_BOOT_ANI))});
			}
		});
		if(DataStorage.isStored(DataStorage.SETTINGS_BOOT_ANI))											//HIER IST DER FEHLER!!!!
			chckbxBootAnimation.setSelected((boolean) DataStorage.getData(DataStorage.SETTINGS_BOOT_ANI)); //wenn true, dann ‰ndert sich Modekey zu Arduino... H‰‰‰????
		chckbxBootAnimation.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxBootAnimation)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblLedNumber)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(spinnerLEDnum, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE))
						.addComponent(chckbxShowLast))
					.addContainerGap(281, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblLedNumber)
						.addComponent(spinnerLEDnum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxShowLast)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(chckbxBootAnimation)
					.addContainerGap())
		);
		panel_1.setLayout(gl_panel_1);
		
		JLabel lblMode = new JLabel("Mode:");
		lblMode.setFocusable(false);
		lblMode.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		
		JComboBox<String> boxMode = new JComboBox<String>();
		boxMode.setFocusable(false);
		boxMode.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		boxMode.setToolTipText("Select the device you want to control");
		boxMode.setModel(new DefaultComboBoxModel<String>(new String[] {"RGB LED/Strip (RPi)", "WS281x Strip (RPi)", "WS281x Strip (Arduino)"}));
		if(DataStorage.isStored(DataStorage.SETTINGS_CONTROL_MODEKEY)) {
			String mode = (String) DataStorage.getData(DataStorage.SETTINGS_CONTROL_MODEKEY);
			if(mode.equalsIgnoreCase("RGB")) {
				boxMode.setSelectedIndex(0);
			} else if(mode.equalsIgnoreCase("WS281X")) {
				boxMode.setSelectedIndex(1);
			} else if(mode.equalsIgnoreCase("ARDUINO")) {
				boxMode.setSelectedIndex(2);
			}
		}
		boxMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(boxMode.getSelectedIndex() == 0) {
					DataStorage.store(DataStorage.SETTINGS_CONTROL_MODEKEY, "RGB");
				} else if(boxMode.getSelectedIndex() == 1) {
					DataStorage.store(DataStorage.SETTINGS_CONTROL_MODEKEY, "WS281x");
				} else if(boxMode.getSelectedIndex() == 2) {
					DataStorage.store(DataStorage.SETTINGS_CONTROL_MODEKEY, "Arduino");
				}
			}
		});
		
		JCheckBox chkAutostart = new JCheckBox("Autostart");
		chkAutostart.setVisible(false);
		chkAutostart.setFocusable(false);
		chkAutostart.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		chkAutostart.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(chkAutostart.isSelected())
					DataStorage.store(DataStorage.SETTINGS_AUTOSTART, true);
				else 
					DataStorage.store(DataStorage.SETTINGS_AUTOSTART, false);
			}
		});
		if((boolean) (DataStorage.getData(DataStorage.SETTINGS_AUTOSTART)) == false)
			chkAutostart.setSelected(false);
		else
			chkAutostart.setSelected(true);
		chkAutostart.setToolTipText("Should the application start on boot?");
		
		JCheckBox chkHide = new JCheckBox("Hide in System tray");
		chkHide.setFocusable(false);
		chkHide.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		chkHide.setToolTipText("Icon will be shown in the system tray. Right click on it to close.");
		chkHide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(chkHide.isSelected())
					DataStorage.store(DataStorage.SETTINGS_HIDE, true);
				else 
					DataStorage.store(DataStorage.SETTINGS_HIDE, false);
			}
		});
		if((boolean) (DataStorage.getData(DataStorage.SETTINGS_HIDE)) == false)
			chkHide.setSelected(false);
		else
			chkHide.setSelected(true);
		
		JLabel lblRaspberrypiIp = new JLabel("RaspberryPi IP:");
		lblRaspberrypiIp.setFocusable(false);
		lblRaspberrypiIp.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		
		fieldServerIP = new JTextField();
		fieldServerIP.setFocusable(false);
		fieldServerIP.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		fieldServerIP.setToolTipText("IP / Hostname of the RaspberryPi");
		fieldServerIP.setColumns(10);
		fieldServerIP.setText((String) DataStorage.getData(DataStorage.IP_STOREKEY));
		
		JCheckBox chkAutoShutdownRaspberrypi = new JCheckBox("Auto Shutdown RaspberryPi");
		chkAutoShutdownRaspberrypi.setVisible(false);
		chkAutoShutdownRaspberrypi.setFocusable(false);
		chkAutoShutdownRaspberrypi.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		chkAutoShutdownRaspberrypi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chkAutoShutdownRaspberrypi.isSelected()) {
					DataStorage.store(DataStorage.SETTINGS_AUTOSHUTDOWN, true);
					//Client.send(new String[] {Identifier.SYS_SHUTDOWN_IFNOT_REACHABLE});
				} else {
					DataStorage.store(DataStorage.SETTINGS_AUTOSHUTDOWN, false);
					//Client.send(new String[] {Identifier.SYS_SHUTDOWN_CANCEL});
				}
			}
		});
		chkAutoShutdownRaspberrypi.setToolTipText("Shutdown RaspberryPi when application is closed");
		if((boolean) (DataStorage.getData(DataStorage.SETTINGS_AUTOSHUTDOWN)) == false)
			chkAutoShutdownRaspberrypi.setSelected(false);
		else
			chkAutoShutdownRaspberrypi.setSelected(true);
		
		JCheckBox chkAutoConnect = new JCheckBox("Auto connect");
		chkAutoConnect.setFocusable(false);
		chkAutoConnect.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		chkAutoConnect.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(chkAutoConnect.isSelected())
					DataStorage.store(DataStorage.SETTINGS_AUTOCONNECT, true);
				else 
					DataStorage.store(DataStorage.SETTINGS_AUTOCONNECT, false);
			}
		});
		chkAutoConnect.setToolTipText("Auto connect to RaspberryPi at startup");
		if((boolean) (DataStorage.getData(DataStorage.SETTINGS_AUTOCONNECT)) == false)
			chkAutoConnect.setSelected(false);
		else
			chkAutoConnect.setSelected(true);
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblMode)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(boxMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lblRaspberrypiIp)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(fieldServerIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(chkHide)
								.addComponent(chkAutostart))
							.addGap(18)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(chkAutoShutdownRaspberrypi)
								.addComponent(chkAutoConnect))))
					.addContainerGap(48, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMode)
						.addComponent(boxMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblRaspberrypiIp)
						.addComponent(fieldServerIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(chkAutostart)
						.addComponent(chkAutoConnect))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(chkHide)
						.addComponent(chkAutoShutdownRaspberrypi))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);
	}
	
	public static void standardSettings() {
		HashMap<String, Object> data = DataStorage.getstorage().getAll();
		if(!data.containsKey(DataStorage.SETTINGS_AUTOCONNECT))
				DataStorage.store(DataStorage.SETTINGS_AUTOCONNECT, false);
		if(!data.containsKey(DataStorage.SETTINGS_AUTOSHUTDOWN))
			DataStorage.store(DataStorage.SETTINGS_AUTOSHUTDOWN, false);
		if(!data.containsKey(DataStorage.SETTINGS_AUTOSTART))
			DataStorage.store(DataStorage.SETTINGS_AUTOSTART, true);
		if(!data.containsKey(DataStorage.SETTINGS_HIDE))
			DataStorage.store(DataStorage.SETTINGS_HIDE, false);
		if(!data.containsKey(DataStorage.SETTINGS_LED_NUM))
			DataStorage.store(DataStorage.SETTINGS_LED_NUM, 60);
		
	}
	
	public void setComPortStatusLabel(String text) {
		lblComPortStatus.setText(text);
	}
	
	public void setComButtonText(String text) {
		btnComOpen.setText(text);
	}
}
