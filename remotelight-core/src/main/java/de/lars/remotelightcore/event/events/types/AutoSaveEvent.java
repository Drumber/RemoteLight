package de.lars.remotelightcore.event.events.types;

import de.lars.remotelightcore.event.events.Event;
import de.lars.remotelightcore.event.events.Stated;
import de.lars.remotelightcore.io.FileStorage;

/**
 * AutoSaveEvent called on auto save. This event has two states:
 * <p>
 * PRE: before saving <br>
 * POST: after saving
 */
public class AutoSaveEvent implements Event, Stated {
	
	private final State state;
	private final FileStorage storage;
	
	public AutoSaveEvent(final FileStorage storage, final State state) {
		this.state = state;
		this.storage = storage;
	}

	@Override
	public State getState() {
		return state;
	}

	/**
	 * Get the FileStorage instance that is saved.
	 * 
	 * @return the event FileStorage instance
	 */
	public FileStorage getStorage() {
		return storage;
	}

}
