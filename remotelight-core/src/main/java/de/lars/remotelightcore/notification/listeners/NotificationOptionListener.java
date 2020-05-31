package de.lars.remotelightcore.notification.listeners;

public interface NotificationOptionListener {

	/**
	 * Triggered when a notification option is clicked
	 * @param option clicked option
	 * @param index clicked option index (option array index)
	 */
	void onOptionClicked(String option, int index);
	
}
