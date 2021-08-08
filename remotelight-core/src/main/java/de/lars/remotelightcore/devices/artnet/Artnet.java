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

package de.lars.remotelightcore.devices.artnet;

import org.tinylog.Logger;

import ch.bildspur.artnet.ArtNetClient;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.Color;

public class Artnet extends Device {
	private static final long serialVersionUID = 620972378928905059L;
	
	public static final int MAX_UNIVERSE_SIZE = 512;
	
	private transient ArtNetClient artnet;
	private boolean broadcast;
	private String address;
	private int subnet;
	private int startUniverse;
	private int universeSize = MAX_UNIVERSE_SIZE;
	
	public Artnet(String id) {
		super(id, 0);
		this.artnet = new ArtNetClient();
	}
	
	public void setBroadcast(boolean broadcast) {
		this.broadcast = broadcast;
	}
	
	public boolean isBroadcast() {
		return broadcast;
	}
	
	public void setUnicastAddress(String address) {
		this.address = address;
	}
	
	public String getUnicastAddress() {
		return address;
	}
	
	public void setSubnet(int subnet) {
		this.subnet = subnet;
	}
	
	public int getSubnet() {
		return subnet;
	}
	
	public void setStartUniverse(int startUniverse) {
		this.startUniverse = startUniverse;
	}
	
	public int getStartUniverse() {
		return startUniverse;
	}

	public int getUniverseSize() {
		return universeSize;
	}

	public void setUniverseSize(int universeSize) {
		this.universeSize = universeSize;
	}

	@Override
	public ConnectionState connect() {
		artnet.start();
		
		// Output is laggy if output delay > 45
		if(((SettingInt) RemoteLightCore.getInstance().getSettingsManager().getSettingFromId("out.delay")).get() > 45) {
			((SettingInt) RemoteLightCore.getInstance().getSettingsManager().getSettingFromId("out.delay")).setValue(45);
			Logger.info("Set output delay to 45 ms for Artnet to work fine.");
		}
		
		if(artnet.isRunning()) {
			return ConnectionState.CONNECTED;
		}
		return ConnectionState.FAILED;
	}

	@Override
	public ConnectionState disconnect() {
		artnet.stop();
		if(artnet.isRunning()) {
			return ConnectionState.CONNECTED;
		}
		return ConnectionState.DISCONNECTED;
	}

	@Override
	public ConnectionState getConnectionState() {
		if(artnet.isRunning()) {
			return ConnectionState.CONNECTED;
		}
		return ConnectionState.DISCONNECTED;
	}

	@Override
	public void onLoad() {
		if(artnet == null) {
			artnet = new ArtNetClient();
		}
		if(universeSize == 0) {
			universeSize = MAX_UNIVERSE_SIZE;
		}
		if(universeSize < 3) {
			universeSize = 3;
		}
	}

	@Override
	public void send(Color[] pixels) {
		final int dataLength = pixels.length * 3;
		final int MAX_LENGTH = universeSize; // maximal dmx data length
		
		int arrayLength = dataLength > MAX_LENGTH ? MAX_LENGTH : dataLength;
		byte[] dmxData = new byte[arrayLength];
		int currUniverse = startUniverse;
		int offset = 0;
		
		// loop over each pixel
		for(int i = 0; i < pixels.length; i++) {
			byte[] rgbData = {(byte) pixels[i].getRed(), (byte) pixels[i].getGreen(), (byte) pixels[i].getBlue()};
			
			// loop over RGB data array
			for(int d = 0; d < rgbData.length; d++) {
				// check if max length of universe is reached
				if(offset >= MAX_LENGTH) {
					// current universe is full; output universe and use next universe
					sendDmxData(currUniverse, dmxData);
					
					// use next universe
					currUniverse++;
					// reset offset
					offset = 0;
					
					// create new buffer
					int usedUniverses = currUniverse - startUniverse;
					int remainingDataLength = dataLength - usedUniverses * MAX_LENGTH;
					int size = remainingDataLength > MAX_LENGTH ? MAX_LENGTH : remainingDataLength;
					dmxData = new byte[size];
				}
				
				// add to output data buffer
				dmxData[offset] = rgbData[d];
				// increment offset
				offset++;
			}
		}
		// output universe
		sendDmxData(currUniverse, dmxData);
	}
	
	private void sendDmxData(int universe, byte[] dmxData) {
		if(broadcast) {
			artnet.broadcastDmx(subnet, universe, dmxData);
		} else {
			artnet.unicastDmx(address.trim(), subnet, universe, dmxData);
		}
	}
	
	public int getEndUniverse(int startUniverse, int universeSize, int pixels) {
		return startUniverse + (3 * pixels / universeSize);
	}

}
