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
