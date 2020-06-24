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

import de.lars.remotelightcore.devices.Device;

public class DeviceSerializer implements JsonSerializer<List<Device>> {
	
	public final static String DEVICE_TYPE = "DEVICE_TYPE";
	public final static String VALUES = "VALUES";

	@Override
	public JsonElement serialize(List<Device> src, Type typeOfSrc, JsonSerializationContext context) {
		// json root array
		JsonArray array = new JsonArray();
		// loop through all devices
		for(Device device : src) {
			JsonObject jsonRoot = new JsonObject();
			// get simple class name of device instance
			String className = device.getClass().getSimpleName();
			// add class name as device type to root json
			jsonRoot.addProperty(DEVICE_TYPE, className);
			// serialize content/values of device instance and add to root json
			JsonElement content = context.serialize(device);
			jsonRoot.add(VALUES, content);
			// add json object to array
			array.add(jsonRoot);
		}
		return array;
	}

}
