package de.lars.remotelightclient;

import java.awt.Color;

import de.lars.remotelightclient.arduino.Arduino;
import de.lars.remotelightclient.arduino.ComPort;
import de.lars.remotelightclient.musicsync.ws281x.LevelBar;
import de.lars.remotelightclient.network.Client;

public class StartUp {

	public static void startUp() {
		// which gui should be shown on start
		if (DataStorage.isStored(DataStorage.SETTINGS_CONTROL_MODEKEY)) {
			String mode = (String) DataStorage.getData(DataStorage.SETTINGS_CONTROL_MODEKEY);
			if (mode.toUpperCase().equals("RGB")) {
				Main.getRgbGUI().setVisible(true);
				
			} else if (mode.toUpperCase().equals("WS281X")) {
				Main.getWS281xGUI().setVisible(true);
				
			} else if(mode.equalsIgnoreCase("ARDUINO")) {
				Main.getWS281xGUI().setVisible(true);
				Arduino.init();
				//auto open comport
				if(DataStorage.isStored(DataStorage.SETTINGS_COMPORT_AUTOOPEN) && (boolean) DataStorage.getData(DataStorage.SETTINGS_COMPORT_AUTOOPEN)) {
					for(int i = 0; i < ComPort.getComPorts().length; i++) {
						if(DataStorage.getData(DataStorage.SETTINGS_COMPORT).equals(ComPort.getComPorts()[i].getSystemPortName())) {
							ComPort.openPort(ComPort.getComPorts()[i]);
							if(ComPort.isOpen())
								Main.getWS281xGUI().setTitle("WS281x | Arduiono >> " + ComPort.getPortName());
						}
					}
				}
				
			} else {
				Main.getSelectionWindow().setVisible(true);
			}
		} else {
			Main.getSelectionWindow().setVisible(true);
		}

		// auto connect
		if ((boolean) DataStorage.getData(DataStorage.SETTINGS_AUTOCONNECT)) {
			if (DataStorage.isStored(DataStorage.IP_STOREKEY))
				autoConnect();
		}

		// set settings
		setSettings();
	}

	// try 10 times every 5 seconds to connect to client
	private static void autoConnect() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				int count = 0;
				while (!Client.isConnected()) {
					if (count <= 10) { // 10 tries
						Client.connect((String) DataStorage.getData(DataStorage.IP_STOREKEY));
						count++;
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("[AutoConnect] Could not connect!");
						return;
					}
				}
				if(Client.isConnected()) {
					Main.getWS281xGUI().performConnectActions();
				}
			}
		}).start();
	}

	private static void setSettings() {
		if (!DataStorage.isStored(DataStorage.LEVELBAR_COLOR1)) {
			DataStorage.store(DataStorage.LEVELBAR_COLOR1, Color.RED);
			DataStorage.store(DataStorage.LEVELBAR_COLOR2, Color.RED);
			DataStorage.store(DataStorage.LEVELBAR_COLOR3, Color.RED);
		}
		if (!DataStorage.isStored(DataStorage.LEVELBAR_AUTOCHANGE)) {
			DataStorage.store(DataStorage.LEVELBAR_AUTOCHANGE, true);
			DataStorage.store(DataStorage.LEVELBAR_SMOOTH, true);
		}
		LevelBar.initPattern();

		if (!DataStorage.isStored(DataStorage.SETTINGS_BRIGHTNESS)) {
			DataStorage.store(DataStorage.SETTINGS_BRIGHTNESS, 1);
		}
	}

}
