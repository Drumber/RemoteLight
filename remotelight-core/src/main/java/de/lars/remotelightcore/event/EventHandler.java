package de.lars.remotelightcore.event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.tinylog.Logger;

import de.lars.remotelightcore.event.events.Event;

public class EventHandler {
	
	/** Map holding all event types and the registered listeners */
	private final Map<Class<? extends Event>, List<Listener<? extends Event>>> registerdEvents;
	
	/**
	 * Create a new EventHandler
	 */
	public EventHandler() {
		this.registerdEvents = new HashMap<Class<? extends Event>, List<Listener<? extends Event>>>();
	}
	
	/**
	 * Register a new listener for the specified event type.
	 * 
	 * @param event		the event you want to listen for
	 * @param listener 	the listener you want to register
	 */
	public void register(Class<? extends Event> event, Listener<? extends Event> listener) {
		if(!registerdEvents.containsKey(event)) {
			registerdEvents.put(event, new CopyOnWriteArrayList<Listener<? extends Event>>());
		}
		if(!registerdEvents.get(event).contains(listener)) {
			registerdEvents.get(event).add(listener);
		}
	}
	
	/**
	 * Register a new listener for the (as generic type) specified event type.
	 * <p>
	 * Note: When using Lambda, the {@link #register(Class, Listener)} method must be used,
	 * otherwise the event type cannot be determined.
	 * 
	 * @param listener	the listener you want to register
	 */
	public void register(Listener<? extends Event> listener) {
		Class<? extends Event> eventType = getEventFromListener(listener);
		if(eventType != null) {
			register(eventType, listener);
		} else {
			// could happen when using lambda expression
			Logger.warn("Could not register listener. No generic type found that implements the Event interface.");
		}
	}
	
	/**
	 * Unregister the listener of the specified event type.
	 * 
	 * @param event		the event type
	 * @param listener	the listener you want to unregister
	 */
	public void unregister(Class<? extends Event> event, Listener<? extends Event> listener) {
		if(registerdEvents.containsKey(event)) {
			if(registerdEvents.get(event).contains(listener)) {
				registerdEvents.get(event).remove(listener);
			}
		}
	}
	
	/**
	 * Calls an event and triggers all registered listeners of this event type.
	 * 
	 * @param <T>	event type
	 * @param event	the event you want to call
	 * @return		Returns the passed event
	 */
	@SuppressWarnings("unchecked")
	public <T extends Event> T call(final T event) {
		if(registerdEvents.containsKey(event.getClass())) {
			List<Listener<? extends Event>> listeners = registerdEvents.get(event.getClass());
			for(final Listener<? extends Event> listener : listeners) {
				if(listener != null) {
					((Listener<T>) listener).onEvent(event);
				}
			}
		}
		return event;
	}
	
	
	/**
	 * Get the event type from the generic parameter of the listener.
	 * 
	 * @param 	listener the target listener
	 * @return 	The first found event class. Returns null if no class, that
	 * 			implements {@link Event}, was found.
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends Event> getEventFromListener(Listener<? extends Event> listener) {
		// get all types of the class
		Type[] genericParams = listener.getClass().getGenericInterfaces();
		// loop trough all classes
		for(Type type : genericParams) {
			// the event class
			Class<?> clazz = null;
			System.out.println(type.getTypeName());
			// check whether it is a parameterized type or a class
			if(type instanceof ParameterizedType) {
				ParameterizedType ptype = (ParameterizedType) type;
				// loop through all types (generic types)
				for(Type actualType : ptype.getActualTypeArguments()) {
					if(actualType instanceof Class) {
						clazz = (Class<?>) actualType;
					}
				}
			} else if(type instanceof Class) {
				clazz = (Class<?>) type;
			}
			
			if(clazz != null) {
				// check if the class is really a subclass of Event
				if(Event.class.isAssignableFrom(clazz)) {
					return (Class<? extends Event>) clazz;
				}
			}
		}
		return null;
	}

}
