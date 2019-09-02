package de.lars.remotelightclient.ui.panels;

import javax.swing.JPanel;

import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.ui.Style;

public class Settings extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3954953325346082615L;

	/**
	 * Create the panel.
	 */
	public Settings(SettingsManager sm) {
		setBackground(Style.panelBackground);
	}

}
