package de.lars.remotelightcore.notification;

/**
 * Notification types:
 * <p>
 * <code>ERROR, WARN, INFO, DEBUG, NOTIFICATION, SUCCESS, NONE</code>
 */
public enum NotificationType {
	
	ERROR("Error"),
	WARN("Warning"),
	INFO("Info"),
	DEBUG("Debug"),
	NOTIFICATION("Notification"),
	SUCCESS("Success"),
	NONE("");
	
	private final String text;
	
	NotificationType(String text) {
		this.text = text;
	}
	
	/**
	 * Notification type as string
	 */
	@Override
	public String toString() {
		return text;
	}

}
