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

import de.lars.remotelightcore.Effect;
import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.event.events.types.EffectOptionsUpdateEvent;
import de.lars.remotelightcore.musicsync.sound.SoundProcessing;

public class MusicEffect extends Effect {
	
	private boolean bump;
	private float pitch;
	private double pitchTime;
	private double volume;
	private SoundProcessing soundProcessor;
	private double sensitivity;
	private double adjustment;
	private double maxSpl, minSpl, spl;
	
	/**
	 * Create a new music visualizer effect
	 * 
	 * @param name	the name of the effect (should be unique)
	 */
	public MusicEffect(String name) {
		super(name);
	}
	
	
	/**
	 * Trigger an {@link EffectOptionsUpdateEvent}
	 */
	public void updateEffectOptions() {
		RemoteLightCore.getInstance().getEventHandler().call(new EffectOptionsUpdateEvent(EffectType.MusicSync));
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

	//TODO add onRender()

}
