package de.lars.remotelightcore.event.events;

/**
 * Adds the ability to an event to have a PRE and a POST state.
 */
public interface Stated {
	
	public enum State {
		PRE,
		POST;
	}
	
	State getState();

}
