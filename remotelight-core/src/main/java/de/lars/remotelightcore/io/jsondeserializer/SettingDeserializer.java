package de.lars.remotelightcore.io.jsondeserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import com.google.gson.*;

import de.lars.remotelightcore.io.jsonserializer.SettingSerializer;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.types.SettingObject;
import de.lars.remotelightcore.utils.SettingTypeUtil;

public class SettingDeserializer implements JsonDeserializer<List<Setting>> {

	@Override
	public List<Setting> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if(json.isJsonArray()) {
			JsonArray array = json.getAsJsonArray();
			List<Setting> settings = new ArrayList<>();
			
			// loop through json array
			for(JsonElement element : array) {
				// json root object
				JsonObject jsonRoot = element.getAsJsonObject();
				
				if(!jsonRoot.has(SettingSerializer.SETTING_TYPE))
					throw new JsonParseException("JsonObject does not contain '" + SettingSerializer.SETTING_TYPE + "' member.");
				
				// get class name/setting type from json
				String className = jsonRoot.get(SettingSerializer.SETTING_TYPE).getAsString();
				// get setting class from string
				Class<? extends Setting> clazz = SettingTypeUtil.getSettingClass(className);
				if(clazz == null) {
					Logger.error("Could not find class for setting type '" + className + "'. Skipping this setting.");
					continue;
				}
				
				if(!jsonRoot.has(SettingSerializer.VALUES))
					throw new JsonParseException("JsonObject does not contain '" + SettingSerializer.VALUES + "' member.");
				
				JsonElement content = jsonRoot.get(SettingSerializer.VALUES);
				Setting setting;
				
				// special deserialization for SettingObject
				if(className.equals("SettingObject") && content.isJsonObject()) {
					setting = deserializeObject((JsonObject) jsonRoot, context);
				} else {
					setting = context.deserialize(content, clazz);
				}
				
				// add setting to list
				settings.add(setting);
			}
			return settings;
		}
		throw new JsonParseException("Couldn't parse setting from json. Expected a JsonArray, got " + json.getClass().getSimpleName());
	}
	
	protected SettingObject deserializeObject(JsonObject json, JsonDeserializationContext context) {
		// SettingObject value key
		final String valueKey = "value";
		// setting data
		JsonObject content = json.get(SettingSerializer.VALUES).getAsJsonObject();
		// deserialize setting object
		SettingObject setting = context.deserialize(content, SettingObject.class);
		
		if(json.has(SettingSerializer.OBJECT_TYPE) && content.has(valueKey)) {
			// get class name from json object
			String classname = json.get(SettingSerializer.OBJECT_TYPE).getAsString();
			Class<?> clazz = Object.class;
			try {
				// try to get class
				clazz = Class.forName(classname);
			} catch (ClassNotFoundException e) {
				Logger.error(e, "Invalid setting data! Could not find class for object type " + classname);
			}
			// get value element
			JsonElement jsonValue = content.get(valueKey);
			// deserialize using found class
			Object value = context.deserialize(jsonValue, clazz);
			// set new value
			setting.setValue(value);
		}
		return setting;
	}
	
}
