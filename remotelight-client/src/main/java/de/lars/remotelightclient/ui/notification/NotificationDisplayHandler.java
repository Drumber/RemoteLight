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

package de.lars.remotelightclient.ui.notification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLayeredPane;
import javax.swing.Timer;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.events.MenuEvent;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.panels.settings.SettingsPanel;
import de.lars.remotelightcore.event.Listener;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationManager;
import de.lars.remotelightcore.notification.listeners.NotificationListener;

public class NotificationDisplayHandler {
	
	private MainFrame root;
	private NotificationManager manager;
	private JLayeredPane layeredPane;
	
	protected Timer timer;
	private NotificationPane currentPane;
	private int posX, posY;
	
	public NotificationDisplayHandler(MainFrame root, NotificationManager manager) {
		this.root = root;
		this.manager = manager;
		this.layeredPane = root.getLayeredPane();
		
		root.addComponentListener(onResize);
		Main.getInstance().getCore().getEventHandler().register(onMenuChange);
		manager.addNotificationListener(notificationListener);
		
		timer = new Timer(Notification.NORMAL, timerListener);
		timer.stop();
		timer.setInitialDelay(0);
	}
	
	
	private void showNotification() {
		if(manager.hasNext()) {
			Notification noti = manager.getNext();
			
			NotificationPane pane = new NotificationPane(noti, this);
			pane.setOpaque(true);
			pane.setSize(NotificationPane.DEFAULT_WIDTH, NotificationPane.DEFAULT_HEIGHT);
			pane.setVisible(true);
			
			currentPane = pane;
			updateLocation();
			// add it to layered pane
			layeredPane.add(pane, JLayeredPane.POPUP_LAYER);
			timer.setInitialDelay(noti.getDisplayTime());
			timer.restart();
		}
	}
	
	/**
	 * Remove currently displayed notification
	 */
	protected void hideNotification() {
		if(currentPane != null) {
			currentPane.setVisible(false);
			layeredPane.remove(currentPane);
			currentPane = null;
		}
	}
	
	
	/** triggered when timer delay is over */
	private ActionListener timerListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(currentPane != null) {
				if(currentPane.isFocussed())
					return; // mouse entered notification -> leave notification displayed
				hideNotification();
			}
			if(!manager.hasNext()) {
				// no more notifications, stop timer
				timer.stop();
				return;
			}
			// manager has pending notification, show it
			showNotification();
		}
	};
	
	/** triggered when a new notification is added */
	private NotificationListener notificationListener = new NotificationListener() {
		@Override
		public void onNotification(NotificationManager manager) {
			if(!timer.isRunning()) {
				timer.setInitialDelay(0);
				timer.start();
			}
		}
	};
	
	
	/** triggered when frame is resized */
	private ComponentAdapter onResize = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			updateLocation();
		};
	};
	
	/** Triggered when a menu is changed */
	private Listener<MenuEvent> onMenuChange = new Listener<MenuEvent>() {
		@Override
		public void onEvent(MenuEvent event) {
			updateLocation();
		}
	};
	
	private void updateLocation() {
		int w = root.getContentPane().getSize().width;
		int h = root.getContentPane().getSize().height;		
		int space = 10;
		
		posX = w - NotificationPane.DEFAULT_WIDTH  - space;
		posY = h - NotificationPane.DEFAULT_HEIGHT - space;
		
		if(root.isControlBarShown()) {
			// add control bar offset
			posY -= root.getDisplayedControlBar().getPreferredSize().height;
		}
		// add settings panel save button offset
		if(root.getDisplayedPanel() instanceof SettingsPanel) {
			posY -= 25;
		}
						
		if(currentPane != null) {
			currentPane.setLocation(posX, posY);
		}
	}

}
