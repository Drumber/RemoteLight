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
package de.lars.remotelightclient.devices.arduino;

import java.awt.Color;

import de.lars.remotelightclient.utils.ColorUtil;

public class GlediatorProtocol {
	
	public static byte[] doOutput(Color[] leds, RgbOrder order) {
		
		int index = 0;
		
		byte[] outputBuffer = new byte[leds.length * 3 + 1];
		
		outputBuffer[index] = 1;
		index++;
		
		for(int i = 0; i < leds.length; i++) {
			Color tmp = leds[i];
			tmp = ColorUtil.matchRgbOrder(tmp, order);
			
			byte b1 = (byte) tmp.getRed();
			byte b2 = (byte) tmp.getGreen();
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
