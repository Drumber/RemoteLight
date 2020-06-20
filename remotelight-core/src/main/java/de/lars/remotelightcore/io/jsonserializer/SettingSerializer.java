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
		if(setting.getValue() != null)
			return setting.getValue().getClass().getName();
		return null;
	}

}
