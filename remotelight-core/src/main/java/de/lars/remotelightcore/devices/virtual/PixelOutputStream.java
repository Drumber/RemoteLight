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

package de.lars.remotelightcore.devices.virtual;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PixelOutputStream {
	
	private Set<PixelStreamReceiver> setReceiver;
	private boolean active = false;
	
	public PixelOutputStream(Collection<PixelStreamReceiver> receivers) {
		setReceiver = new HashSet<PixelStreamReceiver>();
		setReceiver.addAll(receivers);
	}
	
	public PixelOutputStream() {
		this(Collections.emptySet());
	}

	/**
	 * Write a color array that represents the strip and send it to all
	 * attached pixel stream receiver.
	 * @param strip		color array representing the strip
	 */
	protected void writeStrip(Color[] strip) {
		if(!active) return;
		for(Iterator<PixelStreamReceiver> it = setReceiver.iterator(); it.hasNext();) {
			PixelStreamReceiver rec = it.next();
			if(rec == null)
				it.remove(); // remove null receiver from set
			else
				rec.receivedPixelData(strip);
		}
	}
	
	/**
	 * Check if this output stream is active
	 * @return		true if the stream is enabled,
	 * 				false otherwise
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Enable or disable output
	 * @param active	whether the output should be enabled or disabled
	 */
	protected void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean addReceiver(PixelStreamReceiver receiver) {
		return setReceiver.add(receiver);
	}
	
	public boolean removeReceiver(PixelStreamReceiver receiver) {
		return setReceiver.remove(receiver);
	}

}
