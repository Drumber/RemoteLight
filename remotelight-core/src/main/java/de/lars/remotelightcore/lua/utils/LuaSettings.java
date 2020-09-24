package de.lars.remotelightcore.lua.utils;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.lua.LuaScript;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingDouble;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;

public class LuaSettings {
	/**
	 * Allows basic settings access for Lua scripts
	 */
	public LuaSettings() {}
	
	/**
	 * Register SettingColor
	 */
	public static ThreeArgFunction addColor = new ThreeArgFunction() {
		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
			registerSetting(new SettingColor(getId(arg1), getName(arg2), SettingCategory.Intern, null, LuaColor.getJavaColor(arg3)));
			return NIL;
		}
	};
	
	/**
	 * Register SettingBoolean
	 */
	public static ThreeArgFunction addBoolean = new ThreeArgFunction() {
		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
			registerSetting(new SettingBoolean(getId(arg1), getName(arg2), SettingCategory.Intern, null, arg3.checkboolean()));
			return NIL;
		}
	};
	
	/**
	 * Register SettingInt
	 */
	public static VarArgFunction addInt = new VarArgFunction() {
		public Varargs invoke(Varargs args) {
			if(args.narg() < 5)
				throw new LuaError("Expected min. 5 arguments (id, name, min, max, value, <stepSize>), got " + args.narg());
			int min = args.arg(3).checkint();
			int max = args.arg(4).checkint();
			int value = args.arg(5).checkint();
			int step = args.arg(6).isint() ? args.arg(6).checkint() : 1;
			registerSetting(new SettingInt(getId(args.arg(1)), getName(args.arg(2)), SettingCategory.Intern, null, value, min, max, step));
			return NIL;
		}
	};
	
	/**
	 * Register SettingDouble
	 */
	public static VarArgFunction addDouble = new VarArgFunction() {
		public Varargs invoke(Varargs args) {
			if(args.narg() < 5)
				throw new LuaError("Expected min. 5 arguments (id, name, min, max, value, <stepSize>), got " + args.narg());
			double min = args.arg(3).checkdouble();
			double max = args.arg(4).checkdouble();
			double value = args.arg(5).checkdouble();
			double step = args.arg(6).isnumber() ? args.arg(6).checkdouble() : 1.0;
			registerSetting(new SettingDouble(getId(args.arg(1)), getName(args.arg(2)), SettingCategory.Intern, null, value, min, max, step));
			return NIL;
		}
	};
	
	/**
	 * Register SettingSelection
	 */
	public static VarArgFunction addSelection = new VarArgFunction() {
		public Varargs invoke(Varargs args) {
			if(args.narg() != 4)
				throw new LuaError("Expected 4 arguments (id, name, valuesTable, value), got " + args.narg());
			// convert table to string array
			LuaTable tableVal = args.arg(3).checktable();
			String[] values = new String[tableVal.narg()];
			for(int i = 0; i < tableVal.narg(); i++) {
				values[i] = tableVal.get(i).checkjstring();
			}
			String value = args.arg(4).checkjstring();
			registerSetting(new SettingSelection(getId(args.arg(1)), getName(args.arg(2)), SettingCategory.Intern, null, values, value, SettingSelection.Model.ComboBox));
			return NIL;
		}
	};
	
	
	/**
	 * Get the value of the registered setting with the specified id
	 */
	public static OneArgFunction get = new OneArgFunction() {
		@Override
		public LuaValue call(LuaValue arg) {
			Setting setting = getSetting(getId(arg));
			if(setting == null)
				return NIL;
			if(setting instanceof SettingColor)
				return LuaColor.getLuaTable(((SettingColor) setting).get());
			return CoerceJavaToLua.coerce(setting.get());
		}
	};
	
	/**
	 * Remove the registered setting with the specified id
	 */
	public static OneArgFunction remove = new OneArgFunction() {
		@Override
		public LuaValue call(LuaValue arg) {
			getScript().removeSetting(getId(arg));
			return NIL;
		}
	};
	
	
	private static String getName(LuaValue arg) {
		if(arg.isstring()) {
			return arg.checkjstring();
		}
		throw new LuaError("Could not register setting. Setting name must be a string!");
	}
	
	/**
	 * Convert LuaValue to string and generate a setting id (<code>lua.[script].[id]</code>)
	 * @param arg	raw lua value
	 * @return		setting id
	 */
	private static String getId(LuaValue arg) {
		if(arg.isstring()) {
			return String.format("lua.%s.%s", getScript().getName(), arg.checkjstring());
		}
		throw new LuaError("Could not register setting. Setting ID must be a string!");
	}
	
	private static void registerSetting(Setting setting) {
		getScript().addSetting(setting);
	}
	
	private static Setting getSetting(String id) {
		return getScript().getSetting(id);
	}
	
	/**
	 * Wrapper method for getting active lua script
	 * @return		current active lua script instance
	 */
	private static LuaScript getScript() {
		LuaScript script = RemoteLightCore.getInstance().getLuaManager().getActiveLuaScript();
		if(script == null)
			throw new LuaError("Cannot access active LuaScript instance. Is the script running?");
		return script;
	}
	
}
