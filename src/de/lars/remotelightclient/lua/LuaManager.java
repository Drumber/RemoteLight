package de.lars.remotelightclient.lua;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaManager {

	private Globals globals;
	
	public LuaManager() {
		globals = JsePlatform.standardGlobals();
		globals.set("strip", CoerceJavaToLua.coerce(new LedStrip()));
		
//		String lua = "c = {{255, 0, 0}, {0, 255, 0}} \n" +
//		"print('strip', strip.show(c))";
//		globals.load(lua).call();
	}
	
	public void runLuaScript(String luaFilePath) {
		LuaValue chunk = globals.loadfile(luaFilePath);
		chunk.call();
	}
	
	/**
	 * 
	 * @param path Directory where the lua scripts are located
	 */
	public List<File> getLuaScripts(String path) {
		List<File> luaScripts = new ArrayList<File>();
		File[] allFiles = new File(path).listFiles();
		
		for(int i = 0; i < allFiles.length; i++) {
			if(allFiles[i].isFile()) {
				// check if file is a lua file
				String fileName = allFiles[i].getName();
				int extensionIndex = fileName.lastIndexOf('.');
				if(extensionIndex > 0 && fileName.substring(extensionIndex + 1).equalsIgnoreCase("lua")) {
					luaScripts.add(allFiles[i]);
				}
			}
		}
		return luaScripts;
	}
	
}
