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

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import org.tinylog.Logger;

import com.xtaudio.xt.XtAudio;
import com.xtaudio.xt.XtFormat;
import com.xtaudio.xt.XtPrint;
import com.xtaudio.xt.XtStream;
import com.xtaudio.xt.XtStreamCallback;

import be.tarsos.dsp.io.TarsosDSPAudioFormat;

public class NativeSoundInputStream implements XtStreamCallback, Runnable {
	
	private AudioFormat format;
	private NativeSoundFormat formatInfo;
	
	private PipedInputStream in;
	private PipedOutputStream out;
	
	private int bufferSize = -1;
	private ByteBuffer buffer;
	private Object data;

	public NativeSoundInputStream(NativeSoundFormat soundInfo) {
		format = convertToAudioFormat(soundInfo);
		this.formatInfo = soundInfo;
		
		// send data to OutputStream which forwards it to the InputStream
		out = new PipedOutputStream();
		try {
			in = new PipedInputStream(out);
		} catch (IOException e) {
			Logger.error(e);
		}
		
		
	}
	
	public PipedInputStream getInputStream() {
		return in;
	}
	
	
	@Override
	public void run() {
		Logger.debug("Starting NativeSoundInputStream Thread...");
		while(out != null) {
			if(data != null) {
				writeData(data);
			}
			try {
				// need some delay in the loop, because of too high CPU usage
				Thread.sleep(5);
			} catch (InterruptedException e) {
				Logger.error(e, "Hey! I got some error while sleeping in the NativeSoundInputStream Thread house.");
			}
		}
		Logger.debug("Stopping NativeSoundInputStream Thread...");
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
		
		if(buffer == null)
			buffer = ((StreamContext) user).buffer;
		
		if(!stream.isInterleaved()) {
			if(bufferSize == -1) {
				bufferSize = getBufferSize(stream, frames);
				// issue on Linux: stream.frames == frames -> buffer ArrayIndexOutOfBounds
				// this is not the best fix for it, but at least it works....
				if(!XtAudio.isWin32())
					bufferSize /= 2;
			}
			data = input;
		}
	}
	
	private void writeData(Object input) {
		buffer.clear();
		switch(formatInfo.getXtFormat().mix.sample) {
			case UINT8:
				byte[][] byteBuffer = (byte[][]) input;
	        	bufferSize = Math.min(byteBuffer[0].length, bufferSize);
				buffer.put(byteBuffer[0], 0, bufferSize);
				break;
	        case INT16:
	        	short[][] shortBuffer = (short[][]) input;
	        	bufferSize = Math.min(shortBuffer[0].length, bufferSize);
	        	buffer.asShortBuffer().put(shortBuffer[0], 0, bufferSize);
	        	break;
	        case INT24:
	        	byteBuffer = (byte[][]) input;
	        	bufferSize = Math.min(byteBuffer[0].length, bufferSize);
	        	buffer.put(byteBuffer[0], 0, bufferSize);
	        	break;
	        case INT32:
	        	int[][] intBuffer = (int[][]) input;
	        	bufferSize = Math.min(intBuffer[0].length, bufferSize);
	        	buffer.asIntBuffer().put(intBuffer[0], 0, bufferSize);
	        	break;
	        case FLOAT32:
	        	float[][] floatBuffer = (float[][]) input;
	        	bufferSize = Math.min(floatBuffer[0].length, bufferSize);
	        	buffer.asFloatBuffer().put(floatBuffer[0], 0, bufferSize);
	        	break;
	        default:
	            throw new IllegalArgumentException();
		}
		// write to byte stream
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
		} finally {
			out = null;
			in = null;
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
