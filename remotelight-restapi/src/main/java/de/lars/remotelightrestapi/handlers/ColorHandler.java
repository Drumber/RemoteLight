package de.lars.remotelightrestapi.handlers;

import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightrestapi.RestAPI;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

public class ColorHandler extends RequestHandler {

	@Override
	public IStatus getStatus() {
		return Response.Status.OK;
	}

	@Override
	public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
		Color last = RemoteLightCore.getInstance().getColorManager().getLastColor();
		if(last == null) last = Color.BLACK;
		JsonObject root = serializeColor(last);
		return json(root);
	}
	
	@Override
	public Response put(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
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
					throw new IllegalArgumentException("Request data must be of type JSON object.");
				
				// parse color
				Color color = deserializeJson(jsonEl.getAsJsonObject());
				if(color == null)
					throw new JsonParseException("Could not parse color from JSON.");
				// show color
				RemoteLightCore.getInstance().getColorManager().showColor(color);
			} catch(Exception e) {
				if(RestAPI.shouldLog) Logger.error(e, "Failed to parse body content from PUT request.");
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
	
	protected JsonObject serializeColor(Color color) {
		JsonObject root = new JsonObject();
		root.addProperty("rgb", color.getRGB());
		root.addProperty("red", color.getRed());
		root.addProperty("green", color.getGreen());
		root.addProperty("blue", color.getBlue());
		return root;
	}
	
	/**
	 * Converts color from JSOn object.
	 * Supported fromats:
	 * <ul>
	 * <li>seperate red, green and blue values
	 * <li>RGB value as integer
	 * <li>HEX String
	 * </ul>
	 * @param obj
	 * @return
	 */
	protected Color deserializeJson(JsonObject obj) {
		// 1. priority: R,G,B values
		JsonElement jeRed	= obj.get("red");
		JsonElement jeGreen	= obj.get("green");
		JsonElement jeBlue	= obj.get("blue");
		if(jeRed != null && jeGreen != null && jeBlue != null
				&& jeRed.isJsonPrimitive() && jeGreen.isJsonPrimitive() && jeBlue.isJsonPrimitive()) {
			return new Color(jeRed.getAsInt(), jeGreen.getAsInt(), jeBlue.getAsInt());
		}
		
		// 2. priority: RGB value as integer
		JsonElement jeRgb	= obj.get("rgb");
		if(jeRgb != null && jeRgb.isJsonPrimitive()) {
			return new Color(jeRgb.getAsInt());
		}
		
		// 3. priority: HEX
		JsonElement jeHex	= obj.get("hex");
		if(jeHex != null && jeHex.isJsonPrimitive()) {
			return Color.decodeHex(jeHex.getAsString());
		}
		return null;
	}
	
	
	public static class ColorHandlerPixels extends ColorHandler {

		@Override
		public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
			OutputManager om = RemoteLightCore.getInstance().getOutputManager();
			Color[] lastPixels = om.getLastColors();
			if(lastPixels == null) {
				return json(new Color[0]);
			}
			
			// filter by pixel index
			List<String> pixParams = session.getParameters().get("pixel");
			if(pixParams != null && pixParams.size() > 0 && !pixParams.get(0).isEmpty()) {
				JsonArray arrPixels = new JsonArray();
				for(int i = 0; i < lastPixels.length; i++) {
					if(pixParams.contains(String.valueOf(i))) { // only show colors from indexes defined as url param
						JsonObject objPix = serializeColor(lastPixels[i]);
						objPix.addProperty("index", i);
						arrPixels.add(objPix);
					}
				}
				return json(arrPixels);
			}
			
			JsonArray arrPixels = new JsonArray();
			for(int i = 0; i < lastPixels.length; i++) {
				JsonObject objPix = serializeColor(lastPixels[i]);
				objPix.addProperty("index", i);
				arrPixels.add(objPix);
			}
			return json(arrPixels);
		}
		
		@Override
		public Response put(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
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
					if(!jsonEl.isJsonArray())
						throw new IllegalArgumentException("Request data must be of type JSON array.");
					
					// parse pixel colors
					JsonArray arrPixels = jsonEl.getAsJsonArray();
					Color[] strip = null;
					if(arrPixels.size() < RemoteLightCore.getLedNum()) {
						strip = RemoteLightCore.getInstance().getOutputManager().getLastColors();
					} else {
						strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
					}
					// parse every color value
					for(int i = 0; i < arrPixels.size(); i++) {
						if(i >= strip.length) break; // given json array is larger than strip
						if(!arrPixels.get(i).isJsonObject()) continue;
						JsonObject jPix = arrPixels.get(i).getAsJsonObject();
						Color color = deserializeJson(jPix);
						if(color == null) continue;
						strip[i] = color;
					}
					
					// add to output manager
					OutputManager.addToOutput(strip);
				} catch(Exception e) {
					if(RestAPI.shouldLog) Logger.error(e, "Failed to parse body content from PUT request.");
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

}
