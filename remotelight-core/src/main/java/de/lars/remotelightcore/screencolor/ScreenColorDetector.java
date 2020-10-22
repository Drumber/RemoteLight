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

package de.lars.remotelightcore.screencolor;

import java.awt.AWTException;
import de.lars.remotelightcore.utils.color.Color;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import org.tinylog.Logger;

public class ScreenColorDetector {
	
	private int leds;
	
	private int x, y;
	private int width, height;
	
	// monitor offsets for multi monitor environments
	private int monitorOffsetX;
	private int monitorOffsetY;
	// monitor width and height
	private int monitorWidth;
	private int monitorHeight;
	
	public ScreenColorDetector(int leds, GraphicsDevice monitor, Rectangle area) {
		this.leds = leds;
		this.setMonitor(monitor);
		this.setAreaBounds(area.x, area.y, area.width, area.height);
	}
	
	/**
	 * Set the bounds of the scan area
	 * @param x			x position
	 * @param y			y position
	 * @param width		area height
	 * @param height	area width
	 */
	public void setAreaBounds(int x, int y, int width, int height) {
		if(x >= 0 && x < monitorWidth)
			this.x = x;
		if(y >= 0 && y < monitorHeight)
			this.y = y;
		if(width <= monitorWidth - this.x)
			this.width = width;
		if(height <= monitorHeight - this.y)
			this.height = height;
	}
	
	public void setMonitor(GraphicsDevice monitor) {
		monitorOffsetX	= monitor.getDefaultConfiguration().getBounds().x;
		monitorOffsetY	= monitor.getDefaultConfiguration().getBounds().y;
		monitorWidth	= monitor.getDefaultConfiguration().getBounds().width;
		monitorHeight	= monitor.getDefaultConfiguration().getBounds().height;
		
		Logger.debug("Selected monitor: " + monitor.getIDstring() + " " + monitorWidth + "x" + monitorHeight);
	}
			
	public Color[] getColors() {
		try {
			Robot robot = new Robot();
			BufferedImage img = robot.createScreenCapture(new Rectangle(monitorOffsetX+x, monitorOffsetY+y, width, height));
			Color[] colors = new Color[leds];
			
			// width per section
			int widthSection = width / leds;
			
			if(widthSection > 0) {
				// every LED has at least 1 monitor pixel
				int offsetX = 0;
				
				for(int i = 0; i < leds; i++) {
					BufferedImage section = img.getSubimage(offsetX, 0, widthSection, height);
					colors[i] = getColorForSection(section);
					offsetX += widthSection;
				}
			} else {
				// multiple LEDs have the same color (width = 1)
				double pixelPerLed = 1.0 * width / leds;
				double xPos = 0.0;
						
				for(int i = 0; i < leds; i++) {
					BufferedImage section = img.getSubimage((int) xPos, 0, 1, height);
					colors[i] = getColorForSection(section);
					xPos += pixelPerLed;
				}
			}
			
			return colors;
			
		} catch(AWTException e) {
			Logger.error(e);
		}
		return null;
	}
	
	
	private Color getColorForSection(BufferedImage imgSection) {
		Color color = getAvgColor(imgSection);
		final float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		// apply filters
		color = increaseSaturation(hsb);
		color = brightnessThreshold(hsb);
		
		return color;
	}
	
	//adapted from https://javastart.pl/b/programowanie/ambilight-w-oparciu-o-jave-i-arduino/
    private Color getAvgColor(BufferedImage imgSection) {
        int width = imgSection.getWidth();
        int height = imgSection.getHeight();
        
        int xIncrement = Math.max(1, (width / 4));
        int yIncrement = Math.max(1, (height / 4));
        
        int r = 0, g = 0, b = 0;
        int loops = 0;
        for (int x = 0; x < width; x += xIncrement) {
            for (int y = 0; y < height; y += yIncrement) {
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
    
    //------------------
    // Color Filters
    //------------------
    
    protected float saturationMultiplier = 1.0f;
    protected float brightnessThreshold = 0.0f;
    
    private Color increaseSaturation(float[] hsb) {
    	hsb[1] = hsb[1] * saturationMultiplier;
    	if(hsb[1] > 1.0f) hsb[1] = 1.0f;
    	return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
    
    private Color brightnessThreshold(float[] hsb) {
    	// return black if brightness is below threshold
    	if(hsb[2] < brightnessThreshold)
    		return Color.BLACK;
    	return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

}
