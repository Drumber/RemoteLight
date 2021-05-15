package de.lars.remotelightcore.colors.palette;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.tinylog.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.lars.remotelightcore.colors.palette.model.AbstractPalette;
import de.lars.remotelightcore.colors.palette.model.PaletteData;

public class PaletteSerializer implements JsonSerializer<PaletteData>, JsonDeserializer<PaletteData> {
	
	public final static String FIELD_TYPE = "type";
	public final static String FIELD_NAME = "name";
	public final static String FIELD_PALETTE = "palette";
	
	private final PaletteLoader loader;
	
	public PaletteSerializer(PaletteLoader loader) {
		this.loader = loader;
	}

	@Override
	public JsonElement serialize(PaletteData src, Type typeOfSrc, JsonSerializationContext context) {
		Type paletteType = src.getPalette().getClass();
		String typeId = loader.paletteTypes.get(paletteType);
		if(typeId == null) {
			Logger.warn("Could not serialize color palette. Unsupported class: " + paletteType);
			return null;
		}
		
		JsonObject root = new JsonObject();
		root.addProperty(FIELD_TYPE, typeId);
		root.addProperty(FIELD_NAME, src.getName());
		
		JsonElement paletteData = context.serialize(src.getPalette(), paletteType);
		root.add(FIELD_PALETTE, paletteData);
		
		return root;
	}

	
	@Override
	public PaletteData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject root = json.getAsJsonObject();
		String typeId = root.get(FIELD_TYPE).getAsString();
		String name = root.get(FIELD_NAME).getAsString();
		
		Type paletteType = getPaletteTypeById(typeId);
		if(paletteType == null) {
			Logger.warn("Could not deserialize color palette. Unsupported type id: " + typeId);
			return null;
		}
		
		// deserialize color palette
		AbstractPalette palette = context.deserialize(root.get(FIELD_PALETTE), paletteType);
		
		// construct new PaletteData object
		PaletteData paletteData = new PaletteData(name, palette);
		return paletteData;
	}
	
	/**
	 * Returns the first palette type that is associated with
	 * the specified type ID.
	 * @param typeId	type ID as String
	 * @return			first palette type or <code>null</code>
	 */
	private Type getPaletteTypeById(String typeId) {
		for(Entry<Type, String> entry : loader.paletteTypes.entrySet()) {
			if(entry.getValue().equals(typeId)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
}
