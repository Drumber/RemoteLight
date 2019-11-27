package de.lars.remotelightclient.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.tinylog.Logger;

public class LedStrip extends TwoArgFunction {
	
	public LedStrip() {}

	@Override
	public LuaValue call(LuaValue modName, LuaValue env) {
		Logger.debug("LUA: modeName: " + modName + ", Env: " + env.toString());
		
		LuaTable strip = new LuaTable();
		strip.set("show", new show());
		env.set("strip", strip);
		
		env.get("package").get("loaded").set("strip", strip);
		return strip;
	}
	
	class show extends OneArgFunction {
		@Override
		public LuaValue call(LuaValue colorTable) {
			//colorTable.checktable();
			return LuaValue.valueOf(1);
		}
		
	}

}
