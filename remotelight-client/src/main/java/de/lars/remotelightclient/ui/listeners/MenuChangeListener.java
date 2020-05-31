package de.lars.remotelightclient.ui.listeners;

import de.lars.remotelightclient.ui.MenuPanel;

public interface MenuChangeListener {

	/**
	 * Triggered when another menu is displayed
	 * @param menuPanel new menu panel
	 */
	void onMenuChange(MenuPanel menuPanel);
	
}
