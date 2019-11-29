package de.lars.remotelightclient.lua;

import java.awt.Color;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class LedStrip {
	/**
	 * This class allows controlling LED strips from Lua
	 */
	
	private final static String TAG = "[LUA] ";
	public LedStrip() {}

	/**
	 * 
	 * @return LED number of the current strip
	 */
	public static ZeroArgFunction ledNumber = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return LuaValue.valueOf(Main.getLedNum());
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
			
			if(strip.length() == Main.getLedNum()) {
				Color[] outPixels = new Color[Main.getLedNum()];
				
				for(int i = 1; i <= Main.getLedNum(); i++) {
					LuaTable rgb = strip.get(i).checktable();
					int r = rgb.get(1).checkint();
					int g = rgb.get(2).checkint();
					int b = rgb.get(3).checkint();
					outPixels[i - 1] = new Color(r, g, b);
				}
				OutputManager.addToOutput(outPixels);
			} else {
				Logger.error(TAG + "Expected a table length of " + Main.getLedNum() + ", got " + strip.length());
			}
			return NIL;
		}
	};
	
	/**
	 * Return true if the user defined delay/speed is over
	 */
	public static ZeroArgFunction delayReached = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return LuaValue.valueOf(Main.getInstance().getLuaManager().getTimer().hasReached());
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
				PixelColorUtils.colorAllPixels(new Color(r, g, b), Main.getLedNum());
			} else {
				Logger.error(TAG + "Expected a table length of 3, got " + rgb.length());
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
				if(pixel > 0 && pixel <= Main.getLedNum()) {
					PixelColorUtils.setPixel(pixel, new Color(r, g, b));
				} else {
					Logger.error(TAG + "Inavlid LED number '" + pixel + "'. Should be > 0 and <= " + Main.getLedNum());
				}
			} else {
				Logger.error(TAG + "Expected a table length of 3, got " + rgb.length());
			}
			return NIL;
		}
	};

}
