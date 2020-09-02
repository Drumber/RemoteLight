package de.lars.remotelightcore.devices.virtual;

import java.awt.Color;

public interface PixelStreamReceiver {
	
	void receivedPixelData(Color[] strip);

}
