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

package de.lars.remotelightcore.io;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.io.jsondeserializer.DeviceDeserializer;
import de.lars.remotelightcore.io.jsondeserializer.SettingDeserializer;
import de.lars.remotelightcore.io.jsonserializer.DeviceSerializer;
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
	public final String KEY_SETTINGS_LIST = "settings";
	public final String KEY_DEVICES_LIST = "devices";
	
	public final Type TYPE_SETTINGS_LIST;
	public final Type TYPE_DEVICES_LIST;
	
	public FileStorage(File file) {
		this.file = file;
		TYPE_SETTINGS_LIST = new TypeToken<List<Setting>>() {}.getType();
		TYPE_DEVICES_LIST = new TypeToken<List<Device>>() {}.getType();
		this.gson = new GsonBuilder()
				.registerTypeAdapter(TYPE_SETTINGS_LIST, new SettingSerializer())
				.registerTypeAdapter(TYPE_SETTINGS_LIST, new SettingDeserializer())
				.registerTypeAdapter(TYPE_DEVICES_LIST, new DeviceSerializer())
				.registerTypeAdapter(TYPE_DEVICES_LIST, new DeviceDeserializer())
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
	
	public synchronized void save() throws IOException, JsonIOException {
		// create directory if not exists
		if(!file.exists()) {
			file.getAbsoluteFile().getParentFile().mkdirs();
		// check if file is writable
		} else if(!Files.isWritable(file.toPath())) {
			throw new IOException("Can not write to data file! File is locked or the application has no write access.");
		}
		
		// json root element
		JsonObject jsonRoot = new JsonObject();
		// add version tag
		jsonRoot.addProperty(KEY_VERSION, RemoteLightCore.VERSION);
		// json array including all data
		JsonArray array = new JsonArray();
		
		// loop through all data and build json object
		for(Map.Entry<String, Object> entry : storageMap.entrySet()) {
			JsonElement sElement;
			
			// serialize data object to json
			if(entry.getKey().equals(KEY_SETTINGS_LIST)) {
				// serialize setting list
				sElement = gson.toJsonTree(entry.getValue(), TYPE_SETTINGS_LIST);
			} else if(entry.getKey().equals(KEY_DEVICES_LIST)) {
				// serialize device list
				sElement = gson.toJsonTree(entry.getValue(), TYPE_DEVICES_LIST);
			} else {
				// serialize other data
				sElement = gson.toJsonTree(entry.getValue());
			}
			
			// create json object and add json data
			JsonObject jsonEntry = new JsonObject();
			jsonEntry.add(entry.getKey(), sElement);
			// add to json array
			array.add(jsonEntry);
		}
		
		// add json array to root
		jsonRoot.add(KEY_DATA, array);
		// save to file
		try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
			gson.toJson(jsonRoot, writer);
			// should actually be flushed automatically on auto close,
			// but apparently does not work for some users
			writer.flush();
		}
	}
	
	public synchronized void load() throws IOException, JsonParseException {
		if(!file.exists() || !file.isFile()) {
			Logger.info("Data file does not exists: " + file.getAbsolutePath());
			return;
		}
		if(!Files.isReadable(file.toPath()))
			throw new IOException("Can not read data file! File is locked or the application has no read access.");
			
		try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
			// read json object from file
			JsonObject jsonRoot = gson.fromJson(reader, JsonObject.class);
			if(jsonRoot == null)
				throw new JsonParseException("Could not parse JSON data from file '" + file.getPath() + "'! Gson returned null.");
			// get json data array
			JsonElement arrayElement = jsonRoot.get(KEY_DATA);
			if(arrayElement == null || !arrayElement.isJsonArray())
				throw new JsonParseException("Invalid data! Expected JsonArray, but got " + (arrayElement == null ? "null" : arrayElement.getClass().getName()));
			JsonArray array = arrayElement.getAsJsonArray();
			
			// add all data to hash map
			for(JsonElement element : array) {
				if(element == null || !element.isJsonObject()) {
					Logger.warn("Invalid data entry! Expected JsonObject, but got '" + (arrayElement == null ? "null" : arrayElement.getClass().getName()) + "'. Skipping this data...");
					continue;
				}
				JsonObject jsonEntry = element.getAsJsonObject();
				for(Map.Entry<String, JsonElement> entry : jsonEntry.entrySet()) {
					Object data;
					
					// deserialize json data
					if(entry.getKey().equals(KEY_SETTINGS_LIST)) {
						// deserialize settings list
						data = gson.fromJson(entry.getValue(), TYPE_SETTINGS_LIST);
					} else if (entry.getKey().equals(KEY_DEVICES_LIST)) {
						// deserialize devices list
						data = gson.fromJson(entry.getValue(), TYPE_DEVICES_LIST);
					} else {
						// deserialize other data
						data = gson.fromJson(entry.getValue(), Object.class);
					}
					
					storageMap.put(entry.getKey(), data);
				}
			}
		}
	}

}
