package de.lars.remotelightclient;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.lars.remotelightclient.arduino.ComPort;
import de.lars.remotelightclient.gui.RgbGUI;
import de.lars.remotelightclient.gui.SelectionWindow;
import de.lars.remotelightclient.gui.SettingsGUI;
import de.lars.remotelightclient.gui.WS281xGUI;
import de.lars.remotelightclient.musicsync.MusicSync;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;
import de.lars.remotelightclient.screencolor.WS281xScreenColorHandler;

public class Main {
	
	public final static String VERSION = "0.0.4";
	
	private static SettingsGUI settingsGui;
	private static RgbGUI rgbGui;
	private static WS281xGUI ws281xGui;
	private static SelectionWindow selectionWindow;
	

	public static void main(String[] args) {
		DataStorage.start();
		//DataStorage.remove(DataStorage.SETTINGS_CONTROL_MODEKEY); //TODO remove this! only for debug...
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		settingsGui = new SettingsGUI();
		rgbGui = new RgbGUI();
		ws281xGui = new WS281xGUI();
		selectionWindow = new SelectionWindow();
		new MusicSync();
		
		StartUp.startUp();

	    Runtime.getRuntime().addShutdownHook(new Thread() {
		      public void run() {
		  		if((boolean) DataStorage.getData(DataStorage.SETTINGS_AUTOSHUTDOWN))
					Client.send(new String[] {Identifier.SYS_SHUTDOWN_NOW});
		        close();
		      } 
	    }); 
	}
	
	public static RgbGUI getRgbGUI() {
		return rgbGui;
	}
	
	public static WS281xGUI getWS281xGUI() {
		return ws281xGui;
	}
	
	public static SelectionWindow getSelectionWindow() {
		return selectionWindow;
	}
	
	public static SettingsGUI getSettingsGUI() {
		return settingsGui;
	}
	
	public static void openSettingsGui() {
		settingsGui.setVisible(true);
	}
	
	public static void openWS281xGui() {
		rgbGui.dispose();
		ws281xGui.setVisible(true);
	}
	
	public static void close() {
		MusicSync.stopLoop();
		WS281xScreenColorHandler.stop();
		Client.send(new String[] {Identifier.WS_ANI_STOP});
		
		Client.send(new String[] {Identifier.WS_COLOR_OFF});
		
		DataStorage.save();
		ComPort.closePort();
		Client.disconnect();
	}
	
	public static void setRGBMode() {
		selectionWindow.dispose();
		ws281xGui.dispose();
		rgbGui.setVisible(true);
		DataStorage.store(DataStorage.SETTINGS_CONTROL_MODEKEY, "RGB");
		Client.send(new String[] {Identifier.STNG_MODE_WS28x});
	}
	
	public static void setWS281xMode() {
		selectionWindow.dispose();
		rgbGui.dispose();
		ws281xGui.setVisible(true);
		DataStorage.store(DataStorage.SETTINGS_CONTROL_MODEKEY, "WS281x");
		Client.send(new String[] {Identifier.STNG_MODE_WS28x, DataStorage.getData(DataStorage.SETTINGS_LED_NUM)+""});
	}
	
	public static boolean isArduinoMode() {
		DataStorage.store(DataStorage.SETTINGS_CONTROL_MODEKEY, "Arduino");
		if(DataStorage.isStored(DataStorage.SETTINGS_CONTROL_MODEKEY)) {
			String mode = (String) DataStorage.getData(DataStorage.SETTINGS_CONTROL_MODEKEY);
			if(mode.equalsIgnoreCase("Arduino"))
				return true;
		}
		return false;
	}
	
	public static int getLedNum() {
		return (int) DataStorage.getData(DataStorage.SETTINGS_LED_NUM);
	}
	

}
