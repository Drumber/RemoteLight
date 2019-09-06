package de.lars.remotelightclient;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.util.Locale;

import de.lars.remotelightclient.arduino.Arduino;
import de.lars.remotelightclient.arduino.RainbowWheel;
import de.lars.remotelightclient.devices.arduino.ComPort;
import de.lars.remotelightclient.lang.LangUtil;
import de.lars.remotelightclient.lang.i18n;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.out.Output;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.settings.types.SettingSelection.Model;
import de.lars.remotelightclient.ui.panels.colors.CustomColorPanel;
import de.lars.remotelightclient.utils.DirectoryUtil;

public class StartUp {
	
	private SettingsManager s = Main.getInstance().getSettingsManager();

	public StartUp() {
		//delete old logs
		DirectoryUtil.deleteOldLogs(2);
		//register default setting
		registerSettings();
		
		//set language
		Locale.setDefault(new Locale(LangUtil.langNameToCode(((SettingSelection) s.getSettingFromId("ui.language")).getSelected())));
		
		//auto connect feauture
		if(s.getSettingFromType(new SettingBoolean("out.autoconnect", null, null, null, false)).getValue()) {
			Output output = (Output) s.getSettingFromType(new SettingObject("out.lastoutput", null, null)).getValue();
			if(output != null) {
				Main.getInstance().getOutputManager().setActiveOutput(output);
			}
		}
		
		
		if(true)
			return;
		// set settings
		setSettings();
		// which gui should be shown on start
		if (DataStorage.isStored(DataStorage.SETTINGS_CONTROL_MODEKEY)) {
			String mode = (String) DataStorage.getData(DataStorage.SETTINGS_CONTROL_MODEKEY);
			if (mode.toUpperCase().equals("RGB")) {
				//Main.getInstance().setRGBMode();
				
			} else if (mode.toUpperCase().equals("WS281X")) {
				//Main.getInstance().setWS281xMode();
				
			} else if(mode.equalsIgnoreCase("ARDUINO")) {
				//Main.getInstance().setArduinoMode();
				Arduino.init();
				//auto open comport
				if(DataStorage.isStored(DataStorage.SETTINGS_COMPORT_AUTOOPEN) && (boolean) DataStorage.getData(DataStorage.SETTINGS_COMPORT_AUTOOPEN)) {
					for(int i = 0; i < ComPort.getComPorts().length; i++) {
						if(DataStorage.getData(DataStorage.SETTINGS_COMPORT).equals(ComPort.getComPorts()[i].getSystemPortName())) {
							
							de.lars.remotelightclient.devices.arduino.Arduino arduino = new de.lars.remotelightclient.devices.arduino.Arduino("Test", ComPort.getComPorts()[i].getSystemPortName());
							arduino.connect();
							OutputManager outm = Main.getInstance().getOutputManager();
							outm.setActiveOutput(arduino);
							
//							ComPortOld.openPort(ComPortOld.getComPorts()[i]);
//							if(ComPortOld.isOpen()) {
//								Main.getInstance().getWS281xGUI().setTitle("WS281x | Arduiono >> " + ComPortOld.getPortName());
//								Main.getInstance().getSettingsGUI().setComButtonText("Close");
//							}
						}
					}
				}
				
			} else {
				//Main.getInstance().openSelectionWindow();
			}
		} else {
			//Main.getInstance().openSelectionWindow();
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
		//Rainbow.init();
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
		//LevelBar.initPattern();

		if (!DataStorage.isStored(DataStorage.SETTINGS_BRIGHTNESS)) {
			DataStorage.store(DataStorage.SETTINGS_BRIGHTNESS, 1);
		}
		
		if(!DataStorage.isStored(DataStorage.SETTINGS_SCREENCOLOR_MONITOR)) {
			DataStorage.store(DataStorage.SETTINGS_SCREENCOLOR_MONITOR, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getIDstring());
		}
	}
	
	public void registerSettings() {
		s.addSetting(new SettingSelection("ui.language", "Language", SettingCategory.General, "UI Language", LangUtil.langCodeToName(LangUtil.LANGUAGES), "English", Model.ComboBox));
		s.addSetting(new SettingSelection("ui.style", "Style", SettingCategory.General, "Colors of the UI", new String[] {"Light", "Dark"}, "Dark", Model.ComboBox));
		s.addSetting(new SettingBoolean("out.autoconnect", "Auto connect", SettingCategory.General, "Automaticly connect/open last used output.", false));
		s.addSetting(new SettingObject("out.lastoutput", "Last active Output", null));
		
	}

}
