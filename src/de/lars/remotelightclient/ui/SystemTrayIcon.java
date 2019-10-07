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
package de.lars.remotelightclient.ui;

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

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.StartUp;

public class SystemTrayIcon {

	private static SystemTray tray;
	private static TrayIcon trayIcon;
	
	//Michael Myers; https://stackoverflow.com/questions/758083/how-do-i-put-a-java-app-in-the-system-tray
	public static void showTrayIcon() {
		if (SystemTray.isSupported()) {
		    tray = SystemTray.getSystemTray();
		    Image image = new ImageIcon(StartUp.class.getResource("/resourcen/Icon-16x16.png")).getImage();

		    PopupMenu popup = new PopupMenu();
		    MenuItem show = new MenuItem("Show UI");
		    show.addActionListener(listenerShow);
		    popup.add(show);
		    MenuItem exit = new MenuItem("Exit");
		    exit.addActionListener(listenerExit);
		    popup.add(exit);

		    trayIcon = new TrayIcon(image, "RemoteLight", popup);
		    trayIcon.setImageAutoSize(true);
		    trayIcon.addActionListener(listenerShow);
		    trayIcon.addMouseListener(mouseListener);
		    try {
		    	tray.remove(trayIcon);
		    	tray.add(trayIcon);
		    	trayIcon.displayMessage("RemoteLight", "Minimized in system tray", MessageType.NONE);
		    } catch (AWTException e) {
		    	Logger.error(e);
		    }
		} else {
			Logger.error("ERROR: TrayIcon not supported!");
		}
	}
	
	
	private static ActionListener listenerShow = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			tray.remove(trayIcon);
			Main.getInstance().getMainFrame().setVisible(true);
		}
    };
    
    private static ActionListener listenerExit = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			tray.remove(trayIcon);
			Main.getInstance().close(true);
		}
    };
    
    private static MouseListener mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
            	tray.remove(trayIcon);
            	Main.getInstance().getMainFrame().setVisible(true);
            }
        }
	};
	
}
