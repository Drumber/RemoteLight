package de.lars.remotelightcore.notification;

public class Notification {
	
	public final int MAX_OPTIONS = 3;

	/** time in milliseconds when this notification was created */
	private long creationTime;
	/** time in milliseconds when this notification was displayed */
	private long displayedTime = -1;
	
	private NotificationType notificationType;
	private String title;
	private String message;
	private String[] options;
	
	public Notification(NotificationType notificationType, String message) {
		this(notificationType, null, message);
	}
	
	public Notification(NotificationType notificationType, String title, String message) {
		this(notificationType, title, message, null);
	}
	
	public Notification(NotificationType notificationType, String title, String message, String[] options) {
		this.notificationType = notificationType;
		this.title = (title != null ? title : notificationType.toString());
		this.message = message;
		this.options = options;
		this.creationTime = System.currentTimeMillis();
		
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
	
}
