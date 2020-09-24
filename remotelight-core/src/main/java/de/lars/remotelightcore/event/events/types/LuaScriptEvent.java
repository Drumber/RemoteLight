package de.lars.remotelightcore.event.events.types;

import de.lars.remotelightcore.event.events.Event;
import de.lars.remotelightcore.lua.LuaScript;

/**
 * LuaScriptEvent called on running or stopping lua scripts.
 */
public class LuaScriptEvent implements Event {
	
	public enum Action {
		START,
		STOP;
	}
	
	private final LuaScript luaScript;
	private final Action action;
	
	public LuaScriptEvent(LuaScript luaScript, Action action) {
		this.luaScript = luaScript;
		this.action = action;
	}
	
	/**
	 * Get the event lua script
	 * @return		{@link LuaScript} instance
	 */
	public LuaScript getLuaScript() {
		return luaScript;
	}
	
	/**
	 * Get the event action
	 * @return		{@link Action#START} on running lua script<br>
	 * 				{@link Action#STOP} on stopping lua script
	 */
	public Action getAction() {
		return action;
	}

}
