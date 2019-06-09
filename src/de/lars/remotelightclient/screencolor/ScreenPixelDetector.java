package de.lars.remotelightclient.screencolor;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;


public class ScreenPixelDetector {
	
	/*
	 * WS281x
	 */
	private static int y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100);
	private static Robot robot;
	private static int sizeX = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private static int sizeY = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
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
	
	
	//best result BUT mouse flickering (does not matter when watching full screen videos)
	public static Color[] getColors(int pixel) {
		try {
			robot = new Robot();
			BufferedImage img = robot.createScreenCapture(new Rectangle(sizeX, sizeY));
			Color[] c = new Color[pixel];
			
			int space = sizeX / pixel;
			int x = 0;
			for(int i = 0; i < pixel; i++) {
				BufferedImage section = img.getSubimage(x, y, space, (int) (sizeY / 50));
				c[i] = getAvgColor(section);
				x += space;
			}
			return c;
			
		} catch(AWTException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//adapted from https://javastart.pl/b/programowanie/ambilight-w-oparciu-o-jave-i-arduino/
    private static Color getAvgColor(BufferedImage imgSection) {
        int width = imgSection.getWidth();
        int height = imgSection.getHeight();
        int r = 0, g = 0, b = 0;
        int loops = 0;
        for (int x = 0; x < width; x += (width / 4)) {
            for (int y = 0; y < height; y += (height / 4)) {
                int rgb = imgSection.getRGB(x, y);
                Color color = new Color(rgb);
                r += color.getRed();
                g += color.getGreen();
                b += color.getBlue();
                loops++;
            }
        }
        r = r / loops;
        g = g / loops;
        b = b / loops;
        return new Color(r, g, b);
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
