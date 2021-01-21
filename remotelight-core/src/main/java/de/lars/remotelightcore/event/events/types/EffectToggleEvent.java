package de.lars.remotelightcore.event.events.types;

import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.effect.AbstractEffect;
import de.lars.remotelightcore.event.events.Event;
import de.lars.remotelightcore.lua.LuaScript;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.scene.Scene;

/**
 * EffectToggleEvent called when enabling or disabling an effect.
 */
public abstract class EffectToggleEvent implements Event {
	
	public enum Action {
		ENABLE, DISABLE
	}
	
	private final Action action;
	
	public EffectToggleEvent(Action action) {
		this.action = action;
	}
	
	/**
	 * Get the event action
	 * @return		{@link Action#ENABLE} on starting the effect<br>
	 * 				{@link Action#DISABLE} on stopping the effect
	 */
	public Action getAction() {
		return action;
	}
	
	/**
	 * Get the event effect. This could also return {@code null} if
	 * the effect is not available (e.g. on disable)
	 * @return		instance of the effect or {@code null}
	 */
	public abstract AbstractEffect getEffect();
	
	@Override
	public String toString() {
		return String.format("%s[action=%s,effect=%s]",
				getClass().getCanonicalName(), String.valueOf(getAction()), String.valueOf(getEffect()));
	}
	
	
	/*---------------------------------
	 * Subclasses for each effect type
	 *---------------------------------*/
	
	public static class AnimationToggleEvent extends EffectToggleEvent {
		private final Animation animation;
		
		public AnimationToggleEvent(Action action, Animation animation) {
			super(action);
			this.animation = animation;
		}

		@Override
		public Animation getEffect() {
			return animation;
		}
	}
	
	public static class MusicSyncToggleEvent extends EffectToggleEvent {
		private final MusicEffect musicEffect;
		
		public MusicSyncToggleEvent(Action action, MusicEffect musicEffect) {
			super(action);
			this.musicEffect = musicEffect;
		}

		@Override
		public MusicEffect getEffect() {
			return musicEffect;
		}
	}
	
	public static class SceneToggleEvent extends EffectToggleEvent {
		private final Scene scene;
		
		public SceneToggleEvent(Action action, Scene scene) {
			super(action);
			this.scene = scene;
		}
		
		@Override
		public Scene getEffect() {
			return scene;
		}
	}
	
	public static class LuaScriptToggleEvent extends EffectToggleEvent {
		private final LuaScript luaScript;
		
		public LuaScriptToggleEvent(Action action, LuaScript luaScript) {
			super(action);
			this.luaScript = luaScript;
		}
		
		/**
		 * Get the event lua script
		 * @return		{@link LuaScript} instance
		 */
		@Override
		public LuaScript getEffect() {
			return luaScript;
		}
	}

}
