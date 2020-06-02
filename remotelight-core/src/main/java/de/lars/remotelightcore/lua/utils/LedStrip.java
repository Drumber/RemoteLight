/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Drumber
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

package de.lars.remotelightcore.lua.utils;

import java.awt.Color;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class LedStrip {
	/**
	 * This class allows controlling LED strips from Lua
	 */
	public LedStrip() {}

	/**
	 * Returns the LED number of the current strip
	 */
	public static ZeroArgFunction ledNumber = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return LuaValue.valueOf(RemoteLightCore.getLedNum());
		}
	};
	
	/**
	 * Expects a LuaTable with RGB values in a LuaTable which is as long as the led strip
	 * e.g.: {{255, 0, 0}, {250, 15, 0}, {0, 255, 255}, ...}
	 * 		  \ 1. LED  /  \ 2. LED   /   \ 3. LED   /  ...
	 */
	public static VarArgFunction show = new VarArgFunction() {
		public Varargs invoke(Varargs args) {
			LuaTable strip = args.checktable(1);
			
			if(strip.length() == RemoteLightCore.getLedNum()) {
				Color[] outPixels = new Color[RemoteLightCore.getLedNum()];
				
				for(int i = 1; i <= RemoteLightCore.getLedNum(); i++) {
					LuaTable rgb = strip.get(i).checktable();
					int r = rgb.get(1).checkint();
					int g = rgb.get(2).checkint();
					int b = rgb.get(3).checkint();
					outPixels[i - 1] = new Color(r, g, b);
				}
				OutputManager.addToOutput(outPixels);
			} else {
				throw new LuaError("Expected a table length of " + RemoteLightCore.getLedNum() + ", got " + strip.length());
			}
			return NIL;
		}
	};
	
	/**
	 * Returns true if the user defined delay/speed is over
	 */
	public static ZeroArgFunction delayReached = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return LuaValue.valueOf(RemoteLightCore.getInstance().getLuaManager().getTimer().hasReached());
		}
	};
	
	/**
	 * Set the whole strip to the given color
	 */
	public static OneArgFunction setAll = new OneArgFunction() {
		@Override
		public LuaValue call(LuaValue arg) {
			LuaTable rgb = arg.checktable();
			if(rgb.length() == 3) {
				int r = rgb.get(1).checkint();
				int g = rgb.get(2).checkint();
				int b = rgb.get(3).checkint();
				OutputManager.addToOutput(PixelColorUtils.colorAllPixels(new Color(r, g, b), RemoteLightCore.getLedNum()));
			} else {
				throw new LuaError("Expected a table length of 3, got " + rgb.length());
			}
			return NIL;
		}
	};
	
	/**
	 * Set the color of a single pixel
	 */
	public static TwoArgFunction setPixel = new TwoArgFunction() {
		@Override
		public LuaValue call(LuaValue led, LuaValue color) {
			int pixel = led.checkinteger().toint();
			LuaTable rgb = color.checktable();
			if(rgb.length() == 3) {
				int r = rgb.get(1).checkint();
				int g = rgb.get(2).checkint();
				int b = rgb.get(3).checkint();
				if(pixel > 0 && pixel <= RemoteLightCore.getLedNum()) {
					PixelColorUtils.setPixel(pixel - 1, new Color(r, g, b));
				} else {
					throw new LuaError("Inavlid LED number '" + pixel + "'. Should be > 0 and <= " + RemoteLightCore.getLedNum());
				}
			} else {
				throw new LuaError("Expected a table length of 3, got " + rgb.length());
			}
			return NIL;
		}
	};
	
	/**
	 * Shift all colors in strip x times to right
	 */
	public static OneArgFunction shiftRight = new OneArgFunction() {
		@Override
		public LuaValue call(LuaValue arg) {
			PixelColorUtils.shiftRight(arg.checkint());
			return NIL;
		}
	};
	
	/**
	 * Shift all colors in strip x times to left
	 */
	public static OneArgFunction shiftLeft = new OneArgFunction() {
		@Override
		public LuaValue call(LuaValue arg) {
			PixelColorUtils.shiftLeft(arg.checkint());
			return NIL;
		}
	};
	
	/**
	 * Shift all colors in strip x times outwards
	 */
	public static OneArgFunction shiftOutwards = new OneArgFunction() {
		@Override
		public LuaValue call(LuaValue arg) {
			PixelColorUtils.shiftCenter(arg.checkint());
			return NIL;
		}
	};
	
	/**
	 * Returns a LuaTable with RGB values of a random color
	 */
	public static ZeroArgFunction randomColor = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			Color c = RainbowWheel.getRandomColor();
			Varargs rgb = LuaTable.varargsOf( LuaValue.valueOf(c.getRed()) , LuaValue.valueOf(c.getGreen()) , LuaValue.valueOf(c.getBlue()) );
			return new LuaTable(rgb);
		}
	};

}
