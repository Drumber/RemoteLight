package de.lars.remotelightcore.musicsync.sound;

import java.util.Arrays;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HammingWindow;

/**
 * Mel Filter Bank implementation adapted from {@link be.tarsos.dsp.mfcc.MFCC}.
 */
public class MelFilter implements AudioProcessor {

	protected int amountOfMelFilters; // Number of mel filters
	protected float lowerFilterFreq; // lower limit of filter
	protected float upperFilterFreq; // upper limit of filter
	protected float preemphasisFactor = 0.97f;

	float[] audioFloatBuffer;
	private float[] filterBank;

	int centerFrequencies[];

	private FFT fft;
	private int samplesPerFrame;
	private float sampleRate;

	public MelFilter(int samplesPerFrame, float sampleRate) {
		this(samplesPerFrame, sampleRate, 60, 60, 10000);
	}

	public MelFilter(int samplesPerFrame, float sampleRate, int amountOfMelFilters, float lowerFilterFreq,
			float upperFilterFreq) {
		this.samplesPerFrame = samplesPerFrame;
		this.sampleRate = sampleRate;
		this.amountOfMelFilters = amountOfMelFilters + 1; // we remove the last mel in process()
		this.fft = new FFT(samplesPerFrame, new HammingWindow());

		this.lowerFilterFreq = Math.max(lowerFilterFreq, 25);
		this.upperFilterFreq = Math.min(upperFilterFreq, sampleRate / 2);
		calculateFilterBanks();
	}

	@Override
	public boolean process(AudioEvent audioEvent) {
		audioFloatBuffer = audioEvent.getFloatBuffer().clone();

		// Magnitude Spectrum
		float bin[] = magnitudeSpectrum(audioFloatBuffer);
		// get Mel Filterbank
		float fbank[] = melFilter(bin, centerFrequencies);

		filterBank = Arrays.copyOf(fbank, fbank.length - 1);

		return true;
	}

	@Override
	public void processingFinished() {
	}

	/**
	 * Computes the magnitude spectrum of the input frame<br>
	 * 
	 * @param frame Input frame signal
	 * @return Magnitude Spectrum array
	 */
	public float[] magnitudeSpectrum(float frame[]) {
		float magSpectrum[] = new float[frame.length];

		// calculate FFT for current frame

		fft.forwardTransform(frame);

		// calculate magnitude spectrum
		for (int k = 0; k < frame.length / 2; k++) {
			magSpectrum[frame.length / 2 + k] = fft.modulus(frame, frame.length / 2 - 1 - k);
			magSpectrum[frame.length / 2 - 1 - k] = magSpectrum[frame.length / 2 + k];
		}

		return magSpectrum;
	}

	/**
	 * Calculates the FFT bin indices<br>
	 */
	public final void calculateFilterBanks() {
		centerFrequencies = new int[amountOfMelFilters + 2];

		centerFrequencies[0] = Math.round(lowerFilterFreq / sampleRate * samplesPerFrame);
		centerFrequencies[centerFrequencies.length - 1] = (int) (samplesPerFrame / 2);

		double mel[] = new double[2];
		mel[0] = freqToMel(lowerFilterFreq);
		mel[1] = freqToMel(upperFilterFreq);

		float factor = (float) ((mel[1] - mel[0]) / (amountOfMelFilters + 1));
		// Calculates te centerfrequencies.
		for (int i = 1; i <= amountOfMelFilters; i++) {
			float fc = (inverseMel(mel[0] + factor * i) / sampleRate) * samplesPerFrame;
			centerFrequencies[i - 1] = Math.round(fc);
		}

	}

	/**
	 * Calculate the output of the mel filter
	 * 
	 * @param bin               The bins.
	 * @param centerFrequencies The frequency centers.
	 * @return Output of mel filter.
	 */
	public float[] melFilter(float bin[], int centerFrequencies[]) {
		float temp[] = new float[amountOfMelFilters + 2];

		for (int k = 1; k <= amountOfMelFilters; k++) {
			float num1 = 0, num2 = 0;

			float den = (centerFrequencies[k] - centerFrequencies[k - 1] + 1);

			for (int i = centerFrequencies[k - 1]; i <= centerFrequencies[k]; i++) {
				num1 += bin[i] * (i - centerFrequencies[k - 1] + 1);
			}
			num1 /= den;

			den = (centerFrequencies[k + 1] - centerFrequencies[k] + 1);

			for (int i = centerFrequencies[k] + 1; i <= centerFrequencies[k + 1]; i++) {
				num2 += bin[i] * (1 - ((i - centerFrequencies[k]) / den));
			}

			temp[k] = num1 + num2;
		}

		float fbank[] = new float[amountOfMelFilters];

		for (int i = 0; i < amountOfMelFilters; i++) {
			fbank[i] = temp[i + 1];
		}

		return fbank;
	}

	/**
	 * Convert frequency to mel-frequency<br>
	 * 
	 * @param freq Frequency
	 * @return Mel-Frequency
	 */
	protected static float freqToMel(float freq) {
		return (float) (2595 * log10(1 + freq / 700));
	}

	/**
	 * Calculates the inverse of Mel Frequency<br>
	 */
	private static float inverseMel(double x) {
		return (float) (700 * (Math.pow(10, x / 2595) - 1));
	}

	/**
	 * Calculates logarithm with base 10<br>
	 * 
	 * @param value Number to take the log of
	 * @return base 10 logarithm of the input values
	 */
	protected static float log10(float value) {
		return (float) (Math.log(value) / Math.log(10));
	}

	public int[] getCenterFrequencies() {
		return centerFrequencies;
	}

	public float[] getFilterBank() {
		return filterBank;
	}

}
