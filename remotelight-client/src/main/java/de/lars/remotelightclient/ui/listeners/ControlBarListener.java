package de.lars.remotelightclient.ui.listeners;

import de.lars.remotelightclient.ui.panels.controlbars.ControlBar;

public interface ControlBarListener {
	
	/**
	 * Triggered when the visibility of the control bar is changed.
	 * @param visible whether the control bar is visible or hidden
	 */
	void onControlBarVisibilityChange(boolean visible);
	
	/**
	 * Triggered when the control bar is changed, i.e. replaced by another one or null.
	 * @param controlBar new control bar or null
	 */
	void onControlBarChange(ControlBar controlBar);

}
