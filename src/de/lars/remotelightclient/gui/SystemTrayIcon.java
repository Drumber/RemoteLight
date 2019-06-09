package de.lars.remotelightclient.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;

import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.StartUp;

public class SystemTrayIcon {
	
	private static SystemTray tray;
	private static TrayIcon trayIcon;
	
	//Michael Myers; https://stackoverflow.com/questions/758083/how-do-i-put-a-java-app-in-the-system-tray
	public static void showTrayIcon() {
		if (SystemTray.isSupported()) {
		    tray = SystemTray.getSystemTray();
		    Image image = new ImageIcon(StartUp.class.getResource("/resourcen/led-icon-Kiranshastry.png")).getImage();
		    

		    PopupMenu popup = new PopupMenu();
		    MenuItem show = new MenuItem("Show Gui");
		    show.addActionListener(listenerShow);
		    popup.add(show);
		    MenuItem exit = new MenuItem("Exit");
		    exit.addActionListener(listenerExit);
		    popup.add(exit);

		    trayIcon = new TrayIcon(image, "RemoteLightClient", popup);
		    trayIcon.addActionListener(listenerShow);
		    trayIcon.addMouseListener(mouseListener);
		    try {
		    	tray.remove(trayIcon);
		    	tray.add(trayIcon);
		    	trayIcon.displayMessage("RemoteLight", "I am here!", MessageType.NONE);
		    } catch (AWTException e) {
		        System.err.println(e);
		    }
		} else {
			System.out.println("ERROR: TrayIcon not supported!");
		}
	}
	
	private static ActionListener listenerShow = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			tray.remove(trayIcon);
			if(DataStorage.isStored(DataStorage.SETTINGS_CONTROL_MODEKEY)) {
				String mode = (String) DataStorage.getData(DataStorage.SETTINGS_CONTROL_MODEKEY);
				if(mode.toUpperCase().equals("RGB")) {
					Main.getRgbGUI().setVisible(true);
				} else if(mode.toUpperCase().equals("WS281X") || mode.toUpperCase().equals("ARDUINO")) {
					Main.getWS281xGUI().setVisible(true);
				} else {
					Main.getSelectionWindow().setVisible(true);
				}
			} else {
				Main.getSelectionWindow().setVisible(true);
			}
		}
    };
    private static ActionListener listenerExit = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			tray.remove(trayIcon);
			System.exit(0);
		}
    };
    private static MouseListener mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
            	tray.remove(trayIcon);
				if(DataStorage.isStored(DataStorage.SETTINGS_CONTROL_MODEKEY)) {
					String mode = (String) DataStorage.getData(DataStorage.SETTINGS_CONTROL_MODEKEY);
					if(mode.toUpperCase().equals("RGB")) {
						Main.getRgbGUI().setVisible(true);
					} else if(mode.toUpperCase().equals("WS281X") || mode.toUpperCase().equals("ARDUINO")) {
						Main.getWS281xGUI().setVisible(true);
					} else {
						Main.getSelectionWindow().setVisible(true);
					}
				} else {
					Main.getSelectionWindow().setVisible(true);
				}
            }
        }
	};

}
