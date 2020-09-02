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
