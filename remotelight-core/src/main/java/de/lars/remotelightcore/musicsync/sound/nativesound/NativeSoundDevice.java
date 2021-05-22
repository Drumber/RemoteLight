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

package de.lars.remotelightcore.musicsync.sound.nativesound;

import java.io.Serializable;

import com.xtaudio.xt.XtAudio;
import com.xtaudio.xt.XtDevice;
import com.xtaudio.xt.XtFormat;
import com.xtaudio.xt.XtMix;
import com.xtaudio.xt.XtSample;
import com.xtaudio.xt.XtService;

public class NativeSoundDevice implements Serializable {
	private static final long serialVersionUID = -8566436981838708790L;

	/** device exists and is supported */
	private boolean valid = false;
	
	private int serviceIndex;
	private int deviceIndex;
	private int sampleRate;
	private int bitDepth;
	private int channels;
	
	private String name;
	
	public NativeSoundDevice(int serviceIndex, int deviceIndex, int samplerate, int bitDepth, int channels) {
		this(serviceIndex, deviceIndex, samplerate, bitDepth, channels, false);
	}
	
	public NativeSoundDevice(int serviceIndex, int deviceIndex, int samplerate, int bitDepth, int channels, boolean checkValidity) {
		this.serviceIndex = serviceIndex;
		this.deviceIndex = deviceIndex;
		this.sampleRate = samplerate;
		this.bitDepth = bitDepth;
		this.channels = channels;
		if(checkValidity)
			this.checkValidity();
	}
	
	public boolean checkValidity() {
		valid = false;
		NativeSound sound = new NativeSound(false);
		try (XtAudio audio = new XtAudio(null, null, null, null)) {
			XtService service = XtAudio.getServiceByIndex(serviceIndex);
			XtFormat format = new XtFormat(new XtMix(sampleRate, NativeSoundFormat.bitDepthToSample(bitDepth)), channels, 0, 0, 0);
			valid = sound.isDeviceSupported(service, deviceIndex, format);
			// set name
			try(XtDevice device = service.openDevice(deviceIndex)) {
				name = device.getName();
			}
		}
		return valid;
	}

	public int getServiceIndex() {
		return serviceIndex;
	}

	public void setServiceIndex(int serviceIndex) {
		this.serviceIndex = serviceIndex;
	}

	public int getDeviceIndex() {
		return deviceIndex;
	}

	public void setDeviceIndex(int deviceIndex) {
		this.deviceIndex = deviceIndex;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public int getBitDepth() {
		return bitDepth;
	}
	
	public XtSample getBitDepthXtSample() {
		return NativeSoundFormat.bitDepthToSample(bitDepth);
	}

	public void setBitDepth(int bitDepth) {
		this.bitDepth = bitDepth;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public boolean isValid() {
		return valid;
	}
	
	public String getName() {
		return name;
	}

}
