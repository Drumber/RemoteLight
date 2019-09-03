package de.lars.remotelightclient.ui.panels.connectionComps;

import javax.swing.JPanel;

public abstract class DeviceSettingsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1279445549682121981L;

	/**
	 * Create the panel.
	 */
	public DeviceSettingsPanel() {
	}
	
	public abstract void save();

}
