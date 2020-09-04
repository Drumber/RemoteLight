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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLayeredPane;

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
	protected NotificationTask activeTask;
	private NotificationPane currentPane;
	private int posX, posY;
	
	public NotificationDisplayHandler(MainFrame root, NotificationManager manager) {
		this.root = root;
		this.manager = manager;
		this.layeredPane = root.getLayeredPane();
		
		root.addComponentListener(onResize);
		Main.getInstance().getCore().getEventHandler().register(onMenuChange);
		manager.addNotificationListener(notificationListener);
		
		timer = new Timer();
		
		// show pending notifications
		showNotification();
	}
	
	
	/**
	 * Show notification if the manager has one and schedule the timer
	 */
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
			
			if(activeTask != null)
				activeTask.cancel(); // to be save there is no other scheduled task
			activeTask = new NotificationTask();
			timer.schedule(activeTask, noti.getDisplayTime()); // schedule timer
		}
	}
	
	/**
	 * Remove currently displayed notification
	 */
	protected void hideNotification() {
		if(activeTask != null) {
			activeTask.cancel();
			activeTask = null;
		}
		if(currentPane != null) {
			currentPane.setVisible(false);
			layeredPane.remove(currentPane);
			currentPane = null;
		}
	}
	
	/**
	 * Cancel current timer tasks and reset timer for current notification.
	 * e.g. if mouse entered notification pane
	 */
	protected void resetNotificationTimer() {
		if(activeTask != null) {
			activeTask.cancel();
		}
		if(currentPane != null) {
			activeTask = new NotificationTask();
			timer.schedule(activeTask, currentPane.getNotification().getDisplayTime());
		}
	}
	
	
	/** triggered when a new notification is added */
	private NotificationListener notificationListener = new NotificationListener() {
		@Override
		public void onNotification(NotificationManager manager) {
			// check if currentPane is null (timer is inactive)
			if(currentPane == null) {
				showNotification();
			}
			// if currentPane is not null, pending notifications will be
			// automatically shown in the timerTask
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
	
	/**
	 * Calculate notification position to not overlap the bottom bar
	 */
	private void updateLocation() {
		int w = root.getContentPane().getSize().width;
		int h = root.getContentPane().getSize().height;		
		int spaceX = 20;
		int spaceY = 10;
		
		posX = w - NotificationPane.DEFAULT_WIDTH  - spaceX;
		posY = h - NotificationPane.DEFAULT_HEIGHT - spaceY;
		
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
	
	
	/**
	 * TimerTask that is used by the timer
	 */
	private class NotificationTask extends TimerTask {

		/** triggered when timer delay is over (hide notification) */
		@Override
		public void run() {
			if(currentPane != null) {
				if(currentPane.isFocussed()) {
					// mouse entered notification -> leave notification displayed and restart timer
					resetNotificationTimer();
					return;
				}
				// hide the current notification
				hideNotification();
			}
			// check for pending notifications
			if(manager.hasNext()) {
				// manager has pending notification, show it
				showNotification();
			}
		}
	}

}
