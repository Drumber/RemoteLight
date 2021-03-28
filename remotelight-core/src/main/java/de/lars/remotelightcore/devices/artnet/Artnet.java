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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 620972378928905059L;
	private transient ArtNetClient artnet;
	private boolean broadcast;
	private String address;
	private int subnet;
	private int startUniverse;
	private boolean continuousUniverseOverflow;
	
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

	public boolean isContinuousUniverseOverflow() {
		return continuousUniverseOverflow;
	}

	public void setContinuousUniverseOverflow(boolean continuousUniverseOverflow) {
		this.continuousUniverseOverflow = continuousUniverseOverflow;
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
	}

	@Override
	public void send(Color[] pixels) {
		int dataLength = 512;
		if(pixels.length * 3 <= 512) {
			dataLength = pixels.length * 3;
		}
		byte[] dmxData = new byte[dataLength];
		int universe = startUniverse;
		int index = 0;
		
		for(int i = 0; i < pixels.length; i++) {
			byte[] rgbColor = {(byte) pixels[i].getRed(), (byte) pixels[i].getGreen(), (byte) pixels[i].getBlue()};
			
			if(continuousUniverseOverflow) {
				// continuous universe overflow: split RGB values across multiple universes
				// e.g.: [uni 0] ... 510(B), 511(R), 512(G), [uni 1] 001(B), 002(R) ...
				
				for(int rgb = 0; rgb < rgbColor.length; rgb++) {
					if(index + rgb >= dmxData.length) { // universe is full; output universe and use next one
						sendDmxData(dmxData, universe);
						universe++;
						
						dataLength = 512;
						if((pixels.length - i) * 3 <= 512) {
							dataLength = (pixels.length - i) * 3 - rgb;
						}
						dmxData = new byte[dataLength];
						index = 0 - rgb;
					}
					
					dmxData[index + rgb] = rgbColor[rgb];
				}
			} else {
				// do not split RGB values over multiple universes
				
				// universe is full, send universe and use next one
				if(index + 3 >= dmxData.length) {
					sendDmxData(dmxData, universe);
					universe++;
					
					dataLength = 512;
					if((pixels.length - i) * 3 <= 512) {
						dataLength = (pixels.length - i) * 3;
					}
					dmxData = new byte[dataLength];
					index = 0;
				}
				
				dmxData[index + 0] = rgbColor[0];
				dmxData[index + 1] = rgbColor[1];
				dmxData[index + 2] = rgbColor[2];
			}
			index += 3;
		}
		sendDmxData(dmxData, universe);
	}
	
	private void sendDmxData(byte[] dmxData, int universe) {
		if(broadcast) {
			artnet.broadcastDmx(subnet, universe, dmxData);
		} else {
			artnet.unicastDmx(address.trim(), subnet, universe, dmxData);
		}
	}
	
	public int getEndUniverse(int startUniverse, int pixels) {
		return startUniverse + (3 * pixels / 512);
	}

}
