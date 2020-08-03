package de.lars.remotelightclient.events;

import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightcore.event.events.Event;

/**
 * MenuEvent called on menu change
 */
public class MenuEvent implements Event {
	
	private final MenuPanel menuPanel;
	private final String menuName, prevMenuName;
	
	public MenuEvent(final MenuPanel menu, String menuName, String prevMenuName) {
		this.menuPanel = menu;
		this.menuName = menuName;
		this.prevMenuName = prevMenuName;
	}

	/**
	 * Get the new menu panel
	 * 
	 * @return Returns the new menu panel
	 */
	public MenuPanel getMenuPanel() {
		return menuPanel;
	}

	/**
	 * Get the name of the new menu
	 * 
	 * @return Returns the menu name
	 */
	public String getMenuName() {
		return menuName;
	}

	/**
	 * Get the name of the previous menu
	 * 
	 * @return 	Returns the previous menu name
	 * 			or null
	 */
	public String getPrevMenuName() {
		return prevMenuName;
	}

}
