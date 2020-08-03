package de.lars.remotelightcore.event.events;

/**
 * Adds the ability to be cancelled.
 */
public interface Cancellable {
	
	/**
	 * Returns the cancelled state of the event
	 * 
	 * @return true if cancelled, false otherwise
	 */
	boolean isCancelled();
	
	/**
	 * Sets whether the event should be cancelled.
	 * 
	 * @param cancelled cancel state
	 */
	void setCancelled(boolean cancelled);

}
