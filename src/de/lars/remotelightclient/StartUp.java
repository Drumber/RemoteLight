package de.lars.remotelightclient;

import java.awt.Color;

import de.lars.remotelightclient.arduino.Arduino;
import de.lars.remotelightclient.arduino.ComPort;
import de.lars.remotelightclient.arduino.RainbowWheel;
import de.lars.remotelightclient.musicsync.ws281x.LevelBar;
import de.lars.remotelightclient.musicsync.ws281x.Rainbow;
import de.lars.remotelightclient.network.Client;

public class StartUp {

	public StartUp() {
		// set settings
		setSettings();
		// which gui should be shown on start
		if (DataStorage.isStored(DataStorage.SETTINGS_CONTROL_MODEKEY)) {
			String mode = (String) DataStorage.getData(DataStorage.SETTINGS_CONTROL_MODEKEY);
			if (mode.toUpperCase().equals("RGB")) {
				Main.getInstance().setRGBMode();
				
			} else if (mode.toUpperCase().equals("WS281X")) {
				Main.getInstance().setWS281xMode();
				
			} else if(mode.equalsIgnoreCase("ARDUINO")) {
				Main.getInstance().setArduinoMode();
				Arduino.init();
				//auto open comport
				if(DataStorage.isStored(DataStorage.SETTINGS_COMPORT_AUTOOPEN) && (boolean) DataStorage.getData(DataStorage.SETTINGS_COMPORT_AUTOOPEN)) {
					for(int i = 0; i < ComPort.getComPorts().length; i++) {
						if(DataStorage.getData(DataStorage.SETTINGS_COMPORT).equals(ComPort.getComPorts()[i].getSystemPortName())) {
							ComPort.openPort(ComPort.getComPorts()[i]);
							if(ComPort.isOpen()) {
								Main.getInstance().getWS281xGUI().setTitle("WS281x | Arduiono >> " + ComPort.getPortName());
								Main.getInstance().getSettingsGUI().setComButtonText("Close");
							}
						}
					}
				}
				
			} else {
				Main.getInstance().openSelectionWindow();
			}
		} else {
			Main.getInstance().openSelectionWindow();
		}

		// auto connect
		if ((boolean) DataStorage.getData(DataStorage.SETTINGS_AUTOCONNECT)) {
			if (DataStorage.isStored(DataStorage.IP_STOREKEY))
				autoConnect();
		}
		
		init();
	}
	
	private void init() {
		RainbowWheel.init();
		Rainbow.init();
	}

	// try 10 times every 5 seconds to connect to client
	private void autoConnect() {
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
					if(Main.getInstance().getWS281xGUI() != null)
						Main.getInstance().getWS281xGUI().performConnectActions();
				}
			}
		}).start();
	}

	private void setSettings() {
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
		
		if(!DataStorage.isStored(DataStorage.CUSTOM_COLORS_ARRAY)) {
			Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.ORANGE, Color.WHITE};
			DataStorage.store(DataStorage.CUSTOM_COLORS_ARRAY, colors);
		}
	}

}
