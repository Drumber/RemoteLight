package de.lars.remotelightcore.io.jsondeserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import com.google.gson.*;

import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.io.jsonserializer.DeviceSerializer;
import de.lars.remotelightcore.utils.OutputUtil;

public class DeviceDeserializer implements JsonDeserializer<List<Device>> {

	@Override
	public List<Device> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if(json.isJsonArray()) {
			JsonArray array = json.getAsJsonArray();
			List<Device> devices = new ArrayList<>();
			
			// loop through json array
			for(JsonElement element : array) {
				// json root object
				JsonObject jsonRoot = element.getAsJsonObject();
				
				if(!jsonRoot.has(DeviceSerializer.DEVICE_TYPE))
					throw new JsonParseException("JsonObject does not contain '" + DeviceSerializer.DEVICE_TYPE + "' member.");
				
				// get class name/device type from json
				String className = jsonRoot.get(DeviceSerializer.DEVICE_TYPE).getAsString();
				// get setting class from string
				Class<? extends Device> clazz = OutputUtil.getDeviceClass(className);
				if(clazz == null) {
					Logger.error("Could not find class for device type '" + className + "'. Skipping this device.");
					continue;
				}
				
				if(!jsonRoot.has(DeviceSerializer.VALUES))
					throw new JsonParseException("JsonObject does not contain '" + DeviceSerializer.VALUES + "' member.");
				
				JsonElement content = jsonRoot.get(DeviceSerializer.VALUES);
				Device device = context.deserialize(content, clazz);
				
				// add device to list
				devices.add(device);
			}
			return devices;
		}
		throw new JsonParseException("Couldn't parse setting from json. Expected a JsonArray, got " + json.getClass().getSimpleName());
	}
	
}
