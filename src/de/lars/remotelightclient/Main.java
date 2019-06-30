package de.lars.remotelightclient;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.lars.remotelightclient.arduino.ComPort;
import de.lars.remotelightclient.gui.RgbGUI;
import de.lars.remotelightclient.gui.SelectionWindow;
import de.lars.remotelightclient.gui.SettingsGUI;
import de.lars.remotelightclient.gui.WS281xGUI;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;
import de.lars.remotelightclient.screencolor.WS281xScreenColorHandler;

public class Main {
	
	public final static String VERSION = "0.1.0.1";
	public final static String WEBSITE = "https://remotelight-software.blogspot.com";
	
	private static Main instance;
	private SettingsGUI settingsGui;
	private RgbGUI rgbGui;
	private WS281xGUI ws281xGui;
	private SelectionWindow selectionWindow;
	

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) { e.printStackTrace(); }
		
		new Main();
		//DataStorage.remove(DataStorage.SETTINGS_CONTROL_MODEKEY); //TODO remove this! only for debug...

	    Runtime.getRuntime().addShutdownHook(new Thread() {
		      public void run() {
		  		if((boolean) DataStorage.getData(DataStorage.SETTINGS_AUTOSHUTDOWN))
					Client.send(new String[] {Identifier.SYS_SHUTDOWN_NOW});
		        close();
		      } 
	    }); 
	}
	
	public Main() {
		instance = this;
		DataStorage.start();
		settingsGui = new SettingsGUI();
		StartUp.startUp();
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public RgbGUI getRgbGUI() {
		return rgbGui;
	}
	
	public WS281xGUI getWS281xGUI() {
		return ws281xGui;
	}
	
	public SelectionWindow getSelectionWindow() {
		return selectionWindow;
	}
	
	public SettingsGUI getSettingsGUI() {
		return settingsGui;
	}
	
	public void openSelectionWindow() {
		if(selectionWindow == null)
			selectionWindow = new SelectionWindow();
		selectionWindow.setVisible(true);
	}
	
	public void openSettingsGui() {
		if(settingsGui == null)
			settingsGui = new SettingsGUI();
		settingsGui.setVisible(true);
	}
	
	public void openRgbGui() {
		if(rgbGui == null)
			rgbGui = new RgbGUI();
		rgbGui.setVisible(true);
	}
	
	public void openWS281xGui() {
		if(ws281xGui == null)
			ws281xGui = new WS281xGUI();
		ws281xGui.setVisible(true);
	}
	
	public void setRGBMode() {
		rgbGui = new RgbGUI();
		rgbGui.setVisible(true);
		DataStorage.store(DataStorage.SETTINGS_CONTROL_MODEKEY, "RGB");
		Client.send(new String[] {Identifier.STNG_MODE_WS28x});
	}
	
	public void setWS281xMode() {
		ws281xGui = new WS281xGUI();
		ws281xGui.setVisible(true);
		DataStorage.store(DataStorage.SETTINGS_CONTROL_MODEKEY, "WS281x");
		Client.send(new String[] {Identifier.STNG_MODE_WS28x, DataStorage.getData(DataStorage.SETTINGS_LED_NUM)+""});
	}
	
	public void setArduinoMode() {
		ws281xGui = new WS281xGUI();
		ws281xGui.setVisible(true);
		DataStorage.store(DataStorage.SETTINGS_CONTROL_MODEKEY, "Arduino");
	}
	
	public static boolean isArduinoMode() {
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
	
	
	public static void close() {
		WS281xScreenColorHandler.stop();
		Client.send(new String[] {Identifier.WS_ANI_STOP});
		
		Client.send(new String[] {Identifier.WS_COLOR_OFF});
		
		DataStorage.save();
		if(ComPort.isOpen())
			ComPort.closePort();
		if(Client.isConnected())
			Client.disconnect();
	}

}
