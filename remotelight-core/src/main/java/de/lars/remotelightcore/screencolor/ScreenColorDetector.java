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
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import org.tinylog.Logger;

public class ScreenColorDetector {
	
	private int pixel;
	private int offsetY, yHeight;
	private int x, y;
	private int width, height;
	
	public ScreenColorDetector(int pixel, GraphicsDevice monitor, int offsetY, int yHeight) {
		this.pixel = pixel;
		this.setMonitor(monitor);
		this.setYPos(offsetY);
		this.setYHeight(yHeight);
	}
	
	
	public void setYPos(int ypos) {
		this.offsetY = ypos;
		if(offsetY + yHeight > height) {
			this.offsetY = height - yHeight;
		}
	}
	
	public void setYHeight(int yHeight) {
		this.yHeight = yHeight;
		if(yHeight + offsetY > height) {
			offsetY = height - yHeight;
		}
	}
	
	public void setMonitor(GraphicsDevice monitor) {
		x = monitor.getDefaultConfiguration().getBounds().x;
		y = monitor.getDefaultConfiguration().getBounds().y;
		width = monitor.getDefaultConfiguration().getBounds().width;
		height = monitor.getDefaultConfiguration().getBounds().height;
		
		Logger.debug("Selected monitor: " + monitor.getIDstring() + " " + width + " | " + height);
	}
	
	public Color[] getColors() {
		try {
			Robot robot = new Robot();
			BufferedImage img = robot.createScreenCapture(new Rectangle(x, y, width, height));
			Color[] c = new Color[pixel];
			
			int space = width / pixel;
			int x = 0;
			
			for(int i = 0; i < pixel; i++) {
				BufferedImage section = img.getSubimage(x, offsetY, space, yHeight);
				c[i] = getAvgColor(section);
				x += space;
			}
			return c;
			
		} catch(AWTException e) {
			Logger.error(e);
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
