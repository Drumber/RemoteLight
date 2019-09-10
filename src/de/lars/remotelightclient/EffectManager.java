package de.lars.remotelightclient;

import java.awt.Color;

import de.lars.remotelightclient.animation.AnimationManager;
import de.lars.remotelightclient.musicsync.MusicSyncManager;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.scene.SceneManager;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class EffectManager {
	
	private AnimationManager am;
	private MusicSyncManager msm;
	private SceneManager sm;
	
	public enum EffectType {
		Animation, Scene, MusicSync, ScreenColor
	}
	
	public EffectManager() {
		Main main = Main.getInstance();
		am = main.getAnimationManager();
		msm = main.getMusicSyncManager();
		sm = main.getSceneManager();
	}
	
	public void stopAll() {
		if(am.isActive())
			am.stop();
		if(msm.isActive())
			msm.stop();
		if(sm.isActive())
			sm.stop();
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
		if(type != EffectType.ScreenColor) {
			//TODO
		}
	}

}
