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

package de.lars.remotelightclient.ui.components.frames;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.SplashScreen;

import de.lars.remotelightclient.Main;

public class SplashFrame {
	
	private static SplashScreen splash;
	
	/**
	 * Show the {@link java.awt.SplashScreen} and draw the current version tag.
	 * The splash screen only works with the <code>SplashScreen-Image: %splashImage%</code>
	 * option defined in the Manifest or using the command line argument
	 * <code>-splash:%splashImage</code>.
	 * <p>The splash screen is closed as soon as the first Frame is displayed.
	 */
	public static void showSplashScreen() {
		if(splash != null) {
			System.out.println("SplashScreen already displayed.");
			return;
		}
		
		splash = SplashScreen.getSplashScreen();
		if(splash == null) {
			System.out.println("SplashScreen is not available.");
			return;
		}
		Graphics2D g = splash.createGraphics();
		if(g == null) {
			System.out.println("SplashSCreen Graphics2D instance is null.");
			return;
		}
		
		// draw version tag in bottom right corner
		final int padding = 5;
		final int width = splash.getBounds().width;
		final int height = splash.getBounds().height;
		final String text = Main.VERSION;
		final FontMetrics fm = g.getFontMetrics();
		
		int x = width - fm.stringWidth(text) - padding;
		int y = height - fm.getHeight() - padding;
		
		g.setColor(Color.WHITE);
		g.drawString(text, x, y);
		g.dispose();
		splash.update();
	}

}
