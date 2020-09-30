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

package de.lars.remotelightcore.io.jsonserializer;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.*;

import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.types.SettingObject;

public class SettingSerializer implements JsonSerializer<List<Setting>> {
	
	public final static String SETTING_TYPE = "SETTING_TYPE";
	public final static String VALUES = "VALUES";
	public final static String OBJECT_TYPE = "OBJECT_TYPE";

	@Override
	public JsonElement serialize(List<Setting> src, Type typeOfSrc, JsonSerializationContext context) {
		// json root array
		JsonArray array = new JsonArray();
		// loop through all settings
		for(Setting setting : src) {
			JsonObject jsonRoot = new JsonObject();
			// get simple class name of setting instance
			String className = setting.getClass().getSimpleName();
			// add class name as setting type to root json
			jsonRoot.addProperty(SETTING_TYPE, className);
			// if setting instanceof SettingObject, add object type
			if(setting instanceof SettingObject) {
				String objectType = getSettingObjectType((SettingObject) setting);
				if(objectType != null)
					jsonRoot.addProperty(OBJECT_TYPE, objectType);
			}
			// serialize content/values of setting instance and add to root json
			JsonElement content = context.serialize(setting);
			jsonRoot.add(VALUES, content);
			// add json object to array
			array.add(jsonRoot);
		}
		return array;
	}
	
	protected String getSettingObjectType(SettingObject setting) {
		if(setting.get() != null)
			return setting.get().getClass().getName();
		return null;
	}

}
