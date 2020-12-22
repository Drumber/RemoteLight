package de.lars.remotelightrestapi.handlers;

import java.util.Map;

import org.tinylog.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.Output;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightrestapi.RestAPI;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

public class InformationHandler extends RequestHandler {

	@Override
	public IStatus getStatus() {
		return Response.Status.OK;
	}

	@Override
	public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
		SettingsManager sm = RemoteLightCore.getInstance().getSettingsManager();
		JsonObject root = new JsonObject();
		root.addProperty("version", RemoteLightCore.VERSION);
		root.addProperty("devbuild", RemoteLightCore.DEVBUILD);
		root.addProperty("github", RemoteLightCore.GITHUB);
		root.addProperty("pixels", RemoteLightCore.getLedNum());
		root.addProperty("brightness", String.valueOf((int) sm.getSettingObject("out.brightness").get()));
		root.addProperty("animations_speed", String.valueOf((int) sm.getSettingObject("animations.speed").get()));
		
		RemoteLightCore core = RemoteLightCore.getInstance();
		if(core != null) {
			Output activeOut = core.getOutputManager().getActiveOutput();
			root.addProperty("active_output", activeOut != null ? activeOut.getId() : null);
		}
		return json(root);
	}
	
	@Override
	public Response put(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
		SettingsManager sm = RemoteLightCore.getInstance().getSettingsManager();
		String exception = null;
		if(session.getMethod() == Method.PUT) {
			try {
				// get content of PUT request
				int contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
				byte[] buffer = new byte[contentLength];
				session.getInputStream().read(buffer, 0, contentLength);
				
				// parse content
				String content = new String(buffer);
				JsonElement jsonEl = RestAPI.getGson().fromJson(content, JsonElement.class);
				if(!jsonEl.isJsonObject())
					throw new IllegalStateException("JSON root element must be of type JSON object.");
				JsonObject root = jsonEl.getAsJsonObject();
				
				// brightness
				if(root.has("brightness")) {
					int brightness = root.get("brightness").getAsInt();
					RemoteLightCore.getInstance().getOutputManager().setBrightness(brightness);
					sm.getSettingObject("out.brightness").setValue(brightness);
				}
				
				// animations speed
				if(root.has("animations_speed")) {
					int speed = root.get("animations_speed").getAsInt();
					sm.getSettingObject("animations.speed").setValue(speed);
					RemoteLightCore.getInstance().getAnimationManager().setDelay(speed);
				}
				
			} catch(Exception e) {
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

}
