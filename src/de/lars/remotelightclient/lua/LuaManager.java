package de.lars.remotelightclient.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaManager {
	
	// TEMP
	public static void main(String[] args) {
		new LuaManager();
	}

	private Globals globals;
	
	public LuaManager() {
		globals = JsePlatform.standardGlobals();
		
		String lua = "print('strip', strip) \n"
				+ "print('strip.show', strip.show)";
		
		globals.load(lua).call();
	}
	
	public void runLuaScript(String luaFilePath) {
		LuaValue chunk = globals.loadfile(luaFilePath);
		chunk.call();
	}
	
}
