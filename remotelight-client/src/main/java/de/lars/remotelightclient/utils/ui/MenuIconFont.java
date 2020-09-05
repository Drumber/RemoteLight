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

package de.lars.remotelightclient.utils.ui;

import java.io.InputStream;

import jiconfont.IconCode;
import jiconfont.IconFont;

public class MenuIconFont implements IconFont {
	
	protected String classpath;
	
	public MenuIconFont(String classpath) {
		this.classpath = classpath;
	}

	@Override
	public String getFontFamily() {
		return "MenuIconFont";
	}

	@Override
	public InputStream getFontInputStream() {
		return getClass().getResourceAsStream(classpath);
	}
	
	
	public enum MenuIcon implements IconCode {
		
		ABOUT('\uE900'),
		ADD('\uE901'),
		ANIMATION('\uE902'),
		ARDUINO('\uE903'),
		ARTNET('\uE904'),
		CHAIN('\uE905'),
		COLOR_PALETTE('\uE906'),
		LED_STRIP('\uE907'),
		LED_STRIP_GLOWING('\uE908'),
		LED_STRIP_MIDDLE('\uE909'),
		LINK('\uE90a'),
		LINK_STRIPS('\uE90b'),
		MENU('\uE90c'),
		MUSICSYNC('\uE90d'),
		RASPBERRYPI('\uE90e'),
		SCENE('\uE90f'),
		SCREENCOLOR('\uE910'),
		SCRIPT('\uE911'),
		SETTINGS('\uE912'),
		OUTPUTS('\uE913'),
		ERROR('\uE914'),
		HELP('\uE915'),
		CONTROL('\uE916'),
		TOOLS('\uE917'),
		VIRTUAL('\uE918');

		private final char character;
		
		MenuIcon(char character) {
			this.character = character;
		}
		
		@Override
		public char getUnicode() {
			return character;
		}

		@Override
		public String getFontFamily() {
			return "MenuIconFont";
		}
		
	}

}
