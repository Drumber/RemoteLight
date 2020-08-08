package de.lars.remotelightclient.ui.components.frames;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import de.lars.remotelightclient.Main;
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
	public final SettingsManager sm;
	private Runnable closeAction;
	
	/**
	 * Create a new basic JFrame with always-on-top option and saved window size.
	 * <p> Will automatically register and unregister frame.
	 * 
	 * @param frameID	the unique frame id (is used for saving options)
	 * @param sm		a setting manager instance
	 */
	public BasicFrame(final String frameID, SettingsManager sm) {
		this.sm = sm;
		this.frameID = frameID;
		this.settingPrefix = "frame." + frameID;
		// register frame
		Main.getInstance().registerFrame(this);
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// add window close listener
		addWindowListener(windowCloseListener);
		
		initSavedSize();
		initSavedAlwaysTop();
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
			if(closeAction != null) {
				closeAction.run();
			}
			// unregister frame
			Main.getInstance().unregisterFrame(BasicFrame.this);
		};
	};

}
