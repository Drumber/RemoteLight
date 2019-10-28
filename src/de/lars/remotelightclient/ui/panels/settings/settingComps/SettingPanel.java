/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient.ui.panels.settings.settingComps;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JPanel;

import de.lars.remotelightclient.settings.Setting;

public abstract class SettingPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2743868556642656374L;
	private int width = 2000;
	private int height = 40;
	private SettingChangedListener listener;
	private Setting setting;
	
	public SettingPanel(Setting setting) {
		this.setting = setting;
		
		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
	}
	
	public Setting getSetting() {
		return setting;
	}
	
	public interface SettingChangedListener {
		public void onSettingChanged(SettingPanel settingPanel);
	}
	
	public abstract void setValue();
	public abstract void updateComponents();

	public synchronized void setSettingChangedListener(SettingChangedListener l) {
		this.listener = l;
	}
	
	public void onChanged(SettingPanel settingPanel) {
		if(listener != null) {
			listener.onSettingChanged(settingPanel);
		}
	}
	
}
