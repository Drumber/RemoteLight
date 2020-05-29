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
package de.lars.remotelightcore.musicsync;

import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightcore.musicsync.sound.SoundProcessing;

public class MusicEffect {
	
	private String name;
	private String displayname;
	private List<String> options;
	private boolean bump;
	private float pitch;
	private double pitchTime;
	private double volume;
	private SoundProcessing soundProcessor;
	private double sensitivity;
	private double adjustment;
	private double maxSpl, minSpl, spl;
	
	
	public MusicEffect(String name) {
		this.name = name;
		this.displayname = name; //TODO Language system
		this.options = new ArrayList<String>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	
	/**
	 * @param id ID of the setting
	 */
	public void addOption(String id) {
		this.options.add(id);
	}
	
	public List<String> getOptions() {
		return this.options;
	}
	
	public boolean isBump() {
		return bump;
	}

	public void setBump(boolean bump) {
		this.bump = bump;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public double getPitchTime() {
		return pitchTime;
	}

	public void setPitchTime(double pitchTime) {
		this.pitchTime = pitchTime;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	public SoundProcessing getSoundProcessor() {
		return soundProcessor;
	}

	public void setSoundProcessor(SoundProcessing soundProcessor) {
		this.soundProcessor = soundProcessor;
	}

	public double getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(double sensitivity) {
		this.sensitivity = sensitivity;
	}
	
	public void setAdjustment(double adjustment) {
		this.adjustment = adjustment;
	}
	
	public double getAdjustment() {
		return adjustment;
	}

	public double getMaxSpl() {
		return maxSpl;
	}

	public void setMaxSpl(double maxSpl) {
		this.maxSpl = maxSpl;
	}

	public double getMinSpl() {
		return minSpl;
	}

	public void setMinSpl(double minSpl) {
		this.minSpl = minSpl;
	}

	public double getSpl() {
		return spl;
	}

	public void setSpl(double spl) {
		this.spl = spl;
	}

	public void onEnable() {}
	public void onDisable() {}
	public void onLoop() {}

	//TODO add onRender()

}
