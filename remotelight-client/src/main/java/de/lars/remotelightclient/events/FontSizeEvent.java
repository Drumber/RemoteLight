package de.lars.remotelightclient.events;

import de.lars.remotelightcore.event.events.Event;

public class FontSizeEvent implements Event {
	
	private final int fontSize;

	public FontSizeEvent(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getFontSize() {
		return fontSize;
	}
	
}
