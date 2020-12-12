package de.lars.remotelightrestapi.handlers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.devices.DeviceManager;
import de.lars.remotelightcore.utils.OutputUtil;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

public class HandlerOutputs extends RequestHandler {

	@Override
	public IStatus getStatus() {
		return Response.Status.OK;
	}

	@Override
	public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
		DeviceManager dm = RemoteLightCore.getInstance().getDeviceManager();
		// uri parameters
		String pOutput = urlParams.get("output");
		if(pOutput != null && dm.isIdUsed(pOutput)) {
			return json(dm.getDevice(pOutput));
		}

		// query parameters
		List<Device> listOutputs = dm.getDevices();
		
		// filter by output type
		List<String> listTypes = session.getParameters().get("type").stream()
				.map(t -> t.toLowerCase())
				.collect(Collectors.toList()); // convert every param to lowercase
		if(listTypes != null && listTypes.size() > 0) {
			listOutputs = listOutputs.stream()
					.filter(o -> listTypes.contains(OutputUtil.getOutputTypeAsString(o).toLowerCase()))
					.collect(Collectors.toList());
		}
		
		// filter by active outputs
		if(session.getParameters().containsKey("active")) {
			listOutputs = listOutputs.stream()
					.filter(o -> o.getConnectionState() == ConnectionState.CONNECTED)
					.collect(Collectors.toList());
		}
		return json(listOutputs);
	}

}
