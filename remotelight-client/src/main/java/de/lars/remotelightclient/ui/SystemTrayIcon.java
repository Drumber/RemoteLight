/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightclient.ui;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.*;

import javax.swing.ImageIcon;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.StartUp;
import de.lars.remotelightcore.RemoteLightCore;

public class SystemTrayIcon {

	private static SystemTray tray;
	private static TrayIcon trayIcon;
	
	//Michael Myers; https://stackoverflow.com/questions/758083/how-do-i-put-a-java-app-in-the-system-tray
	public static void showTrayIcon() {
		if (SystemTray.isSupported()) {
		    tray = SystemTray.getSystemTray();
		    Image image = new ImageIcon(StartUp.class.getResource("/resources/Icon-16x16.png")).getImage();

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
			RemoteLightCore.getInstance().close(true);
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
