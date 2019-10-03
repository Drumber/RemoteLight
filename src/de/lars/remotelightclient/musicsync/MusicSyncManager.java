package de.lars.remotelightclient.musicsync;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.EffectManager.EffectType;
import de.lars.remotelightclient.musicsync.modes.Bump;
import de.lars.remotelightclient.musicsync.modes.EQ;
import de.lars.remotelightclient.musicsync.modes.Fade;
import de.lars.remotelightclient.musicsync.modes.LevelBar;
import de.lars.remotelightclient.musicsync.modes.Pulse;
import de.lars.remotelightclient.musicsync.modes.Rainbow;
import de.lars.remotelightclient.musicsync.modes.RunningLight;
import de.lars.remotelightclient.musicsync.sound.Shared;
import de.lars.remotelightclient.musicsync.sound.SoundProcessing;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class MusicSyncManager {
	
	private MusicEffect activeEffect;
	private List<MusicEffect> effects;
	private String input;
	private boolean active = false;
	private MusicSyncUtils musicUtils;
	private SoundProcessing soundProcessor;
	private double sensitivity = 1;
	private double adjustment = 3;
	private double volume;
	private float pitch;
	private double pitchTime;
	
	public MusicSyncManager() {
		this.loadSettings();
		
		if(SoundProcessing.isMixerSet()) {
			if(soundProcessor != null) {
				soundProcessor.stop();
			}
			soundProcessor = new SoundProcessing(this);
		}
		
		musicUtils = new MusicSyncUtils();
		
		effects = new ArrayList<MusicEffect>();
		this.registerMusicEffects();
	}
	
	private void loadSettings() {
		SettingsManager s = Main.getInstance().getSettingsManager();
		s.addSetting(new SettingObject("musicsync.input", "Input", null));
		
		//select last used input
		input = (String) s.getSettingObject("musicsync.input").getValue();
		if(input != null) {
			for(Mixer.Info info : Shared.getMixerInfo(false, true)){
				if(input.equals(info.toString())){
					Mixer newValue = AudioSystem.getMixer(info);
					SoundProcessing.setMixer(newValue);
					break;
				}
			}
		}
	}
	
	public void newSoundProcessor() {
		if(soundProcessor != null) {
			soundProcessor.start();
		}
	}
	
	public void soundToLight(float pitch, double rms, double time) {
		this.volume = rms;
		this.pitch = pitch;
		this.pitchTime = time;
	}
	
	public void setSensitivity(double sensitivity) {
		this.sensitivity = sensitivity;
	}
	
	public double getSensitivity() {
		return sensitivity;
	}
	
	public void setAdjustment(double adjustment) {
		this.adjustment = adjustment;
	}
	
	public double getAdjustment() {
		return adjustment;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public MusicEffect getActiveEffect() {
		return activeEffect;
	}
	
	public List<MusicEffect> getMusicEffects() {
		return this.effects;
	}
	
	public List<Setting> getCurrentMusicEffectOptions() {
		SettingsManager sm = Main.getInstance().getSettingsManager();
		List<Setting> tmp = new ArrayList<>();
		for(String s : getActiveEffect().getOptions()) {
			tmp.add(sm.getSettingFromId(s));
		}
		return tmp;
	}
	
	public void start(MusicEffect effect) {
		Main.getInstance().getEffectManager().stopAllExceptFor(EffectType.MusicSync);
		if(activeEffect != null) {
			activeEffect.onDisable();
		}
		if(!soundProcessor.isActive()) {
			soundProcessor.start();
		}
		if(effect != null) {
			effect.onEnable();
		} else {
			soundProcessor.stop();
		}
		activeEffect = effect;
		this.loop();
	}
	
	public void stop() {
		if(activeEffect != null) {
			activeEffect.onDisable();
		}
		activeEffect = null;
		soundProcessor.stop();
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum()));
	}
	
	private void loop() {
		if(!active) {
			active = true;
			Logger.info("Starting MusicSync Thread.");
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(activeEffect != null) {
						musicUtils.process(pitch, volume, pitchTime, sensitivity, soundProcessor);
						volume = musicUtils.getVolume();
						activeEffect.setBump(musicUtils.isBump());
						activeEffect.setVolume(volume);
						activeEffect.setPitch(pitch);
						activeEffect.setPitchTime(pitchTime);
						activeEffect.setSoundProcessor(soundProcessor);
						activeEffect.setSensitivity(sensitivity);
						activeEffect.setAdjustment(adjustment);
						activeEffect.setMaxSpl(musicUtils.getMaxSpl());
						activeEffect.setMinSpl(musicUtils.getMinSpl());
						activeEffect.setSpl(musicUtils.getSpl());
						
						activeEffect.onLoop();
						
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					active = false;
					Logger.info("Stopped MusicSync Thread.");
				}
			}, "MusicSync loop").start();
		}
	}
	
	private void registerMusicEffects() {
		effects.add(new LevelBar());
		effects.add(new Rainbow());
		effects.add(new RunningLight());
		effects.add(new Bump());
		effects.add(new EQ());
		effects.add(new Fade());
		effects.add(new Pulse());
	}

}
