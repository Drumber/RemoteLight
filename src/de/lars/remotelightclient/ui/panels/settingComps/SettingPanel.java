package de.lars.remotelightclient.ui.panels.settingComps;

import javax.swing.JPanel;

public abstract class SettingPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2743868556642656374L;
	
	public SettingPanel() {
	}
	
	public abstract void setValue();

}
