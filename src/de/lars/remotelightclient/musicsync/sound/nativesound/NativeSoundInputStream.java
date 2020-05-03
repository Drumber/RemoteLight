package de.lars.remotelightclient.musicsync.sound.nativesound;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import com.xtaudio.xt.XtStream;
import com.xtaudio.xt.XtStreamCallback;

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
		byte[] data;
		switch(formatInfo.getXtFormat().mix.sample) {
			case UINT8:
				data = (byte[]) input;
				break;
	        case INT16:
	        	data = shortToByte((short[]) input);
	        	break;
	        case INT24:
	        	data = (byte[]) input;
	        	break;
	        case INT32:
	        	data = intToByte((int[]) input);
	        	break;
	        case FLOAT32:
	        	data = floatToByte((float[]) input);
	        	break;
	        default:
	            throw new IllegalArgumentException();
		}
		try {
			out.write(data);
		} catch(IOException e) {
			close();
		}
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
	
	
	/*===============
	 * ByteArray conversion
	 *===============*/
	// https://stackoverflow.com/a/12347176/12821118
	byte[] shortToByte(short[] input) {
		int short_index, byte_index;
		int iterations = input.length;

		byte[] buffer = new byte[input.length * 2];

		short_index = byte_index = 0;

		for (/* NOP */; short_index != iterations; /* NOP */) {
			buffer[byte_index] = (byte) (input[short_index] & 0x00FF);
			buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

			++short_index;
			byte_index += 2;
		}

		return buffer;
	}
	
	byte[] intToByte(int[] input) {
		int short_index, byte_index;
		int iterations = input.length;

		byte[] buffer = new byte[input.length * 4];

		short_index = byte_index = 0;

		for (/* NOP */; short_index != iterations; /* NOP */) {
			buffer[byte_index] = (byte) (input[short_index] & 0x00FF);
			buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);
			buffer[byte_index + 3] = (byte) ((input[short_index] & 0xFF00) >> 16);
			buffer[byte_index + 4] = (byte) ((input[short_index] & 0xFF00) >> 24);

			++short_index;
			byte_index += 4;
		}

		return buffer;
	}
	
	byte[] floatToByte(float[] input) {
		// I know, the method used above is faster...
		ByteBuffer buffer = ByteBuffer.allocate(4 * input.length);
		for (float value : input) {
			buffer.putFloat(value);
		}
		return buffer.array();
	}

}
