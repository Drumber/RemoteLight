/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/


package de.lars.remotelightclient.musicsync.tarosdsp;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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
import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.musicsync.MusicSync;

public class PitchDetector extends JFrame implements PitchDetectionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3501426880288136245L;

	private final JTextArea textArea;

	private AudioDispatcher dispatcher;
	private Mixer currentMixer;
	private static boolean mixerSet = false;
	private static SilenceDetector silenceDetector;
	private static int threshold = -100;
	
	
	private PitchEstimationAlgorithm algo;	
	private ActionListener algoChangeListener = new ActionListener(){
		@Override
		public void actionPerformed(final ActionEvent e) {
			String name = e.getActionCommand();
			PitchEstimationAlgorithm newAlgo = PitchEstimationAlgorithm.valueOf(name);
			algo = newAlgo;
			try {
				setNewMixer(currentMixer);
			} catch (LineUnavailableException e1) {
				e1.printStackTrace();
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			}
	}};

	public PitchDetector() {
		this.setLayout(new GridLayout(0, 1));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Pitch Detector by Joren Six");
		
		JPanel inputPanel = new InputPanel();
		add(inputPanel);
		inputPanel.addPropertyChangeListener("mixer",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent arg0) {
						try {
							setNewMixer((Mixer) arg0.getNewValue());
						} catch (LineUnavailableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedAudioFileException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		
		algo = PitchEstimationAlgorithm.DYNAMIC_WAVELET;
		
		JPanel pitchDetectionPanel = new PitchDetectionPanel(algoChangeListener);
		
		add(pitchDetectionPanel);
	
		textArea = new JTextArea();
		textArea.setEditable(false);
		add(new JScrollPane(textArea));
		
		String data = (String) DataStorage.getData(DataStorage.SOUND_INPUT_STOREKEY);
		if(data != null) {
			for(Mixer.Info info : Shared.getMixerInfo(false, true)){
				if(data.equals(info.toString())){
					Mixer newValue = AudioSystem.getMixer(info);
					try {
						setNewMixer(newValue);
					} catch (LineUnavailableException e) {
						e.printStackTrace();
					} catch (UnsupportedAudioFileException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}


	private float sampleRate = 44100;
	private int bufferSize = 1024 * 4;
	private int overlap = 768 * 4;
	
	private void setNewMixer(Mixer mixer) throws LineUnavailableException,
			UnsupportedAudioFileException {
		
		if(dispatcher!= null){
			dispatcher.stop();
		}
		currentMixer = mixer;
		mixerSet = true;
		
		textArea.append("Started listening with " + Shared.toLocalString(mixer.getMixerInfo().getName()) + "\n");

		final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
		final DataLine.Info dataLineInfo = new DataLine.Info(
				TargetDataLine.class, format);
		TargetDataLine line;
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
	}


	@Override
	public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) {
		if(pitchDetectionResult.getPitch() != -1){
			double timeStamp = audioEvent.getTimeStamp();
			float pitch = pitchDetectionResult.getPitch(); //what we need (Hz)
			float probability = pitchDetectionResult.getProbability();
			double rms = audioEvent.getRMS() * 100; //what we need (loudness)
			String message = String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f )\n", timeStamp,pitch,probability,rms);
			textArea.append(message);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			
			MusicSync.soundToLight(pitch, rms, timeStamp);
		}
	}
	
	public static double getCurrentSPL() {
		double spl = silenceDetector.currentSPL();
		if(spl > threshold) return (spl + (threshold * (-1)));
		else return 0;
	}
	
	
	private static int low1 = 0, low2 = 0, mid1 = 0, mid2 = 0, high1 = 0, high2 = 0;
	
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
	
	public static int[] getAmplitudes() {
		return new int[] {low1, low2, mid1, mid2, high1, high2};
	}
	
	public static boolean isMixerSet() {
		return mixerSet;
	}
	
}
