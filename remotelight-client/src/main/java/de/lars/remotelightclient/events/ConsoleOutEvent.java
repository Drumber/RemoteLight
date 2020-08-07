package de.lars.remotelightclient.events;

import de.lars.remotelightcore.event.events.Event;

public class ConsoleOutEvent implements Event {

	private final boolean isErrorOut;
	private final String content;
	
	/**
	 * Create a new console output event.
	 * 
	 * @param isErrorOut	whether this is an error output
	 * 						or a regular console output
	 */
	public ConsoleOutEvent(boolean isErrorOut, String content) {
		this.isErrorOut = isErrorOut;
		this.content = content;
	}
	
	public boolean isErrorOut() {
		return isErrorOut;
	}
	
	public String getContent() {
		return content;
	}
	
}
