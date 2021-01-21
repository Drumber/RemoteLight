package de.lars.remotelightrestapi.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.lars.remotelightcore.EffectManagerHelper;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.effect.AbstractEffect;
import de.lars.remotelightcore.effect.Effect;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.scene.Scene;
import de.lars.remotelightrestapi.RestAPI;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

public class EffectsHandler extends RequestHandler {

	@Override
	public IStatus getStatus() {
		return Response.Status.OK;
	}

	@Override
	public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
		Map<String, JsonArray> effectsMap = getAllEffects();
		List<String> listTypes = new ArrayList<String>();
		// url paramters
		String effectTypeUrl = urlParams.get("type");
		if(effectTypeUrl != null) {
			listTypes.add(effectTypeUrl);
		}
		
		// filter by effect type
		if(session.getParameters().containsKey("type")) {
			listTypes.addAll(listToLowerCase(session.getParameters().get("type")));
		}
		if(listTypes.size() > 0) {
			effectsMap = getFilteredEffects(listTypes);
		}
		
		// filter by name
		if(session.getParameters().containsKey("name")) {
			List<String> listNames = listToLowerCase(session.getParameters().get("name"));
			effectsMap = getByName(listNames, effectsMap);
		}
		
		return json(effectsMap);
	}
	
	public Map<String, JsonArray> getAllEffects() {
		Map<String, JsonArray> effectsMap = new HashMap<String, JsonArray>();
		effectsMap.put("animations", serializeEffects(getAnimations()));
		effectsMap.put("scenes", serializeEffects(getScenes()));
		effectsMap.put("music", serializeEffects(getMusicEffects()));
		return effectsMap;
	}
	
	public Map<String, JsonArray> getFilteredEffects(List<String> listTypes) {
		Map<String, JsonArray> effectsMap = new HashMap<String, JsonArray>();
		if(listTypes.contains("animations"))
			effectsMap.put("animations", serializeEffects(getAnimations()));
		if(listTypes.contains("scenes"))
			effectsMap.put("scenes", serializeEffects(getScenes()));
		if(listTypes.contains("music") || listTypes.contains("musicsync"))
			effectsMap.put("music", serializeEffects(getMusicEffects()));
		return effectsMap;
	}
	
	public Map<String, JsonArray> getByName(List<String> listNames, Map<String, JsonArray> effectsMap) {
		for(Map.Entry<String, JsonArray> entry : effectsMap.entrySet()) {
			Iterator<JsonElement> it = entry.getValue().iterator();
			while(it.hasNext()) {
				if(!listNames.contains(it.next().getAsJsonObject().get("name").getAsString().toLowerCase()))
					it.remove(); // remove json element from array if name is not in the filter list
			}
		}
		return effectsMap;
	}
	
	protected List<Animation> getAnimations() {
		return RemoteLightCore.getInstance().getAnimationManager().getAnimations();
	}
	
	protected List<Scene> getScenes() {
		return RemoteLightCore.getInstance().getSceneManager().getScenes();
	}
	
	protected List<MusicEffect> getMusicEffects() {
		return RemoteLightCore.getInstance().getMusicSyncManager().getMusicEffects();
	}
	
	protected static JsonObject serializeEffect(AbstractEffect effect) {
		JsonObject jsonRoot = new JsonObject();
		jsonRoot.addProperty("name", effect.getName());
		if(effect instanceof Effect) {
			jsonRoot.add("options", RestAPI.getGson().toJsonTree(((Effect) effect).getOptions()));
		}
		return jsonRoot;
	}
	
	protected static JsonArray serializeEffects(List<? extends AbstractEffect> effects) {
		JsonArray jsonRoot = new JsonArray();
		effects.forEach(e -> jsonRoot.add(serializeEffect(e)));
		return jsonRoot;
	}
	
	
	public static class EffectsActiveHandler extends RequestHandler {

		@Override
		public IStatus getStatus() {
			return Response.Status.OK;
		}

		@Override
		public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
			String effectTypeUrl = urlParams.get("type");
			if(effectTypeUrl == null)
				text("No effect type specified.", Response.Status.INTERNAL_ERROR);
			AbstractEffect activeEffect = getActiveEffect(effectTypeUrl.toLowerCase());
			JsonObject jsonObj = new JsonObject();
			jsonObj.add("active_effect", activeEffect == null ? null : EffectsHandler.serializeEffect(activeEffect));
			return json(jsonObj);
		}
		
		@Override
		public Response put(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
			String effectTypeUrl = urlParams.get("type");
			if(effectTypeUrl == null)
				text("No effect type specified.", Response.Status.INTERNAL_ERROR);
			
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
					JsonElement activeEl = jsonEl.getAsJsonObject().get("active_effect");
					if(activeEl == null)
						throw new IllegalStateException("JSON object must have 'active_effect' paramter.");
					
					JsonElement nameEl = null;
					if(activeEl.isJsonObject()) {
						nameEl = activeEl.getAsJsonObject().get("name");
					} else {
						nameEl = activeEl; // support for direct effect name {"active_effect":"effect"}
					}
					
					if(activeEl.isJsonNull() || (nameEl != null && nameEl.isJsonNull())) {
						// disable effect
						stopEffect(effectTypeUrl.toLowerCase());
					} else {
						if(nameEl == null)
							throw new IllegalStateException("JSON object must have 'name' parameter.");
						// enable effect
						String effectName = nameEl.getAsString();
						startEffect(effectTypeUrl.toLowerCase(), effectName);
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
		
		protected AbstractEffect getActiveEffect(String type) {
			RemoteLightCore core = RemoteLightCore.getInstance();
			if(type.startsWith("animations")) {
				return core.getAnimationManager().getActiveAnimation();
			} else if(type.startsWith("scenes")) {
				return core.getSceneManager().getActiveScene();
			} else if(type.startsWith("music")) {
				return core.getMusicSyncManager().getActiveEffect();
			} else {
				return null;
			}
		}
		
		protected void stopEffect(String type) {
			RemoteLightCore core = RemoteLightCore.getInstance();
			if(type.startsWith("animations")) {
				core.getAnimationManager().stop();
			} else if(type.startsWith("scenes")) {
				core.getSceneManager().stop();
			} else if(type.startsWith("music")) {
				core.getMusicSyncManager().stop();
			} else {
				throw new IllegalArgumentException("Invalid effect type '" + type + "'.");
			}
		}
		
		protected void startEffect(String type, String effect) {
			RemoteLightCore core = RemoteLightCore.getInstance();
			EffectManagerHelper helper = core.getEffectManagerHelper();
			boolean notFound = false;
			if(type.startsWith("animations")) {
				AbstractEffect e = helper.getEffect(core.getAnimationManager(), effect);
				notFound = e == null;
				core.getAnimationManager().start((Animation) e);
			} else if(type.startsWith("scenes")) {
				AbstractEffect e = helper.getEffect(core.getSceneManager(), effect);
				notFound = e == null;
				core.getSceneManager().start((Scene) e);
			} else if(type.startsWith("music")) {
				AbstractEffect e = helper.getEffect(core.getMusicSyncManager(), effect);
				notFound = e == null;
				core.getMusicSyncManager().start((MusicEffect) e);
			} else {
				throw new IllegalArgumentException("Invalid effect type '" + type + "'.");
			}
			if(notFound)
				throw new IllegalArgumentException("Invalid effect name. Could not find effect for '" + effect + "'.");
		}
		
	}

}
