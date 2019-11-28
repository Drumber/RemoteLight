package de.lars.remotelightclient.lua;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.TimeUtil;

public class LuaManager {

	private Globals globals;
	private CustomLuaDebugLib debugLib;
	private List<File> luaScripts;
	private String activeScriptPath;
	
	private TimeUtil timer;
	private int delay;
	
	public LuaManager() {
		globals = JsePlatform.standardGlobals();
		debugLib = new CustomLuaDebugLib();
		globals.load(debugLib);
		globals.set("strip", CoerceJavaToLua.coerce(new LedStrip()));
		
		timer = new TimeUtil(delay, true);
		
//		String lua = "c = {{255, 0, 0}, {0, 255, 0}} \n" +
//		"print('strip', strip.show(c))";
//		globals.load(lua).call();
	}
	
	public void runLuaScript(String luaFilePath) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				activeScriptPath = luaFilePath;
				debugLib.setInterrupted(false);
				LuaValue chunk = globals.loadfile(luaFilePath);
				chunk.call();
			}
		}, "Lua thread").start();
	}
	
	public void stopLuaScript() {
		debugLib.setInterrupted(true);
		activeScriptPath = null;
		PixelColorUtils.setAllPixelsBlack();
	}
	
	public String getActiveLuaScriptPath() {
		return activeScriptPath;
	}
	
	public void setDelay(int delay) {
		timer.setInterval(delay);
		this.delay = delay;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public TimeUtil getTimer() {
		return timer;
	}
	
	/**
	 * Scan for Lua scripts
	 * @param path Directory where the lua scripts are located
	 */
	public List<File> scanLuaScripts(String path) {
		if(luaScripts == null) {
			luaScripts = new ArrayList<File>();
		} else {
			luaScripts.clear();
		}
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
	
	/**
	 * Returns a list of all previous scanned lua scripts.
	 * If list is null, it will be automatically scanned for lua scripts.
	 * @param path Directory where the lua scripts are located
	 */
	public List<File> getLuaScripts(String path) {
		if(luaScripts == null) {
			scanLuaScripts(path);
		}
		return luaScripts;
	}
	
}
