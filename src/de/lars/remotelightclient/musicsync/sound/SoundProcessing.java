package de.lars.remotelightclient.musicsync.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import be.tarsos.dsp.util.fft.FFT;
import de.lars.remotelightclient.musicsync.MusicSync;
import de.lars.remotelightclient.musicsync.tarosdsp.Shared;

public class SoundProcessing implements PitchDetectionHandler {
	
	private MusicSync musicSync;
	private InputFrame gui;
	private PitchEstimationAlgorithm algo = PitchEstimationAlgorithm.DYNAMIC_WAVELET;
	private AudioDispatcher dispatcher;
	private static Mixer mixer;
	private SilenceDetector silenceDetector;
	private int threshold = -100;
	
	private float sampleRate = 44100;
	private int bufferSize = 1024 * 4;
	private int overlap = 768 * 4;
	
	
	public SoundProcessing(InputFrame inputFrame, MusicSync musicSync) {
		System.out.println("New SoundProcessor");
		this.musicSync = musicSync;
		gui = inputFrame;
		
		if(dispatcher!= null){
			dispatcher.stop();
		}
		
		gui.addText("Started listening with " + Shared.toLocalString(mixer.getMixerInfo().getName()) + "\n");

		final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
		final DataLine.Info dataLineInfo = new DataLine.Info(
				TargetDataLine.class, format);
		TargetDataLine line;
		try {
			
			line = (TargetDataLine) mixer.getLine(dataLineInfo);
			final int numberOfSamples = bufferSize;
			line.open(format, numberOfSamples);
			line.start();
			final AudioInputStream stream = new AudioInputStream(line);

			JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
			// create a new dispatcher
			dispatcher = new AudioDispatcher(audioStream, bufferSize, overlap);

			// add a processor
			dispatcher.addAudioProcessor(new PitchProcessor(algo, sampleRate, bufferSize, this));
			silenceDetector = new SilenceDetector(threshold, false);
			dispatcher.addAudioProcessor(silenceDetector);
			dispatcher.addAudioProcessor(fftProcessor);
			
			new Thread(dispatcher,"Audio dispatching").start();
			
		} catch (LineUnavailableException e) {
			gui.addText("\u25b6" + " ERROR: Input not supported! Please select a Mic or Line-In Input.");
			e.printStackTrace();
		}
	}
	
	public void stop() {
		System.out.println("Stop SoundProcessor");
		if(dispatcher != null)
			dispatcher.stop();
	}
	
	public static void setMixer(Mixer mixer) {
		SoundProcessing.mixer = mixer;
	}
	
	public static boolean isMixerSet() {
		return (mixer != null);
	}
	
	
	@Override
	public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) {
		if(pitchDetectionResult.getPitch() != -1){
			double timeStamp = audioEvent.getTimeStamp();
			float pitch = pitchDetectionResult.getPitch(); //what we need (Hz)
			float probability = pitchDetectionResult.getProbability();
			double rms = audioEvent.getRMS() * 100; //what we need (loudness)
			String message = String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f )\n", timeStamp,pitch,probability,rms);
			gui.addText(message);
			
			musicSync.soundToLight(pitch, rms, timeStamp);
		}
	}
	
	public double getCurrentSPL() {
		double spl = silenceDetector.currentSPL();
		if(spl > threshold) return (spl + (threshold * (-1)));
		else return 0;
	}
	
	
	private int low1 = 0, low2 = 0, mid1 = 0, mid2 = 0, high1 = 0, high2 = 0;
	
	AudioProcessor fftProcessor = new AudioProcessor(){
		
		FFT fft = new FFT(bufferSize);
		float[] amplitudes = new float[bufferSize/2];

		@Override
		public void processingFinished() {
			// TODO Auto-generated method stub
		}

		@Override
		public boolean process(AudioEvent audioEvent) {
			float[] audioFloatBuffer = audioEvent.getFloatBuffer();
			float[] transformbuffer = new float[bufferSize*2];
			System.arraycopy(audioFloatBuffer, 0, transformbuffer, 0, audioFloatBuffer.length); 
			fft.forwardTransform(transformbuffer);
			fft.modulus(transformbuffer, amplitudes);
			
			//amplitude of 6 bands: ~280/~570/~850/~1120/~1400/>1400
			low1 = 0; low2 = 0; mid1 = 0; mid2 = 0; high1 = 0; high2 = 0;
			int z = 25; //steps
			
			int d = 0;
			for(int c = 1; c <= 6; c++) {
				
				for(int i = d; i < z * c; i++) {
					if(c == 1 && amplitudes[i] > low1) {
						low1 = (int) amplitudes[i];
					}
					if(c == 2 && amplitudes[i] > low2) {
						low2 = (int) amplitudes[i];
					}
					if(c == 3 && amplitudes[i] > mid1) {
						mid1 = (int) amplitudes[i];
					}
					if(c == 4 && amplitudes[i] > mid2) {
						mid2 = (int) amplitudes[i];
					}
					if(c == 5 && amplitudes[i] > high1) {
						high1 = (int) amplitudes[i];
					}
					if(c == 6 && amplitudes[i] > high2) {
						high2 = (int) amplitudes[i];
					}
				}
				
				d += z;
			}
			
			//System.out.println(low1 + " | " + low2 + " | " + mid1 + " | " + mid2 + " | " + high1 + " | " + high2);
			
			return true;
		}
		
	};
	
	public int[] getAmplitudes() {
		return new int[] {low1, low2, mid1, mid2, high1, high2};
	}

}
