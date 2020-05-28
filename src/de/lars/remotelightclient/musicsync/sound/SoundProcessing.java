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
package de.lars.remotelightclient.musicsync.sound;

import javax.sound.sampled.*;

import org.tinylog.Logger;

import com.xtaudio.xt.*;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import be.tarsos.dsp.util.fft.FFT;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicSyncManager;
import de.lars.remotelightclient.musicsync.sound.nativesound.NativeSound;
import de.lars.remotelightclient.musicsync.sound.nativesound.NativeSoundFormat;
import de.lars.remotelightclient.musicsync.sound.nativesound.NativeSoundInputStream;
import de.lars.remotelightclient.ui.MainFrame.NotificationType;
import de.lars.remotelightclient.utils.maths.MathHelper;

/*
 * 
 * Adapted from the TarosDSP examples
 * 
 */

public class SoundProcessing implements PitchDetectionHandler {

	private MusicSyncManager manager;
	private PitchEstimationAlgorithm algo = PitchEstimationAlgorithm.DYNAMIC_WAVELET;
	private AudioDispatcher dispatcher;
	private Mixer mixer;
	private SilenceDetector silenceDetector;
	private int threshold = -100;
	
	private boolean useNativeSound = false;
	private XtSample bitRate = XtSample.INT16;
	private int xtServiceIndex = -1;
	private int xtDeviceIndex;
	private int channels = 2;

	private float sampleRate = 48000;
	private int bufferSize = 1024 * 2;
	private int overlap = 1024;
	//private int overlap = 768 * 2;

	public SoundProcessing(MusicSyncManager manager) {
		this.manager = manager;

		if (dispatcher != null) {
			dispatcher.stop();
		}
	}

	public void start() {
		if (dispatcher != null) {
			this.stop();
		}

		if(!useNativeSound)
			Logger.debug("Started listening with " + Shared.toLocalString(mixer.getMixerInfo().getName()));
		else
			Logger.debug("Started listening with native sound " + manager.getNativeSoundDevice().getName());
		
		try {

			if(!useNativeSound) {
				
				sampleRate = 41000;
				final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
				final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
				
				TargetDataLine line = (TargetDataLine) mixer.getLine(dataLineInfo);
				final int numberOfSamples = bufferSize;
				line.open(format, numberOfSamples);
				line.start();
				final AudioInputStream stream = new AudioInputStream(line);
	
				JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
				// create a new dispatcher
				dispatcher = new AudioDispatcher(audioStream, bufferSize, overlap);
				
			} else {
				
				UniversalAudioInputStream uais = null;
				try (XtAudio audio = new XtAudio(null, null, null, null)) {
					NativeSound nsound = manager.getNativeSound();
					XtService service = XtAudio.getServiceByIndex(xtServiceIndex);
					
					XtFormat xformat = new XtFormat(new XtMix((int)sampleRate, bitRate), channels, 0, 0, 0);
					NativeSoundFormat nformat = new NativeSoundFormat(xformat);
					NativeSoundInputStream nsis = new NativeSoundInputStream(nformat);
					
					nsound.openDevice(service, xtDeviceIndex, xformat, nsis);
					uais = new UniversalAudioInputStream(nsis.getInputStream(), NativeSoundInputStream.convertToTarosDSPFormat(nformat));
				}
				
				dispatcher = new AudioDispatcher(uais, bufferSize, overlap);
			}

			// add a processor
			dispatcher.addAudioProcessor(new PitchProcessor(algo, sampleRate, bufferSize, this));
			silenceDetector = new SilenceDetector(threshold, false);
			dispatcher.addAudioProcessor(silenceDetector);
			dispatcher.addAudioProcessor(fftProcessor);

			new Thread(dispatcher, "Audio dispatching").start();

		} catch (IllegalArgumentException | LineUnavailableException e) {
			if(!Main.isHeadless()) {
				Main.getInstance().getMainFrame().printNotification(
						"Input not supported! Please select a Mic or Line-In Input.", NotificationType.Error);
			}
			Logger.error(e, "Error while setting up input line!");
		}
	}

	public void stop() {
		if (dispatcher != null) {
			dispatcher.stop();
		}
		manager.getNativeSound().closeDevice();
		Logger.debug("Stopped SoundProcessor");
	}

	public boolean isActive() {
		if (dispatcher != null) {
			return !dispatcher.isStopped();
		}
		return false;
	}

	public void setMixer(Mixer mixer) {
		this.mixer = mixer;
	}

	public boolean isMixerSet() {
		return (mixer != null);
	}

	public void setNativeSoundEnabled(boolean enable) {
		useNativeSound = enable;
	}
	
	public boolean isNativeSoundEnabled() {
		return useNativeSound;
	}
	
	public void configureNativeSound(int serviceIndex, int deviceIndex, int sampleRate, XtSample bitrate, int channels) {
		this.xtServiceIndex = serviceIndex;
		this.xtDeviceIndex = deviceIndex;
		this.sampleRate = sampleRate;
		this.bitRate = bitrate;
		this.channels = channels;
	}
	
	public boolean isConfigured() {
		if(useNativeSound) {
			return xtServiceIndex != -1;
		}
		return isMixerSet();
	}
	
	
	@Override
	public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
		if (pitchDetectionResult.getPitch() != -1) {
			double timeStamp = audioEvent.getTimeStamp();
			float pitch = pitchDetectionResult.getPitch(); // what we need (Hz)
			float probability = pitchDetectionResult.getProbability();
			double rms = audioEvent.getRMS() * 100; // what we need (loudness)
			String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f )", timeStamp, pitch,
					probability, rms);
			// Logger.debug(message);

			manager.soundToLight(pitch, rms, timeStamp);
		}
	}

	public double getCurrentSPL() {
		double spl = silenceDetector.currentSPL();
		if (spl > threshold)
			return (spl + (threshold * (-1)));
		else
			return 0;
	}

	private int low1 = 0, low2 = 0, mid1 = 0, mid2 = 0, high1 = 0, high2 = 0;
	float[] amplitudes = new float[bufferSize / 2];

	AudioProcessor fftProcessor = new AudioProcessor() {
		FFT fft = new FFT(bufferSize);

		@Override
		public void processingFinished() {
		}

		@Override
		public boolean process(AudioEvent audioEvent) {
			float[] audioFloatBuffer = audioEvent.getFloatBuffer();
			float[] transformbuffer = new float[bufferSize * 2];
			System.arraycopy(audioFloatBuffer, 0, transformbuffer, 0, audioFloatBuffer.length);
			fft.forwardTransform(transformbuffer);
			fft.modulus(transformbuffer, amplitudes);
			
			// amplitude of 6 bands: ~280/~570/~850/~1120/~1400/>1400
			low1 = 0;
			low2 = 0;
			mid1 = 0;
			mid2 = 0;
			high1 = 0;
			high2 = 0;
			int z = 25; // steps

			int d = 0;
			for (int c = 1; c <= 6; c++) {

				for (int i = d; i < z * c; i++) {
					if (c == 1 && amplitudes[i] > low1) {
						low1 = (int) amplitudes[i];
					}
					if (c == 2 && amplitudes[i] > low2) {
						low2 = (int) amplitudes[i];
					}
					if (c == 3 && amplitudes[i] > mid1) {
						mid1 = (int) amplitudes[i];
					}
					if (c == 4 && amplitudes[i] > mid2) {
						mid2 = (int) amplitudes[i];
					}
					if (c == 5 && amplitudes[i] > high1) {
						high1 = (int) amplitudes[i];
					}
					if (c == 6 && amplitudes[i] > high2) {
						high2 = (int) amplitudes[i];
					}
				}

				d += z;
			}
			return true;
		}

	};

	public int[] getSimpleAmplitudes() {
		return new int[] { low1, low2, mid1, mid2, high1, high2 };
	}
	
	public float[] getAmplitudes() {
		return amplitudes;
	}
	
	public int hzToBin(double hz) {
		return (int) (hz * bufferSize / sampleRate);
	}
	
	public double binToHz(final int binIndex) {
		return binIndex * sampleRate / bufferSize;
	}
	
	/**
	 * Compute FFT data array
	 * @param fft array of amplitudes
	 * @param length amount of bands
	 * @param boost boost values, if not wanted set it to 0
	 * @return new array of specified length with int values between 0 and 255
	 */
	public int[] computeFFT(float[] fft, int length, double boost) {
		// adapted from a bass_wasapi sample
		int x, y;
		int b0 = 0;
		
		int[] data = new int[length];
		
		for(x = 0; x < length; x++) {
			float peak = 0;
			int b1 = (int) Math.pow(2, x*10.0 / (length - 1));
			if(b1 > 1023) b1 = 1023;
			if(b1 <= b0) b1 = b0 + 1; // make sure it uses at least 1 FFT bin
			
			for(; b0 < b1; b0++) {
				if(peak < fft[1 + b0]) peak = fft[1 + b0];
			}
			
			y = (int) (Math.sqrt(peak) * 3 * 2 - 4); // scale it
			
			// normalize values
			int max = 50;
			if(y > max) y = max;
			if(y < 0) y = 0;
			
			y = (int) MathHelper.map(y, 0, max, 0, 255);
			
			// boost values
			y =  (int) (y * (boost * 0.4));
			if(y > 255) y = 255;
			
			data[x] = y;
		}
		return data;
	}

}
