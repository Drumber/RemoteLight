package de.lars.remotelightclient.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import de.lars.remotelightclient.ui.comps.NotificationPane;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationListener;
import de.lars.remotelightcore.notification.NotificationManager;

public class NotificationDisplayHandler {
	
	/** notification display time */
	private int notificationDisplayTime = 1000 * 3;
	
	private JFrame root;
	private NotificationManager manager;
	private JLayeredPane layeredPane;
	
	private Timer timer;
	private NotificationPane currentPane;
	private int posX, posY;
	
	public NotificationDisplayHandler(JFrame root, NotificationManager manager) {
		this.root = root;
		this.manager = manager;
		this.layeredPane = root.getLayeredPane();
		
		root.addComponentListener(onResize);
		manager.addNotificationListener(notificationListener);
		
		timer = new Timer(notificationDisplayTime, timerListener);
		timer.stop();
		timer.setInitialDelay(0);
	}
	
	
	private void showNotification() {
		if(manager.hasNext()) {
			Notification noti = manager.getNext();
			
			NotificationPane pane = new NotificationPane(noti);
			pane.setOpaque(true);
			pane.setSize(NotificationPane.DEFAULT_WIDTH, NotificationPane.DEFAULT_HEIGHT);
			pane.setBorder(new LineBorder(Color.gray));
			pane.setVisible(true);
			
			currentPane = pane;
			updateLocation();
			// add it to layered pane
			layeredPane.add(pane, JLayeredPane.POPUP_LAYER);
		}
	}
	
	
	/** triggered when timer delay is over */
	private ActionListener timerListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(currentPane != null) {
				currentPane.setVisible(false);
				layeredPane.remove(currentPane);
				currentPane = null;
			}
			
			if(!manager.hasNext()) {
				System.out.println("No notification");
				// no more notifications, stop timer
				timer.stop();
				return;
			}
			System.out.println("Pending notification");
			// manager has pending notification, show it
			showNotification();
		}
	};
	
	/** triggered when a new notification is added */
	private NotificationListener notificationListener = new NotificationListener() {
		@Override
		public void onNotification(NotificationManager manager) {
			if(!timer.isRunning()) {
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
	
	private void updateLocation() {
		int w = root.getBounds().width;
		int h = root.getBounds().height;
		int space = 10;
		
		posX = w - NotificationPane.DEFAULT_WIDTH  - space;
		posY = h - NotificationPane.DEFAULT_HEIGHT - space;
		
		if(currentPane != null) {
			currentPane.setLocation(posX, posY);
		}
	}

}
