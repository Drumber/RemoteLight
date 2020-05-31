package de.lars.remotelightcore.notification;

import de.lars.remotelightcore.notification.listeners.NotificationOptionListener;

public class Notification {
	
	/** Short notification display time */
	public final static int SHORT	= 1000 * 3;
	/** Normal notification display time (default) */
	public final static int NORMAL	= 1000 * 6;
	/** Long notification display time */
	public final static int LONG	= 1000 * 10;
	
	public final int MAX_OPTIONS = 3;

	/** time in milliseconds when this notification was created */
	private long creationTime;
	/** time in milliseconds when this notification was displayed (start display time) */
	private long displayedTime = -1;
	private int showTime = NORMAL;
	
	private NotificationType notificationType;
	private String title;
	private String message;
	private String[] options;
	private NotificationOptionListener optionListener;
	/** hide notification when an options is clicked */
	private boolean hideOnOptionClick = true;
	
	public Notification(NotificationType notificationType, String message) {
		this(notificationType, null, message);
	}
	
	public Notification(NotificationType notificationType, String title, String message) {
		this(notificationType, title, message, null);
	}
	
	public Notification(NotificationType notificationType, String title, String message, String[] options) {
		this(notificationType, title, message, options, NORMAL, null);
	}
	
	public Notification(NotificationType notificationType, String title, String message, int displayTime) {
		this(notificationType, title, message, null, displayTime, null);
	}
	
	/**
	 * Create a new notification
	 * @param notificationType	notification type (see {@link NotificationType})
	 * @param title				notification title (nullable, uses NotificationType instead)
	 * @param message			notification long message
	 * @param options			options shown for this notification (nullable)
	 * @param displayTime		for how long should the notification be visible
	 * @param l option			click listener (nullable)
	 */
	public Notification(NotificationType notificationType, String title, String message, String[] options, int displayTime, NotificationOptionListener l) {
		this.notificationType = notificationType;
		this.title = (title != null ? title : notificationType.toString());
		this.message = message;
		this.options = options;
		this.creationTime = System.currentTimeMillis();
		this.optionListener = l;
		
		if(options != null && options.length > MAX_OPTIONS)
			throw new IllegalArgumentException("Only a maximum of " + MAX_OPTIONS
					+ " are allowed, got " + options.length + " options.");
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public long getCreationTime() {
		return creationTime;
	}
	
	public long getDisplayedTime() {
		return displayedTime;
	}
	
	public void setDisplayed(boolean displayed) {
		if(displayedTime == -1 && displayed)
			displayedTime = System.currentTimeMillis();
		else if(!displayed)
			displayedTime = -1;
	}
	
	/**
	 * Check if this notification was displayed or is currently displayed
	 * @return true if was/is displayed, false otherwise
	 */
	public boolean isDisplayed() {
		return displayedTime != -1;
	}

	public NotificationOptionListener getOptionListener() {
		return optionListener;
	}

	public void setOptionListener(NotificationOptionListener optionListener) {
		this.optionListener = optionListener;
	}

	public boolean isHideOnOptionClick() {
		return hideOnOptionClick;
	}

	/**
	 * Should a notification be hidden after an option has been clicked
	 * (default {@code true})
	 * @param hideOnOptionClick enable/disable notification auto hide
	 */
	public void setHideOnOptionClick(boolean hideOnOptionClick) {
		this.hideOnOptionClick = hideOnOptionClick;
	}

	public int getDisplayTime() {
		return showTime;
	}

	public void setDisplayTime(int showTime) {
		this.showTime = showTime;
	}
	
}
