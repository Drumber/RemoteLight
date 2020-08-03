package de.lars.remotelightcore.event.events.types;

import de.lars.remotelightcore.event.events.Cancellable;
import de.lars.remotelightcore.event.events.Event;
import de.lars.remotelightcore.event.events.Stated;

/**
 * Shutdown event which can be cancelled and has two states:
 * <p>
 * PRE: shutdown routine started <br>
 * POST: shutdown routine finished
 */
public class ShutdownEvent implements Event, Cancellable, Stated {
	
	private boolean cancelled;
	private STATE state;
	
	public ShutdownEvent(STATE state) {
		this.state = state;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public void setState(STATE state) {
		this.state = state;
	}

	@Override
	public STATE getState() {
		return state;
	}

}
