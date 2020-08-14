package de.lars.remotelightclient.ui.panels.tools;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

public abstract class ToolsPanelEntry {
	
	/**
	 * Return the name of this entry.
	 * @return		the name as String
	 */
	public abstract String getName();
	
	/**
	 * Optional entry icon.
	 * @return		the icon or null
	 */
	public Icon getIcon() {
		return null;
	};
	
	/**
	 * Optional action buttons.
	 * @return		array of JButtons or null
	 */
	public JButton[] getActionButtons() {
		return null;
	}
	
	/**
	 * Optional panel that is shown (if not null) when the entry is clicked.
	 * @return		the menu JPanel or null
	 */
	public JPanel getMenuPanel() {
		return null;
	}
	
	/**
	 * Called when the entry is clicked.
	 */
	public void onClick() {}
	
	/**
	 * Called when a new navigation is submitted (up or down).
	 * @param navItem		the event navigation item
	 */
	public void onNavigate(ToolsPanelNavItem navItem) {};
	
}
