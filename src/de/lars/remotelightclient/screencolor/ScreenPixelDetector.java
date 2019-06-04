package de.lars.remotelightclient.screencolor;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.Toolkit;


public class ScreenPixelDetector {
	
	/*
	 * WS281x
	 */
	private static int y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100);
	
	public static void setWS281xYpos(int yPos) {
		y = yPos;
	}
	
	public static Color[] getWS281xPixelColor(int pixelNum) {
		Color[] c = new Color[pixelNum];
		int spaceX = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / pixelNum);
		Robot r = null;
		try {
			r = new Robot();
			int x = 0;
			for(int i = 0; i < pixelNum; i++) {
				c[i] = r.getPixelColor(x, y);
				x += spaceX;
			}
			return c;
			
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	/*
	 * RGB
	 */
	private static int widthL, heightL, widthR, heightR;
	
	public static void setPickPixel(int widthL, int heightL, int widthR, int heightR) {
		ScreenPixelDetector.widthL = widthL;
		ScreenPixelDetector.heightL = heightL;
		ScreenPixelDetector.widthR = widthR;
		ScreenPixelDetector.heightR = heightR;
	}
	
	public static Color getPixelColorLeft() {
		Robot r = null;
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return r.getPixelColor(widthL, heightL);
	}
	
	public static Color getPixelColorRight() {
		Robot r = null;
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return r.getPixelColor(widthR, heightR);
	}
	
	
}
