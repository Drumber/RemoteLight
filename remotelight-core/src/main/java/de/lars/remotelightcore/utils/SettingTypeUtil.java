package de.lars.remotelightcore.utils;

import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.types.*;

public class SettingTypeUtil {
	
	/**
	 * Get setting class from setting type as string.
	 * @param settingType setting type as string (e.g. {@code SettingBoolean})
	 * @return setting class or null
	 */
	public static Class<? extends Setting> getSettingClass(String settingType) {
		switch (settingType) {
		case "SettingBoolean":
			return SettingBoolean.class;
		case "SettingColor":
			return SettingColor.class;
		case "SettingDouble":
			return SettingDouble.class;
		case "SettingInt":
			return SettingInt.class;
		case "SettingObject":
			return SettingObject.class;
		case "SettingSelection":
			return SettingSelection.class;
		case "SettingString":
			return SettingString.class;
		case "Setting":
			return Setting.class;
		default:
			return null;
		}
	}

}
