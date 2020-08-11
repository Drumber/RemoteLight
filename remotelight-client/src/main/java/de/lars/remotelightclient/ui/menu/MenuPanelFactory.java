package de.lars.remotelightclient.ui.menu;

import de.lars.remotelightclient.ui.panels.MenuPanel;

/**
 * Create and instantiate a menu panel by name.
 */
public interface MenuPanelFactory {
	
	/**
	 * Get the menu panel by name.
	 * 
	 * @param name	the name defined by {@link MenuPanel#getName()}
	 * @return		a newly created instance of the menu panel, or
	 * 				{@code null} to request the next menu panel factory.
	 */
	MenuPanel getMenuPanel(String name);

}
