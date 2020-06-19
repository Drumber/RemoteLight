package de.lars.remotelightcore.io;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import com.google.gson.*;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.io.jsonserializer.SettingDeserializer;
import de.lars.remotelightcore.io.jsonserializer.SettingSerializer;
import de.lars.remotelightcore.settings.Setting;

public class FileStorage {
	
	/** storage file */
	private File file;
	/** Gson instance */
	private Gson gson;
	/** storage hash map */
	private HashMap<String, Object> storageMap;
	
	public final String KEY_DATA = "data";
	public final String KEY_VERSION = "version";
	public final String KEY_SETTINGS_LIST = "settingsmanager_key";
	
	public FileStorage(File file) {
		this.file = file;
		List<Setting> settingListTemplate = new ArrayList<>();
		this.gson = new GsonBuilder()
				.registerTypeAdapter(settingListTemplate.getClass(), new SettingSerializer())
				.registerTypeAdapter(settingListTemplate.getClass(), new SettingDeserializer())
				.serializeNulls()
				.setPrettyPrinting()
				.create();
		this.storageMap = new HashMap<>();
	}
	
	public void store(String key, Object data) {
		storageMap.put(key, data);
	}
	
	public Object get(String key) {
		return storageMap.get(key);
	}
	
	public void remove(String key) {
		storageMap.remove(key);
	}
	
	public void clear() {
		storageMap.clear();
	}
	
	public File getFile() {
		return file;
	}
	
	public void save() throws IOException {
		// create directory if not exists
		if(!file.exists())
			file.getParentFile().mkdirs();
		
		// json root element
		JsonObject jsonRoot = new JsonObject();
		// add version tag
		jsonRoot.addProperty(KEY_VERSION, RemoteLightCore.VERSION);
		// json array including all data
		JsonArray array = new JsonArray();
		
		// loop through all data and build json object
		for(Map.Entry<String, Object> entry : storageMap.entrySet()) {
			// serialize data object to json
			JsonElement sElement = gson.toJsonTree(entry.getValue());
			// create json object and add json data
			JsonObject jsonEntry = new JsonObject();
			jsonEntry.add(entry.getKey(), sElement);
			// add to json array
			array.add(jsonEntry);
		}
		
		// add json array to root
		jsonRoot.add(KEY_DATA, array);
		// save to file
		try (Writer writer = Files.newBufferedWriter(file.toPath())) {
			gson.toJson(jsonRoot, writer);
		}
	}
	
	public void load() throws IOException {
		if(!file.exists() || !file.isFile())
			return;
		
		try (Reader reader = Files.newBufferedReader(file.toPath())) {
			// read json object from file
			JsonObject jsonRoot = gson.fromJson(reader, JsonObject.class);
			// get json data array
			JsonElement arrayElement = jsonRoot.get(KEY_DATA);
			if(arrayElement == null || !arrayElement.isJsonArray())
				throw new JsonParseException("Invalid data! Expected JsonArray, but got " + arrayElement.getClass().getName());
			JsonArray array = arrayElement.getAsJsonArray();
			
			// add all data to hash map
			for(JsonElement element : array) {
				if(element == null || !element.isJsonObject()) {
					Logger.warn("Invalid data entry! Expected JsonObject, but got '" + element.getClass().getName() + "'. Skipping this data...");
					continue;
				}
				JsonObject jsonEntry = element.getAsJsonObject();
				for(Map.Entry<String, JsonElement> entry : jsonEntry.entrySet()) {
					Object data;
					if(entry.getKey().equals(KEY_SETTINGS_LIST)) {
						List<Setting> settingListTemplate = new ArrayList<>();
						data = gson.fromJson(entry.getValue(), settingListTemplate.getClass());
					} else {
						data = gson.fromJson(entry.getValue(), Object.class);
					}
					//List<?> list = (List<?>) data;
					//System.out.println(list.get(0).getClass());
					storageMap.put(entry.getKey(), data);
				}
			}
		}
	}

}
