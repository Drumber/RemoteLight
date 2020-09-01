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

package de.lars.remotelightclient.ui.components.frames;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import de.lars.remotelightclient.Main;
import de.lars.remotelightcore.event.Listener;
import de.lars.remotelightcore.event.events.Stated.State;
import de.lars.remotelightcore.event.events.types.ShutdownEvent;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingObject;

/**
 * Adds simple access to always-on-top option and saves
 * the size on window close.
 */
public class BasicFrame extends JFrame {
	private static final long serialVersionUID = -4465454973463383578L;
	
	public final String frameID;
	public final String settingPrefix;
	protected final SettingsManager sm;
	private Runnable closeAction;
	private boolean fullClose = true;
	
	/**
	 * Create a new basic JFrame with always-on-top option and saved window size.
	 * <p> Will automatically register and unregister frame.
	 * 
	 * @param frameID	the unique frame id (is used for saving options)
	 * @param sm		a setting manager instance
	 */
	public BasicFrame(final String frameID, SettingsManager sm) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/Icon-128x128.png")));
		this.sm = sm;
		this.frameID = frameID;
		this.settingPrefix = "frame." + frameID;
		// register frame
		Main.getInstance().registerFrame(this);
		
		initSavedAlwaysTop();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				// set saved size
				initSavedSize();
			}
		});
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// add window close listener
		addWindowListener(windowCloseListener);
		// register shutdown listener
		Main.getInstance().getCore().getEventHandler().register(shutdownListener);
		// show the frame
		setVisible(true);
	}
	
	/**
	 * Tries to set the frames size to the saved setting data.
	 */
	protected synchronized void initSavedSize() {
		SettingObject settingSize = sm.getSettingObject(settingPrefix + ".size");
		if(settingSize != null && settingSize.getValue() instanceof int[]) {
			int[] dataSize = (int[]) settingSize.getValue();
			if(dataSize.length >= 2) {
				setPreferredSize(new Dimension(dataSize[0], dataSize[1]));
				setSize(new Dimension(dataSize[0], dataSize[1]));
			}
		}
	}
	
	/**
	 * Save the window size
	 * 
	 * @param width		the window width
	 * @param height	the window height
	 */
	public synchronized void saveSize(int width, int height) {
		int[] dataSize = {width, height};
		// add setting if not already added
		sm.addSetting(new SettingObject(settingPrefix + ".size", "Window size", dataSize), false);
		// set new value
		sm.getSettingObject(settingPrefix + ".size").setValue(dataSize);
	}
	
	/**
	 * Set the always on top option
	 */
	protected synchronized void initSavedAlwaysTop() {
		SettingBoolean settingAlwayTop = sm.getSetting(SettingBoolean.class, settingPrefix + ".alwaystop");
		if(settingAlwayTop != null) {
			setAlwaysOnTop(settingAlwayTop.getValue());
		}
	}
	
	/**
	 * Enable always on top
	 * 
	 * @param alwaysTop	whether the frame should be always on top
	 */
	public synchronized void setAlwaysTop(boolean alwaysTop) {
		// add setting if not already added
		sm.addSetting(new SettingBoolean(settingPrefix + ".alwaystop", "Always on top", SettingCategory.Intern, null, alwaysTop), false);
		// set new value
		sm.getSetting(SettingBoolean.class, settingPrefix + ".alwaystop").setValue(alwaysTop);
		setAlwaysOnTop(alwaysTop);
	}
	
	/**
	 * Return whether always-on-top is currently enabled.
	 * 
	 * @return	true if enabled, false otherwise
	 */
	public synchronized boolean isAlwayTop() {
		return isAlwaysOnTop();
	}
	
	/**
	 * Set whether this frame should be reusable when closing or not.
	 * 
	 * @param fullClose	set to true when the frame should not be used
	 * 			again after closing
	 */
	public void setFullClose(boolean fullClose) {
		this.fullClose = fullClose;
	}
	
	/**
	 * Set the action that should be performed when the window is closed
	 * 
	 * @param runnable	the action to perform
	 */
	public void setCloseAction(Runnable runnable) {
		this.closeAction = runnable;
	}
	
	WindowListener windowCloseListener = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			// save window size
			if(getExtendedState() == NORMAL) {
				saveSize(getWidth(), getHeight());
			}
			dispose();
			if(fullClose) {
				if(closeAction != null) {
					closeAction.run();
				}
				// unregister frame
				Main.getInstance().unregisterFrame(BasicFrame.this);
			}
		};
	};
	
	Listener<ShutdownEvent> shutdownListener = new Listener<ShutdownEvent>() {
		@Override
		public void onEvent(ShutdownEvent event) {
			if(event.getState() == State.PRE && isShowing())
				saveSize(getWidth(), getHeight());
		}
	};

}
