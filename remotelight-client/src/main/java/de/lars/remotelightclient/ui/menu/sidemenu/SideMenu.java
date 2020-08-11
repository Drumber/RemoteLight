package de.lars.remotelightclient.ui.menu.sidemenu;

import javax.swing.JPanel;

public abstract class SideMenu extends JPanel {
	private static final long serialVersionUID = -1648378491673862404L;

	/**
	 * Will remove and re-add all menu items.
	 */
	public abstract void updateMenuItems();

}
