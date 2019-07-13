package de.lars.remotelightclient.screencolor;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;

public class RgbScreenColorDetector {
	
	private int xL, yL, xR, yR;
	
	public RgbScreenColorDetector(int xLeft, int yLeft, int xRight, int yRight) {
		xL = xLeft;
		yL = yLeft;
		xR = xRight;
		yR = yRight;
	}
	
	
	public Color getPixelColorLeft() {
		try {
			Robot r = new Robot();
			return r.getPixelColor(xL, yL);
			
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Color getPixelColorRight() {
		try {
			Robot r = new Robot();
			return r.getPixelColor(xR, yR);
			
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return null;
	}

}
