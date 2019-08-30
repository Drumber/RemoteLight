package de.lars.remotelightclient.devices.arduino;

import java.awt.Color;

public class GlediatorProtocol {
	
	public static byte[] doOutput(Color[] leds) {
		
		int index = 0;
		
		byte[] outputBuffer = new byte[leds.length * 3 + 1];
		
		outputBuffer[index] = 1;
		index++;
		
		for(int i = 0; i < leds.length; i++) {
			Color tmp = leds[i];
			//GRB color order
			byte b1 = (byte) tmp.getGreen();
			byte b2 = (byte) tmp.getRed();
			byte b3 = (byte) tmp.getBlue();
			
			if(b1 == 1)
				b1 = 2;
			if(b2 == 1)
				b2 = 2;
			if(b3 == 1)
				b3 = 2;
			
			
			outputBuffer[index] = b1;
			outputBuffer[(index + 1)] = b2;
			outputBuffer[(index + 2)] = b3;
			
			index += 3;
		}
		
		return outputBuffer;
	}

}
