package de.lars.remotelightcore.event.events.types;

import de.lars.remotelightcore.event.events.Event;
import de.lars.remotelightcore.out.Output;

/**
 * ConnectionEvent called on activating or deactivating an output.
 */
public class ConnectionEvent implements Event {
	
	public enum Action {
		ACTIVATE,
		DEACTIVATE;
	}
	
	private final Output output;
	private final Action action;
	
	public ConnectionEvent(Output output, Action action) {
		this.output = output;
		this.action = action;
	}

	/**
	 * Get the event output
	 * 
	 * @return the event output
	 */
	public Output getOutput() {
		return output;
	}

	/**
	 * Get the connection type
	 * 
	 * @return 	{@link Action#ACTIVATE} on enabling output or <br>
	 * 			{@link Action#DEACTIVATE} on disabling output
	 */
	public Action getAction() {
		return action;
	}

}
