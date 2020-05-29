package de.lars.remotelightcore.lua;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.tinylog.Logger;

import de.lars.remotelightcore.EffectManager;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.lua.utils.LedStrip;
import de.lars.remotelightcore.lua.utils.LuaColor;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.maths.TimeUtil;

public class LuaManager extends EffectManager {

	private int MAX_INSTRUCTIONS;	// instruction per ms
	
	private Globals globals;
	private CustomLuaDebugLib debugLib;
	private LuaThread lThread;
	private Timer timer;
	private List<File> luaScripts;
	private String activeScriptPath;
	
	private LuaExceptionListener listener;
	private TimeUtil timeUtil;
	private int delay;
	
	public LuaManager() {
		LuaThread.thread_orphan_check_interval = 5;
		// settings
		SettingsManager sm = RemoteLightCore.getInstance().getSettingsManager();
		sm.addSetting(new SettingBoolean("lua.advanced", "Lua Advanced", SettingCategory.Others, "Allow Lua scripts access to insecure libraries, such as OS and IO lib.", false));
		sm.addSetting(new SettingInt("lua.instructions", "Lua instruction per ms", SettingCategory.Others, "Number of instructions a Lua script can execute per millisecond.", 40, 1, 500, 1));
		MAX_INSTRUCTIONS = ((SettingInt) sm.getSettingFromId("lua.instructions")).getValue();
		boolean advanced = ((SettingBoolean) sm.getSettingFromId("lua.advanced")).getValue();
		
		// set up globals
		if(advanced) {	// allow access to all libraries
			globals = JsePlatform.standardGlobals();
		} else {		// allow access only to necessary libraries
			globals = new Globals();
			globals.load(new JseBaseLib());
			globals.load(new PackageLib());
			globals.load(new Bit32Lib());
			globals.load(new TableLib());
			globals.load(new StringLib());
			globals.load(new MathLib());
			LoadState.install(globals);
			LuaC.install(globals);
		}
		// add custom debug lib to limit lua instructions per ms
		debugLib = new CustomLuaDebugLib(globals, MAX_INSTRUCTIONS);
		globals.load(debugLib);
		// coerce LedStrip and LuaColor class
		globals.set("strip", CoerceJavaToLua.coerce(new LedStrip()));
		globals.set("colorUtil", CoerceJavaToLua.coerce(new LuaColor()));
		
		timeUtil = new TimeUtil(delay, true);
		timer = new Timer(1, timerUpdate);
	}
	
	@Override
	public String getName() {
		return "LuaManager";
	}
	
	/**
	 * Run a lua script file
	 * @param luaFilePath Path to the lua file
	 */
	public void runLuaScript(String luaFilePath) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				RemoteLightCore.getInstance().getEffectManagerHelper().stopAllExceptFor(EffectType.Lua);
				// stop active scripts
				if(activeScriptPath != null) {
					stopLuaScript();
					activeScriptPath = luaFilePath;
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}
				}
				
				Logger.info("Started new thread for lua script: " + luaFilePath);
				activeScriptPath = luaFilePath;
				debugLib.setInterrupted(false);
				
				try {
					LuaValue chunk = globals.loadfile(luaFilePath);
					lThread = new LuaThread(globals, chunk);
					Varargs error = lThread.resume(LuaValue.NIL);
					
					if(error.arg(2) != LuaValue.NIL) {
						throw new LuaError(error.arg(2).tojstring());
					}
					timer.start();
				} catch(LuaError e) {
					Logger.error(e);
					onException(e);
				}
			}
		}, "Lua thread").start();
	}
	
	/**
	 * Checks every millisecond if the lua script has instructions left
	 */
	private ActionListener timerUpdate = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			debugLib.addInstructions(MAX_INSTRUCTIONS);
			if(debugLib.getInstructionsLeft() > 0) {	// check if instructionsleft is > 0
				lThread.resume(LuaValue.NIL);
			}
		}
	};
	
	/**
	 * Stop the current lua script
	 */
	public void stopLuaScript() {
		debugLib.setInterrupted(true);
		Logger.info("Stopped lua script: " + activeScriptPath);
		activeScriptPath = null;
		PixelColorUtils.setAllPixelsBlack();
	}
	
	@Override
	public void stop() {
		stopLuaScript();
	}
	
	/**
	 * 
	 * @return True if no lua script is active
	 */
	@Override
	public boolean isActive() {
		return activeScriptPath != null;
	}
	
	/**
	 * 
	 * @return The path of the current active lua script
	 */
	public String getActiveLuaScriptPath() {
		return activeScriptPath;
	}
	
	/**
	 * Set the delay for the delayReached() function in lua
	 * @param delay in ms
	 */
	public void setDelay(int delay) {
		timeUtil.setInterval(delay);
		this.delay = delay;
	}
	
	/**
	 * 
	 * @return The delay
	 */
	public int getDelay() {
		return delay;
	}
	
	/**
	 * 
	 * @return TimeUtil for the delayReached() function in lua 
	 */
	public TimeUtil getTimer() {
		return timeUtil;
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
	
	public interface LuaExceptionListener {
		public void onLuaException(Exception e);
	}
	
	public synchronized void setLuaExceptionListener(LuaExceptionListener l) {
		this.listener = l;
	}
	
	private void onException(Exception e) {
		if(listener != null) {
			listener.onLuaException(e);
		}
	}
	
}
