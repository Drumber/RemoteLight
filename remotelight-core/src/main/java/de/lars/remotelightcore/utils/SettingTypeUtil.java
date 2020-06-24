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
