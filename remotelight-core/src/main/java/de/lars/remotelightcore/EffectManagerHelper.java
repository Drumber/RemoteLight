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

package de.lars.remotelightcore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.animation.AnimationManager;
import de.lars.remotelightcore.effect.AbstractEffect;
import de.lars.remotelightcore.lua.LuaManager;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.musicsync.MusicSyncManager;
import de.lars.remotelightcore.scene.Scene;
import de.lars.remotelightcore.scene.SceneManager;
import de.lars.remotelightcore.screencolor.AbstractScreenColorManager;

public class EffectManagerHelper {
	
	private EffectManager[] allManager;
	private AnimationManager am;
	private MusicSyncManager msm;
	private SceneManager sm;
	private AbstractScreenColorManager scm;
	private LuaManager lua;
	
	public enum EffectType {
		Animation, Scene, MusicSync, ScreenColor, Lua
	}
	
	public EffectManagerHelper() {
		updateManagers();
	}
	
	protected void updateManagers() {
		RemoteLightCore remoteLightCore = RemoteLightCore.getInstance();
		am = remoteLightCore.getAnimationManager();
		msm = remoteLightCore.getMusicSyncManager();
		sm = remoteLightCore.getSceneManager();
		scm = remoteLightCore.getScreenColorManager();
		lua = remoteLightCore.getLuaManager();
		if(scm == null) {
			allManager = new EffectManager[] {am, msm, sm, lua};
		} else {
			allManager = new EffectManager[] {am, msm, sm, lua, scm};
		}
	}
	
	public EffectManager[] getAllManagers() {
		return allManager;
	}
	
	public void stopAll() {
		if(am.isActive())
			am.stop();
		if(msm.isActive())
			msm.stop();
		if(sm.isActive())
			sm.stop();
		if(scm != null && scm.isActive())
			scm.stop();
		if(lua.isActive())
			lua.stopLuaScript();
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
		if(type != EffectType.ScreenColor && scm != null && scm.isActive()) {
			scm.stop();
		}
		if(type != EffectType.Lua && lua.isActive()) {
			lua.stopLuaScript();
		}
	}
	
	/**
	 * Get current active manager
	 * @return 	active manager or {@code null}
	 * 			if no manager is active
	 */
	public EffectManager getActiveManager() {
		for(EffectManager em : allManager) {
			if(em != null && em.isActive())
				return em;
		}
		return null;
	}
	
	/**
	 * Start effect/animation using specified manager
	 * @param manager corresponding EffectManager (except for ScreenColorManager)
	 * @param effect effect/animation etc to start
	 * @return true if effect was found and started, false otherwise
	 * 			
	 */
	public boolean startEffect(EffectManager manager, String effect) {
		if(manager instanceof AnimationManager) {
			AnimationManager m = (AnimationManager) manager;
			for(Animation animation : m.getAnimations()) {
				if(animation.getName().equalsIgnoreCase(effect) ||
						animation.getDisplayname().equalsIgnoreCase(effect)) {
					m.start(animation);
					return true;
				}
			}
		} else if(manager instanceof MusicSyncManager) {
			MusicSyncManager m = (MusicSyncManager) manager;
			for(MusicEffect me : m.getMusicEffects()) {
				if(me.getName().equalsIgnoreCase(effect) ||
						me.getDisplayname().equalsIgnoreCase(effect)) {
					m.start(me);
					return true;
				}
			}
		} else if(manager instanceof SceneManager) {
			SceneManager m = (SceneManager) manager;
			for(Scene scene : m.getScenes()) {
				if(scene.getName().equalsIgnoreCase(effect) ||
						scene.getDisplayname().equalsIgnoreCase(effect)) {
					m.start(scene);
					return true;
				}
			}
		} else if(manager instanceof LuaManager) {
			LuaManager m = (LuaManager) manager;
			if(new File(effect).isFile()) {
				m.runLuaScript(effect);
				return true;
			}
		} else if(manager instanceof AbstractScreenColorManager) {
			AbstractScreenColorManager scm = (AbstractScreenColorManager) manager;
			scm.start();
			return true;
		}
		return false;
	}
	
	/**
	 * Get all effects/animations as string
	 * @param em corresponding manager (only animation, musicsync, scene manager)
	 * @return a list of all effect displaynames or null if manager is not supported
	 */
	public List<String> getAllEffects(EffectManager manager) {
		List<String> names = new ArrayList<>();
		if(manager instanceof AnimationManager) {
			AnimationManager m = (AnimationManager) manager;
			for(Animation animation : m.getAnimations()) {
				names.add(animation.getDisplayname());
			}
		} else if(manager instanceof MusicSyncManager) {
			MusicSyncManager m = (MusicSyncManager) manager;
			for(MusicEffect me : m.getMusicEffects()) {
				names.add(me.getDisplayname());
			}
		} else if(manager instanceof SceneManager) {
			SceneManager m = (SceneManager) manager;
			for(Scene scene : m.getScenes()) {
				names.add(scene.getDisplayname());
			}
		}
		if(names.size() > 0) {
			return names;
		}
		return null;
	}
	
	/**
	 * Get all effects/animations
	 * @param em corresponding manager (only animation, musicsync, scene manager)
	 * @return a list of all effects or null if manager is not supported
	 */
	public List<? extends AbstractEffect> getEffects(EffectManager manager) {
		if(manager instanceof AnimationManager) {
			AnimationManager m = (AnimationManager) manager;
			return m.getAnimations();
		} else if(manager instanceof MusicSyncManager) {
			MusicSyncManager m = (MusicSyncManager) manager;
			return m.getMusicEffects();
		} else if(manager instanceof SceneManager) {
			SceneManager m = (SceneManager) manager;
			return m.getScenes();
		}
		return null;
	}
	
	/**
	 * Get all effect by name
	 * @param em corresponding manager (only animation, musicsync, scene manager)
	 * @return	the effect with the specified name or null if manager is not supported
	 * 			the effect was not found
	 */
	public <T extends EffectManager> AbstractEffect getEffect(T manager, String effectName) {
		List<? extends AbstractEffect> effects = getEffects(manager);
		if(effects != null) {
			for(AbstractEffect e : effects) {
				if(e.getName().equalsIgnoreCase(effectName))
					return e;
			}
		}
		return null;
	}
	
	/**
	 * Get active manager + effect as string
	 * @return string array of length 2; first arg is manager, second is effect or null
	 */
	public String[] getActiveManagerAndEffect() {
		EffectManager manager = getActiveManager();
		String[] out = new String[2];
		if(manager == null)
			return out;
		out[0] = manager.getName();
		
		if(manager instanceof AnimationManager) {
			AnimationManager m = (AnimationManager) manager;
			if(m.getActiveAnimation() != null)
				out[1] = m.getActiveAnimation().getName();
		} else if(manager instanceof MusicSyncManager) {
			MusicSyncManager m = (MusicSyncManager) manager;
			if(m.getActiveEffect() != null)
				out[1] = m.getActiveEffect().getName();
		} else if(manager instanceof SceneManager) {
			SceneManager m = (SceneManager) manager;
			if(m.getActiveScene() != null)
				out[1] = m.getActiveScene().getName();
		} else if(manager instanceof LuaManager) {
			LuaManager m = (LuaManager) manager;
			if(m.getActiveLuaScriptPath() != null)
				out[1] = m.getActiveLuaScriptPath();
		} else if(manager instanceof AbstractScreenColorManager) {
			out[1] = "ScreenColor";
		}
		
		return out;
	}
	
}
