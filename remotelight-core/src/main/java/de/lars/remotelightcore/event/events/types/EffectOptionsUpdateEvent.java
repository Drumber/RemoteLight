package de.lars.remotelightcore.event.events.types;

import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.event.events.Event;

public class EffectOptionsUpdateEvent implements Event {
	
	private EffectType type;
	
	public EffectOptionsUpdateEvent(EffectType type) {
		this.type = type;
	}
	
	public EffectType getType() {
		return type;
	}

}
