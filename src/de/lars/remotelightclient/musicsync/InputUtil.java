package de.lars.remotelightclient.musicsync;

import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class InputUtil {
	
	public static boolean isLineSupported(Mixer mixer) {
		try {
			mixer.open();
	        Line.Info linfo = new Line.Info(TargetDataLine.class);
	        TargetDataLine line = null;
	        try {
	            line = (TargetDataLine)mixer.getLine(linfo);
	            line.open();
	        } catch (IllegalArgumentException ex) {
	        	return false;
	        } catch (LineUnavailableException ex) {
	        	return true;
	        }
			mixer.close();
		} catch(LineUnavailableException e) {
			return true;
		}
		return true;
	}

}
