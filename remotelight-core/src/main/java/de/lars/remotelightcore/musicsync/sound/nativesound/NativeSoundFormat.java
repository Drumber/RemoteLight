/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Drumber
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

import javax.sound.sampled.AudioFormat.Encoding;

import com.xtaudio.xt.XtFormat;
import com.xtaudio.xt.XtSample;

public class NativeSoundFormat {
	
	private XtFormat format;
	
	public NativeSoundFormat(XtFormat format) {
		this.format = format;
	}
	
	public XtFormat getXtFormat() {
		return format;
	}
	
	public int getSampleRate() {
		return format.mix.rate;
	}
	
	public Encoding getSampleFormat() {
		return getSampleFormat(format.mix.sample);
	}
	
	public static Encoding getSampleFormat(XtSample sample) {
		Encoding e;
		switch (sample) {
			case UINT8:
				e = Encoding.PCM_UNSIGNED;
				break;
			case INT16:
			case INT24:
			case INT32:
				e = Encoding.PCM_SIGNED;
				break;
			case FLOAT32:
				e = Encoding.PCM_FLOAT;
				break;
			default:
				e = Encoding.PCM_SIGNED;
		}
		return e;
	}
	
	public static XtSample bitrateToSample(int bitrate) {
		switch (bitrate) {
			case 8:
				return XtSample.UINT8;
			case 16:
				return XtSample.INT16;
			case 24:
				return XtSample.INT24;
			case 32:
				return XtSample.INT32;
			default:
				return XtSample.INT16;
		}
	}
	
	public int getSampleSizeInBits() {
		return getSampleSizeInBits(format.mix.sample);
	}
	
	public static int getSampleSizeInBits(XtSample sample) {
		switch (sample) {
			case UINT8:
				return 8;
			case INT16:
				return 16;
			case INT24:
				return 24;
			case INT32:
				return 32;
			case FLOAT32:
				return 32;
			default:
				return 16;
		}
	}
	
	public int getChannels() {
		return format.inputs;
	}
	
	public boolean isSigned() {
		return getSampleFormat() != Encoding.PCM_UNSIGNED;
	}
	
	public boolean isBigEndian() {
		return format.mix.sample == XtSample.FLOAT32;
	}
	
}
