package de.lars.remotelightclient.ui.panels.settingComps;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

public abstract class SettingPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2743868556642656374L;
	
	public SettingPanel() {
		setPreferredSize(new Dimension(40, 40));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
	}
	
	public abstract void setValue();

}
