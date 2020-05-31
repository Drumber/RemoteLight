package de.lars.remotelightclient.ui.notification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLayeredPane;
import javax.swing.Timer;

import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.listeners.MenuChangeListener;
import de.lars.remotelightclient.ui.panels.settings.SettingsPanel;
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
		root.addMenuChangeListener(onMenuChange);
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
	private MenuChangeListener onMenuChange = new MenuChangeListener() {
		@Override
		public void onMenuChange(MenuPanel menuPanel) {
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
