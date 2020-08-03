package de.lars.remotelightcore.event;

import de.lars.remotelightcore.event.events.Event;

public interface Listener<T extends Event> {

	void onEvent(T event);

}
