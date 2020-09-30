/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightcore.lua;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.MathLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.tinylog.Logger;

import de.lars.remotelightcore.EffectManager;
import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.event.events.types.LuaScriptEvent;
import de.lars.remotelightcore.event.events.types.LuaScriptEvent.Action;
import de.lars.remotelightcore.lua.utils.LedStrip;
import de.lars.remotelightcore.lua.utils.LuaColor;
import de.lars.remotelightcore.lua.utils.LuaSettings;
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
	private LuaScript activeScript;
	
	private LuaExceptionListener listener;
	private TimeUtil timeUtil;
	private int delay;
	
	public LuaManager() {
		LuaThread.thread_orphan_check_interval = 5;
		// settings
		SettingsManager sm = RemoteLightCore.getInstance().getSettingsManager();
		sm.addSetting(new SettingBoolean("lua.advanced", "Lua Advanced", SettingCategory.Others, "Allow Lua scripts access to insecure libraries, such as OS and IO lib.", false));
		sm.addSetting(new SettingInt("lua.instructions", "Lua instruction per ms", SettingCategory.Others, "Number of instructions a Lua script can execute per millisecond.", 40, 1, 500, 1));
		MAX_INSTRUCTIONS = ((SettingInt) sm.getSettingFromId("lua.instructions")).get();
		boolean advanced = ((SettingBoolean) sm.getSettingFromId("lua.advanced")).get();
		
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
		// coerce LedStrip, LuaColor and LuaSettings class
		globals.set("strip", CoerceJavaToLua.coerce(new LedStrip()));
		globals.set("colorUtil", CoerceJavaToLua.coerce(new LuaColor()));
		globals.set("settings", CoerceJavaToLua.coerce(new LuaSettings()));
		
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
				executeScript(luaFilePath);
				
				// lua animation loop
				while(activeScript != null && activeScript.isActive()) {
					activeScript.onLoop();
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						Logger.error(e);
					}
				}
			}
		}, "Lua thread").start();
	}
	
	private void executeScript(String luaFilePath) {
		// stop active scripts
		if(activeScript != null) {
			stopLuaScript();
			try {
				// some delay to be sure the previous script is stopped
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		
		Logger.info("Started new thread for lua script: " + luaFilePath);
		
		activeScript = new LuaScript(globals, luaFilePath);
		debugLib.setInterrupted(false);
		
		try {
			LuaValue chunk = activeScript.load();
			lThread = new LuaThread(globals, chunk);
			Varargs error = lThread.resume(LuaValue.NIL);
			
			if(error.arg(2) != LuaValue.NIL) {
				throw new LuaError(error.arg(2).tojstring());
			}
			timer.start();
			// call lua script start event
			RemoteLightCore.getInstance().getEventHandler().call(new LuaScriptEvent(activeScript, Action.START));
		} catch(LuaError e) {
			Logger.error(e, "Error while executing lua script '" + luaFilePath + "'.");
			onException(e);
			stopLuaScript();
		}
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
		if(activeScript != null) {
			Logger.info("Stopped lua script: " + activeScript.getFilePath());
			
			activeScript.setActive(false);
			debugLib.setInterrupted(true);
			
			LuaScriptEvent event = new LuaScriptEvent(activeScript, Action.STOP);
			activeScript = null;
			PixelColorUtils.setAllPixelsBlack();
			// call lua script stop event
			RemoteLightCore.getInstance().getEventHandler().call(event);
		}
	}
	
	@Override
	public void stop() {
		stopLuaScript();
	}
	
	/**
	 * Check if some lua script is currently running
	 * @return True if no lua script is active
	 */
	@Override
	public boolean isActive() {
		return activeScript != null && activeScript.isActive();
	}
	
	/**
	 * Get the current active lua script instance
	 * @return		{@link LuaScript} instance
	 * 				or null if no script is active
	 */
	public LuaScript getActiveLuaScript() {
		return activeScript;
	}
	
	/**
	 * Get the file path of the current active lua script
	 * @return The path of the current active lua script
	 */
	public String getActiveLuaScriptPath() {
		return activeScript != null ? activeScript.getFilePath() : null;
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
	 * Get the specified loop delay
	 * @return The delay
	 */
	public int getDelay() {
		return delay;
	}
	
	/**
	 * Get the TimeUtil used for the delayReached() function in lua
	 * @return			{@link TimeUtil} instance
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
