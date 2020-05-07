/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient;

import java.awt.Color;

import de.lars.remotelightclient.animation.AnimationManager;
import de.lars.remotelightclient.lua.LuaManager;
import de.lars.remotelightclient.musicsync.MusicSyncManager;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.scene.SceneManager;
import de.lars.remotelightclient.screencolor.ScreenColorManager;
import de.lars.remotelightclient.utils.color.PixelColorUtils;

public class EffectManager {
	
	private AnimationManager am;
	private MusicSyncManager msm;
	private SceneManager sm;
	private ScreenColorManager scm;
	private LuaManager lua;
	
	public enum EffectType {
		Animation, Scene, MusicSync, ScreenColor, Lua
	}
	
	public EffectManager() {
		Main main = Main.getInstance();
		am = main.getAnimationManager();
		msm = main.getMusicSyncManager();
		sm = main.getSceneManager();
		scm = main.getScreenColorManager();
		lua = main.getLuaManager();
	}
	
	public void stopAll() {
		if(am.isActive())
			am.stop();
		if(msm.isActive())
			msm.stop();
		if(sm.isActive())
			sm.stop();
		if(scm.isActive())
			scm.stop();
		if(lua.isActive())
			lua.stopLuaScript();
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum()));
	}
	
	public void stopAllExceptFor(EffectType type) {
		if(type != EffectType.Animation && am.isActive()) {
			am.stop();
		}
		if(type != EffectType.Scene && sm.isActive()) {
			sm.stop();
		}
		if(type != EffectType.MusicSync && msm.isActive()) {
			msm.stop();
		}
		if(type != EffectType.ScreenColor && scm.isActive()) {
			scm.stop();
		}
		if(type != EffectType.Lua && lua.isActive()) {
			lua.stopLuaScript();
		}
	}

}
