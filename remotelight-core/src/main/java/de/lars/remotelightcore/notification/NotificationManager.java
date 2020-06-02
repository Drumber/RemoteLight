/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Drumber
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

package de.lars.remotelightcore.notification;

import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightcore.notification.listeners.NotificationListener;

public class NotificationManager {
	
	private List<Notification> notificationsQueue;
	private List<Notification> oldNotifications;
	private List<NotificationListener> listeners;
	
	public NotificationManager() {
		notificationsQueue = new ArrayList<>();
		oldNotifications = new ArrayList<>();
		listeners = new ArrayList<>();
	}
	
	public void addNotification(Notification notification) {
		notificationsQueue.add(0, notification);
		fireNotificationEvent();
	}
	
	/**
	 * Get the oldest notification in the queue
	 * @return oldest notification or null if there is none
	 */
	public Notification getNext() {
		if(notificationsQueue.isEmpty())
			return null;
		
		Notification notification = notificationsQueue.get(notificationsQueue.size() - 1);
		notificationsQueue.remove(notificationsQueue.size() - 1);
		oldNotifications.add(0, notification);
		notification.setDisplayed(true);
		return notification;
	}
	
	/**
	 * Check if there is a pending notification
	 * @return true if the notification queue if not empty
	 */
	public boolean hasNext() {
		return !notificationsQueue.isEmpty();
	}
	
	/**
	 * Remove all notifications that have been
	 * registered longer than the specified time.
	 * <p>
	 * <code>
	 * if (creationTime - currTime) >= removeTime
	 * 	-> remove notification
	 * </code>
	 * 
	 * @param removeTime milliseconds after which a notification should be removed
	 */
	public void clean(long removeTime) {
		for(int i = 0; i < notificationsQueue.size(); i++) {
			if(notificationsQueue.get(i).getCreationTime() - System.currentTimeMillis() >= removeTime)
				notificationsQueue.remove(i);
		}
		for(int i = 0; i < oldNotifications.size(); i++) {
			if(oldNotifications.get(i).getCreationTime() - System.currentTimeMillis() >= removeTime)
				oldNotifications.remove(i);
		}
	}
	
	/**
	 * Moves the notification to the old notifications list
	 * @param notification target notification
	 */
	public void hideNotification(Notification notification) {
		if(notificationsQueue.contains(notification))
			notificationsQueue.remove(notification);
		if(!oldNotifications.contains(notification))
			oldNotifications.add(0, notification);
	}
	
	/**
	 * Fully remove/delete notification
	 * @param notification target notification
	 */
	public void removeNotification(Notification notification) {
		if(notificationsQueue.contains(notification))
			notificationsQueue.remove(notification);
		if(oldNotifications.contains(notification))
			oldNotifications.remove(notification);
	}

	/**
	 * Clear all notifications (old and new ones)
	 */
	public void clear() {
		notificationsQueue.clear();
		oldNotifications.clear();
	}
	
	public void addNotificationListener(NotificationListener listener) {
		listeners.add(listener);
	}
	
	public void removeNotificationListener(NotificationListener listener) {
		if(listeners.contains(listener))
			listeners.remove(listener);
	}
	
	protected void fireNotificationEvent() {
		if(!listeners.isEmpty()) {
			for(NotificationListener l :listeners)
				if(l != null)
					l.onNotification(this);
		}
	}

}
