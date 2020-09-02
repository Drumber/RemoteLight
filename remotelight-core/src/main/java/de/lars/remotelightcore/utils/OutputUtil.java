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

package de.lars.remotelightcore.utils;

import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.devices.arduino.Arduino;
import de.lars.remotelightcore.devices.artnet.Artnet;
import de.lars.remotelightcore.devices.link.chain.Chain;
import de.lars.remotelightcore.devices.remotelightserver.RemoteLightServer;
import de.lars.remotelightcore.devices.virtual.VirtualOutput;
import de.lars.remotelightcore.out.Output;

public class OutputUtil {
	
	public static String getOutputTypeAsString(Output o) {
		if(o instanceof Arduino) {
			return "Arduino";
		}
		if(o instanceof RemoteLightServer) {
			return "RemoteLightServer";
		}
		if(o instanceof Artnet) {
			return "Artnet";
		}
		if(o instanceof VirtualOutput) {
			return "VirtualOutput";
		}
		if(o instanceof Chain) {
			return "Chain";
		}
		return "Unknown output";
	}
	
	public static Class<? extends Device> getDeviceClass(String deviceType) {
		switch (deviceType) {
		case "Arduino":
			return Arduino.class;
		case "RemoteLightServer":
			return RemoteLightServer.class;
		case "Artnet":
			return Artnet.class;
		case "VirtualOutput":
			return VirtualOutput.class;
		case "Chain":
			return Chain.class;
		default:
			return null;
		}
	}
	
	public static String getDeviceConnectionInfo(Device d) {
		if(d instanceof Arduino) {
			return ((Arduino) d).getSerialPort();
		}
		if(d instanceof RemoteLightServer) {
			return ((RemoteLightServer)d).getIp();
		}
		if(d instanceof Artnet) {
			return ((Artnet)d).isBroadcast() ? "Broadcast" : ((Artnet)d).getUnicastAddress();
		}
		return "No connection info";
	}
	
	public static String getConnectionStateAsString(ConnectionState s) {
		if(s == ConnectionState.CONNECTED) {
			return "Connected";
		}
		if(s == ConnectionState.DISCONNECTED) {
			return "Disconnected";
		}
		if(s == ConnectionState.FAILED) {
			return "Connection failed";
		}
		return "";
	}

}
