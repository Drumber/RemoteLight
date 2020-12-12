package de.lars.remotelightrestapi.handlers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.devices.DeviceManager;
import de.lars.remotelightcore.out.Output;
import de.lars.remotelightcore.utils.OutputUtil;
import de.lars.remotelightrestapi.RestAPI;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

public class OutputsHandler extends RequestHandler {

	@Override
	public IStatus getStatus() {
		return Response.Status.OK;
	}

	@Override
	public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
		DeviceManager dm = RemoteLightCore.getInstance().getDeviceManager();
		List<Device> listOutputs = dm.getDevices();
		
		// filter by id
		List<String> listIds = session.getParameters().get("id");
		if(listIds != null && listIds.size() > 0 && !listIds.get(0).isEmpty()) {
			listOutputs = listOutputs.stream()
					.filter(o -> listIds.contains(o.getId()))
					.collect(Collectors.toList());
		}
		
		// filter by output type
		if(session.getParameters().containsKey("type")) {
			List<String> listTypes = listToLowerCase(session.getParameters().get("type"));
			if(listTypes.size() > 0) {
				listOutputs = listOutputs.stream()
						.filter(o -> listTypes.contains(OutputUtil.getOutputTypeAsString(o).toLowerCase()))
						.collect(Collectors.toList());
			}
		}
		
		// filter by active outputs
		if(session.getParameters().containsKey("active")) {
			listOutputs = listOutputs.stream()
					.filter(o -> o.getConnectionState() == ConnectionState.CONNECTED)
					.collect(Collectors.toList());
		}
		return json(listOutputs);
	}
	
	
	public static class OutputActivateHandler extends RequestHandler {

		@Override
		public IStatus getStatus() {
			return Response.Status.OK;
		}

		@Override
		public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
			Output activeOutput = RemoteLightCore.getInstance().getOutputManager().getActiveOutput();
			String id = activeOutput != null ? activeOutput.getId() : null;
			JsonObject jsonObj = new JsonObject();
			jsonObj.addProperty("active_output", id);
			return json(jsonObj);
		}
		
		@Override
		public Response put(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
			String exception = null;
			DeviceManager dm = RemoteLightCore.getInstance().getDeviceManager();
			if(session.getMethod() == Method.PUT) {
				try {
					// get content of PUT request
					int contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
					byte[] buffer = new byte[contentLength];
					session.getInputStream().read(buffer, 0, contentLength);
					
					// parse content
					String content = new String(buffer);
					JsonElement jsonEl = RestAPI.getGson().fromJson(content, JsonElement.class);
					JsonElement activeEl = jsonEl.getAsJsonObject().get("active_output");
					if(activeEl == null)
						throw new IllegalStateException("JSON object must have 'active_output' paramter.");
					
					if(activeEl.isJsonNull()) {
						// deactivate current output
						RemoteLightCore.getInstance().getOutputManager().setEnabled(false);
					} else {
						String outputId = activeEl.getAsString();
						if(!dm.isIdUsed(outputId))
							throw new IllegalArgumentException("Invalid device id! There is no device with id '" + outputId + "'.");
						Device device = dm.getDevice(outputId);
						// activate output
						RemoteLightCore.getInstance().getOutputManager().setActiveOutput(device);
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
		
	}

}
