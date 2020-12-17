package de.lars.remotelightrestapi.handlers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.io.FileStorage;
import de.lars.remotelightcore.io.jsondeserializer.SettingDeserializer;
import de.lars.remotelightcore.io.jsonserializer.SettingSerializer;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightrestapi.RestAPI;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

public class SettingsHandler extends RequestHandler {

	@Override
	public IStatus getStatus() {
		return Response.Status.OK;
	}

	@Override
	public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
		SettingsManager sm = RemoteLightCore.getInstance().getSettingsManager();
		List<Setting> listSettings = sm.getSettings();
		
		// filter by id
		List<String> listIds = session.getParameters().get("id");
		if(listIds != null && listIds.size() > 0 && !listIds.get(0).isEmpty()) {
			listSettings = listSettings.stream()
					.filter(s -> listIds.contains(s.getId()))
					.collect(Collectors.toList());
		}
		return json(serializeSettingList(listSettings));
	}
	
	
	@Override
	public Response put(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
		String exception = null;
		SettingsManager sm = RemoteLightCore.getInstance().getSettingsManager();
		if(session.getMethod() == Method.PUT) {
			try {
				// get content of PUT request
				int contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
				byte[] buffer = new byte[contentLength];
				session.getInputStream().read(buffer, 0, contentLength);
				
				// parse content
				String content = new String(buffer);
				JsonElement jsonEl = RestAPI.getGson().fromJson(content, JsonElement.class);
				
				JsonArray jarrSettings = null;
				if(jsonEl.isJsonArray()) { // array of setting elements
					jarrSettings = jsonEl.getAsJsonArray();
				} else { // single setting element
					jarrSettings = new JsonArray();
					jarrSettings.add(jsonEl);
				}
				
				// parse setting elements
				Gson gson = new GsonBuilder()
						.registerTypeAdapter(FileStorage.TYPE_SETTINGS_LIST, new SettingSerializer())
						.registerTypeAdapter(FileStorage.TYPE_SETTINGS_LIST, new SettingDeserializer())
						.serializeNulls()
						.setPrettyPrinting()
						.create();
				List<Setting> listSettings = null;
				try {
					listSettings = gson.fromJson(jarrSettings, FileStorage.TYPE_SETTINGS_LIST);
				} catch(Throwable t) {
					throw new JsonParseException("Failed to parse setting elements from JSON", t);
				}
				
				// set new settings
				if(listSettings != null) {
					listSettings.forEach(s -> {
						Setting setting = sm.addSetting(s, true); // add or update setting
						if(setting != null) {
							setting.set(s.get());
						}
					});
					// return settings from request
					return json(serializeSettingList(listSettings));
				}
				
			} catch (Exception e) {
				if(RestAPI.shouldLog) Logger.error("Failed to parse body content from PUT request: " + e.getMessage());
				exception = e.getMessage();
			}
		}
		
		if(exception != null) {
			Response response = json(exception);
			response.setStatus(Response.Status.BAD_REQUEST);
			return response;
		}
		return get(uriResource, urlParams, session);
	}
	
	
	private JsonElement serializeSettingList(List<Setting> listSetting) {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(FileStorage.TYPE_SETTINGS_LIST, new SettingSerializer())
				.registerTypeAdapter(FileStorage.TYPE_SETTINGS_LIST, new SettingDeserializer())
				.serializeNulls()
				.setPrettyPrinting()
				.create();
		return gson.toJsonTree(listSetting, FileStorage.TYPE_SETTINGS_LIST);
	}

}
