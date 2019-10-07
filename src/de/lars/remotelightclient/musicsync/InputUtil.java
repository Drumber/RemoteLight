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
package de.lars.remotelightclient.musicsync;

import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class InputUtil {
	
	public static boolean isLineSupported(Mixer mixer) {
        TargetDataLine line = null;
		try {
			mixer.open();
	        Line.Info linfo = new Line.Info(TargetDataLine.class);
	        try {
	            line = (TargetDataLine)mixer.getLine(linfo);
	            line.open();
	        } catch (IllegalArgumentException ex) {
	        	return false;
	        } catch (LineUnavailableException ex) {
	        	return true;
	        }
		} catch(LineUnavailableException e) {
			return true;
		}
		mixer.close();
		line.close();
		return true;
	}

}
