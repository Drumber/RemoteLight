package de.lars.remotelightclient.musicsync.fft;

import javax.sound.sampled.TargetDataLine;

/*
 * FFT Class by...
 * Copyright © Moritz Lehmann 2014-2017
 * Contact: moritz.lehmann@uni-bayreuth.de
 * 
 */

public class FFT {

	private int sample_rate = 48000;
	private int sample_size = 8192;
	private int bandwidth = 5000;
	private int multiplier = 12;

	public FFT(TargetDataLine targetLine) {
		byte[] buffer = new byte[targetLine.getBufferSize()];

		double[] wave = new double[buffer.length / 2];
		Complex[] c = new Complex[buffer.length / 2];
		double[] freq = new double[c.length * this.bandwidth / this.sample_rate];

	}

}
