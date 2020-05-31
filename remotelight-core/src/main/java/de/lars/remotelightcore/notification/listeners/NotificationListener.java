package de.lars.remotelightcore.notification.listeners;

import de.lars.remotelightcore.notification.NotificationManager;

public interface NotificationListener {
	
	/**
	 * Executed when a new notification is added.
	 * <p>
	 * The notification can be accessed with <code>manager.getNext()</code>.
	 * 
	 * @param manager NotificationManager to which the notification was added
	 */
	void onNotification(NotificationManager manager);

}
