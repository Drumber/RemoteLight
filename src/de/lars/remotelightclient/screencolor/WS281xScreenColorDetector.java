package de.lars.remotelightclient.screencolor;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class WS281xScreenColorDetector {
	
	private int pixel;
	private int offsetY;
	private int x, y;
	private int width, height;
	
	public WS281xScreenColorDetector(int pixel, GraphicsDevice monitor, int offsetY) {
		this.pixel = pixel;
		this.offsetY = offsetY;
		x = monitor.getDefaultConfiguration().getBounds().x;
		y = monitor.getDefaultConfiguration().getBounds().y;
		width = monitor.getDefaultConfiguration().getBounds().width;
		height = monitor.getDefaultConfiguration().getBounds().height;
		
		System.out.println(width + " | " + height);
	}
	
	
	public Color[] getColors() {
		try {
			Robot robot = new Robot();
			BufferedImage img = robot.createScreenCapture(new Rectangle(x, y, width, height));
			Color[] c = new Color[pixel];
			
			int space = width / pixel;
			int x = 0;
			for(int i = 0; i < pixel; i++) {
				BufferedImage section = img.getSubimage(x, offsetY, space, (int) (height / 50));
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
    private Color getAvgColor(BufferedImage imgSection) {
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

}
