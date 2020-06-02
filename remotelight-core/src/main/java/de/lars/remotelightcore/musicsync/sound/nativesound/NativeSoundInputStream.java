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

import java.io.*;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import org.tinylog.Logger;

import com.xtaudio.xt.*;

import be.tarsos.dsp.io.TarsosDSPAudioFormat;

public class NativeSoundInputStream implements XtStreamCallback {
	
	private AudioFormat format;
	private NativeSoundFormat formatInfo;
	
	private PipedInputStream in;
	private PipedOutputStream out;

	public NativeSoundInputStream(NativeSoundFormat soundInfo) {
		format = convertToAudioFormat(soundInfo);
		this.formatInfo = soundInfo;
		
		// send data to OutputStream which forwards it to the InputStream
		in = new PipedInputStream();
		try {
			out = new PipedOutputStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PipedInputStream getInputStream() {
		return in;
	}

	@Override
	public void callback(XtStream stream, Object input, Object output, int frames, double time, long position,
			boolean timeValid, long error, Object user) throws Exception {
		if(frames == 0) {
			return;
		}
		if(error != 0) {
			Logger.error(XtPrint.errorToString(error));
		}
		
		ByteBuffer buffer = ((StreamContext) user).buffer;
		
		if(!stream.isInterleaved()) {
			int bufferSize = getBufferSize(stream, frames);
			// issue on Linux: stream.frames == frames -> buffer ArrayIndexOutOfBounds
			// this is not the best fix for it, but at least it works....
			if(!XtAudio.isWin32())
				bufferSize /= 2;
			writeData(input, buffer, bufferSize);
		}
	}
	
	private void writeData(Object input, ByteBuffer buffer, int bufferSize) {
		buffer.clear();
		switch(formatInfo.getXtFormat().mix.sample) {
			case UINT8:
				buffer.put(((byte[][]) input)[0], 0, bufferSize);
				break;
	        case INT16:
	        	buffer.asShortBuffer().put(((short[][]) input)[0], 0, bufferSize);
	        	break;
	        case INT24:
	        	buffer.put(((byte[][]) input)[0], 0, bufferSize);
	        	break;
	        case INT32:
	        	buffer.asIntBuffer().put(((int[][]) input)[0], 0, bufferSize);
	        	break;
	        case FLOAT32:
	        	buffer.asFloatBuffer().put(((float[][]) input)[0], 0, bufferSize);
	        	break;
	        default:
	            throw new IllegalArgumentException();
		}
		
		try {
			out.write(buffer.array(), 0, bufferSize);
		} catch(IOException e) {
			close();
		}
	}
	
	private int getBufferSize(XtStream stream, int frames) {
		XtFormat format = stream.getFormat();
		int sampleSize = XtAudio.getSampleAttributes(format.mix.sample).size;
		return frames * 1 * sampleSize;
	}
	
	
	public void close() {
		try {
			out.close();
			in.close();
		} catch (IOException e) {
		}
	}
	
	public AudioFormat getAudioFormat() {
		return format;
	}
	
	public static TarsosDSPAudioFormat convertToTarosDSPFormat(NativeSoundFormat finfo) {
		return new TarsosDSPAudioFormat(finfo.getSampleRate(), finfo.getSampleSizeInBits(), finfo.getChannels(), finfo.isSigned(), finfo.isBigEndian());
	}
	
	public static AudioFormat convertToAudioFormat(NativeSoundFormat finfo) {
		return new AudioFormat(finfo.getSampleRate(), finfo.getSampleSizeInBits(), finfo.getChannels(), finfo.isSigned(), finfo.isBigEndian());
	}
	
}
