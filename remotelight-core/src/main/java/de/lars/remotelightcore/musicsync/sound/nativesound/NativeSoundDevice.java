package de.lars.remotelightcore.musicsync.sound.nativesound;

import java.io.Serializable;

import com.xtaudio.xt.*;

public class NativeSoundDevice implements Serializable {
	private static final long serialVersionUID = -8566436981838708790L;

	/** device exists and is supported */
	private boolean valid = false;
	
	private int serviceIndex;
	private int deviceIndex;
	private int sampleRate;
	private int bitrate;
	private int channels;
	
	private String name;
	
	public NativeSoundDevice(int serviceIndex, int deviceIndex, int samplerate, int bitrate, int channels) {
		this(serviceIndex, deviceIndex, samplerate, bitrate, channels, false);
	}
	
	public NativeSoundDevice(int serviceIndex, int deviceIndex, int samplerate, int bitrate, int channels, boolean checkValidity) {
		this.serviceIndex = serviceIndex;
		this.deviceIndex = deviceIndex;
		this.sampleRate = samplerate;
		this.bitrate = bitrate;
		this.channels = channels;
		if(checkValidity)
			this.checkValidity();
	}
	
	public boolean checkValidity() {
		valid = false;
		NativeSound sound = new NativeSound(false);
		try (XtAudio audio = new XtAudio(null, null, null, null)) {
			XtService service = XtAudio.getServiceByIndex(serviceIndex);
			XtFormat format = new XtFormat(new XtMix(sampleRate, NativeSoundFormat.bitrateToSample(bitrate)), channels, 0, 0, 0);
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

	public int getBitrate() {
		return bitrate;
	}
	
	public XtSample getBitrateXtSample() {
		return NativeSoundFormat.bitrateToSample(bitrate);
	}

	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
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
