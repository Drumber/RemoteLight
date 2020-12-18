package de.lars.remotelightrestapi.handlers;

import java.util.Map;

import com.google.gson.JsonObject;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.Output;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
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
		JsonObject root = new JsonObject();
		root.addProperty("version", RemoteLightCore.VERSION);
		root.addProperty("devbuild", RemoteLightCore.DEVBUILD);
		root.addProperty("github", RemoteLightCore.GITHUB);
		root.addProperty("pixels", RemoteLightCore.getLedNum());
		
		RemoteLightCore core = RemoteLightCore.getInstance();
		if(core != null) {
			Output activeOut = core.getOutputManager().getActiveOutput();
			root.addProperty("active_output", activeOut != null ? activeOut.getId() : null);
		}
		return json(root);
	}

}
