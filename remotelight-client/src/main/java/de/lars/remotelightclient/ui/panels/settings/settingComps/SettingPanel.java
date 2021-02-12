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

package de.lars.remotelightclient.ui.panels.settings.settingComps;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import de.lars.remotelightcore.settings.Setting;

public abstract class SettingPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2743868556642656374L;
	private int height = 40;
	private SettingChangedListener listener;
	private Setting setting;
	
	public SettingPanel(Setting setting) {
		this.setting = setting;
		
		setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
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
