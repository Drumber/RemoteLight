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
		ANOMATION('\uE902'),
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
		OUTPUTS('\uE913');

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
