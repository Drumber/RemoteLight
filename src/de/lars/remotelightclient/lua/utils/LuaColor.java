package de.lars.remotelightclient.lua.utils;

import java.awt.Color;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import de.lars.remotelightclient.utils.RainbowWheel;

public class LuaColor {
	/**
	 * This class includes a few color functions for Lua
	 */
	public LuaColor() {}
	
	/**
	 * Returns a LuaTable with RGB values of the given hue from the rainbow wheel
	 */
	public static OneArgFunction rainbowWheel = new OneArgFunction() {
		@Override
		public LuaValue call(LuaValue arg) {
			int hue = arg.checkint();
			if(hue >= 0 && hue < RainbowWheel.getRainbow().length) {
				Color c = RainbowWheel.getRainbow()[hue];
				return getLuaTable(c);
			} else {
				throw new LuaError("Expected numeric value >= 0 and < " + RainbowWheel.getRainbow().length + ", got '" + hue + "'.");
			}
		}
	};
	
	/**
	 * Returns a LuaTable with RGB values of the given HSV values
	 */
	public static ThreeArgFunction hsv = new ThreeArgFunction() {
		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
			int hue = arg1.checkint();
			int saturation = arg2.checkint();
			int brightness = arg3.checkint();
			if(hue < 0 || hue > 360)
				throw new LuaError("Hue: Expected numeric value >= 0 and <= 360, got '" + hue + "'.");
			if(saturation < 0 || saturation > 100)
				throw new LuaError("Saturation: Expected numeric value >= 0 and <= 100, got '" + saturation + "'.");
			if(brightness < 0 || brightness > 100)
				throw new LuaError("Brightness: Expected numeric value >= 0 and <= 100, got '" + brightness + "'.");
			Color c = Color.getHSBColor(hue / 360.0f, saturation / 100.0f, brightness / 100.0f);
			return getLuaTable(c);
		}
	};
	
	/*==============
	 * Colors
	 *==============/
	
	/**
	 * Build a LuaTable with the given RGB values
	 */
	public static ThreeArgFunction rgb = new ThreeArgFunction() {
		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
			int r = arg1.checkint();
			int g = arg2.checkint();
			int b = arg3.checkint();
			if(r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
				throw new LuaError("Expected numeric value >= 0 and <= 255.");
			return getLuaTable(new Color(r, g, b));
		}
	};
	
	public static ZeroArgFunction RED = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			Color c = Color.RED;
			Varargs rgb = LuaTable.varargsOf( LuaValue.valueOf(c.getRed()) , LuaValue.valueOf(c.getGreen()) , LuaValue.valueOf(c.getBlue()) );
			return new LuaTable(rgb);
		}
	};
	
	public static ZeroArgFunction GREEN = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return getLuaTable(Color.GREEN);
		}
	};

	public static ZeroArgFunction BLUE = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return getLuaTable(Color.BLUE);
		}
	};

	public static ZeroArgFunction BLACK = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return getLuaTable(Color.BLACK);
		}
	};

	public static ZeroArgFunction WHITE = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return getLuaTable(Color.WHITE);
		}
	};
	
	public static ZeroArgFunction CYAN = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return getLuaTable(Color.CYAN);
		}
	};

	public static ZeroArgFunction ORANGE = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return getLuaTable(Color.ORANGE);
		}
	};

	public static ZeroArgFunction YELLOW = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return getLuaTable(Color.YELLOW);
		}
	};
	
	public static ZeroArgFunction MAGENTA = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return getLuaTable(Color.MAGENTA);
		}
	};
	
	public static ZeroArgFunction PINK = new ZeroArgFunction() {
		@Override
		public LuaValue call() {
			return getLuaTable(Color.PINK);
		}
	};
	
	/**
	 * Convert java color to lua table
	 * @param c Color
	 * @return LuaTable with RGB values
	 */
	private static LuaTable getLuaTable(Color c) {
		Varargs rgb = LuaTable.varargsOf( LuaValue.valueOf(c.getRed()) , LuaValue.valueOf(c.getGreen()) , LuaValue.valueOf(c.getBlue()) );
		return new LuaTable(rgb);
	}

}
