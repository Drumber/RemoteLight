package de.lars.remotelightclient.utils;

import java.awt.Color;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Custom JSON de-/serializer for {@link java.awt.Color} due to Gson cannot access
 * the <code>value</code> field when running on Java 16.
 */
public class ColorSerializer implements JsonDeserializer<Color>, JsonSerializer<Color> {

	@Override
	public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.addProperty("value", src.getRGB());
		json.addProperty("falpha", src.getRGBComponents(null)[3]);
		return json;
	}

	@Override
	public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if(!json.isJsonObject()) {
			throw new JsonParseException("Expected JSON object for color but got '" + json.getClass().getSimpleName() + "'.");
		}
		
		JsonObject obj = json.getAsJsonObject();
		if(!obj.has("value")) {
			throw new JsonParseException("Color object requires a 'value' member.");
		}
		
		int rgb = obj.get("value").getAsInt();
		return new Color(rgb);
	}

}
