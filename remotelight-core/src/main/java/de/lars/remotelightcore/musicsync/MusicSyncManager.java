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

package de.lars.remotelightcore.musicsync;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

import org.tinylog.Logger;

import de.lars.remotelightcore.EffectManager;
import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.modes.*;
import de.lars.remotelightcore.musicsync.sound.Shared;
import de.lars.remotelightcore.musicsync.sound.SoundProcessing;
import de.lars.remotelightcore.musicsync.sound.nativesound.NativeSound;
import de.lars.remotelightcore.musicsync.sound.nativesound.NativeSoundDevice;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingObject;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class MusicSyncManager extends EffectManager {
	
	/** should the native sound library be loaded */
	public static boolean initNativeSound = true;
	
	private SettingsManager sm;
	private MusicEffect activeEffect;
	private List<MusicEffect> effects;
	private String input;
	private boolean active = false;
	private MusicSyncUtils musicUtils;
	private SoundProcessing soundProcessor;
	private NativeSound nativeSound;
	private NativeSoundDevice nativeSoundDevice;
	
	private int delay = 20;
	private double sensitivity = 1;
	private double adjustment = 3;
	private double volume;
	private float pitch;
	private double pitchTime;
	
	public MusicSyncManager() {
		sm = RemoteLightCore.getInstance().getSettingsManager();
		
		if(nativeSound != null && nativeSound.isInitialized())
			nativeSound.close();
		nativeSound = new NativeSound(initNativeSound);
		
		if(soundProcessor != null) {
			soundProcessor.stop();
		}
		soundProcessor = new SoundProcessing(this);
		
		this.loadSettings();
		
		musicUtils = new MusicSyncUtils();
		
		effects = new ArrayList<MusicEffect>();
		this.registerMusicEffects();
	}
	
	@Override
	public String getName() {
		return "MusicSyncManager";
	}
	
	private void loadSettings() {
		sm.addSetting(new SettingObject("musicsync.input", "Input", null));
		sm.addSetting(new SettingObject("nativesound.serviceindex", "Selected service index", -1));
		sm.addSetting(new SettingObject("nativesound.deviceindex", "Selected device index", 0));
		sm.addSetting(new SettingObject("nativesound.samplerate", "Samplerate", 48000));
		sm.addSetting(new SettingObject("nativesound.bitrate", "Bitrate", 16));
		sm.addSetting(new SettingObject("nativesound.channels", "Channels", 2));
		
		// load native sound device if configured
		if(nativeSound.isInitialized() && isNativeSoundConfigured()) {
			nativeSoundDevice = buildNativeSoundDevice();
			// check if device is still valid / supported
			if(!nativeSoundDevice.checkValidity()) {
				sm.getSettingObject("nativesound.serviceindex").setValue(-1);
			} else {
				configureSoundProcessorForNativeSound(nativeSoundDevice);
			}
		}
		
		// load last used input
		input = (String) sm.getSettingObject("musicsync.input").getValue();
		if(input != null) {
			if(!input.equals("_NativeSound_")) {
				// Java sound api
				for(Mixer.Info info : Shared.getMixerInfo(false, true)) {
					if(input.equals(info.toString())){
						Mixer newValue = AudioSystem.getMixer(info);
						soundProcessor.setMixer(newValue);
						soundProcessor.setNativeSoundEnabled(false);
						break;
					}
				}
			} else if(nativeSound.isInitialized() && isNativeSoundConfigured()) {
				// XtAudio native lib
				soundProcessor.setNativeSoundEnabled(true);
			}
		}
	}
	
	public void newSoundProcessor() {
		if(soundProcessor != null) {
			soundProcessor.start();
		}
	}
	
	public SoundProcessing getSoundProcessor() {
		return soundProcessor;
	}
	
	public NativeSound getNativeSound() {
		return nativeSound;
	}
	
	public boolean isNativeSoundConfigured() {
		return (int) sm.getSettingObject("nativesound.serviceindex").getValue() != -1 && nativeSound.isInitialized();
	}
	
	public void setNativeSoundEnabled(boolean enable) {
		if(nativeSound.isInitialized() && isNativeSoundConfigured() && enable) {
			// update NativeSoundDevice
			nativeSoundDevice = buildNativeSoundDevice();
			if(nativeSoundDevice.checkValidity()) {
				if(soundProcessor.isActive() && soundProcessor.isNativeSoundEnabled())
					return;
				soundProcessor.setNativeSoundEnabled(true);
				configureSoundProcessorForNativeSound(nativeSoundDevice);
			} else {
				soundProcessor.setNativeSoundEnabled(false);
				sm.getSettingObject("nativesound.serviceindex").setValue(-1);
			}
		} else {
			if(soundProcessor.isActive() && soundProcessor.isNativeSoundEnabled()) {
				soundProcessor.stop();
			}
			soundProcessor.setNativeSoundEnabled(false);
		}
	}
	
	private void configureSoundProcessorForNativeSound(NativeSoundDevice nativeSoundDevice) {
		if(nativeSound.isInitialized()) {
			soundProcessor.configureNativeSound(nativeSoundDevice.getServiceIndex(), nativeSoundDevice.getDeviceIndex(),
					nativeSoundDevice.getSampleRate(), nativeSoundDevice.getBitrateXtSample(), nativeSoundDevice.getChannels());
		}
	}
	
	/**
	 * Build NativeSoundDevice from current setting values
	 * @return new NativeSoundDevice instance
	 */
	public NativeSoundDevice buildNativeSoundDevice() {
		int serviceIndex = (int) sm.getSettingObject("nativesound.serviceindex").getValue();
		int deviceIndex = (int) sm.getSettingObject("nativesound.deviceindex").getValue();
		int sampleRate = (int) sm.getSettingObject("nativesound.samplerate").getValue();
		int bitrate = (int) sm.getSettingObject("nativesound.bitrate").getValue();
		int channels = (int) sm.getSettingObject("nativesound.channels").getValue();
		return new NativeSoundDevice(serviceIndex, deviceIndex, sampleRate, bitrate, channels);
	}
	
	public NativeSoundDevice getNativeSoundDevice() {
		return nativeSoundDevice;
	}
	
	public void updateNativeSoundDevice() {
		if(nativeSound.isInitialized() && isNativeSoundConfigured()) {
			nativeSoundDevice = buildNativeSoundDevice();
			nativeSoundDevice.checkValidity();
		}
	}
	
	
	public void soundToLight(float pitch, double rms, double time) {
		this.volume = rms;
		this.pitch = pitch;
		this.pitchTime = time;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public int getDelay() {
		return delay;
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
	
	@Override
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
		SettingsManager sm = RemoteLightCore.getInstance().getSettingsManager();
		List<Setting> tmp = new ArrayList<>();
		for(String s : getActiveEffect().getOptions()) {
			tmp.add(sm.getSettingFromId(s));
		}
		return tmp;
	}
	
	public void start(MusicEffect effect) {
		RemoteLightCore.getInstance().getEffectManagerHelper().stopAllExceptFor(EffectType.MusicSync);
		
		if(soundProcessor == null) {
			soundProcessor = new SoundProcessing(this);
		}
		if(!soundProcessor.isMixerSet() && !soundProcessor.isNativeSoundEnabled()) {
			//Main.getInstance().getMainFrame().printNotification("Please select a input!", NotificationType.Error);
			// TODO message system
			return;
		}
		if(!soundProcessor.isConfigured()) {
			//Main.getInstance().getMainFrame().printNotification("Sound input not configured!", NotificationType.Error);
			// TODO message system
			return;
		}
		if(soundProcessor.isNativeSoundEnabled()) {
			configureSoundProcessorForNativeSound(nativeSoundDevice);
		}
		if(activeEffect != null) {
			activeEffect.onDisable();
		}
		if(!soundProcessor.isActive()) {
			soundProcessor.start();
		}
		if(effect != null) {
			effect.setSoundProcessor(soundProcessor);
			effect.onEnable();
		} else {
			soundProcessor.stop();
		}
		activeEffect = effect;
		this.loop();
	}
	
	@Override
	public void stop() {
		if(activeEffect != null) {
			activeEffect.onDisable();
		}
		activeEffect = null;
		soundProcessor.stop();
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
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
						
						try {
							
						activeEffect.onLoop();
						
						} catch(Exception e) {
							Logger.error(e, "There was an error executing the MusicEffect '" + activeEffect.getDisplayname() + "'.");
							// stop on exception
							stop();
							break;
						}
						
						try {
							Thread.sleep(delay);
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
		effects.add(new Visualizer());
		effects.add(new DancingPoints());
		effects.add(new Energy());
		effects.add(new Strobe());
		effects.add(new Flame());
		effects.add(new Bars());
		effects.add(new RainbowNoise());
		effects.add(new Lines());
	}

}
