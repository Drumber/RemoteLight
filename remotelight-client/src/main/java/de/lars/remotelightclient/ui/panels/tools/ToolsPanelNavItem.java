package de.lars.remotelightclient.ui.panels.tools;

import javax.swing.JPanel;

/**
 * Used to navigate through tool panel.
 */
public class ToolsPanelNavItem {
	
	private String title;
	private JPanel content;
	
	/**
	 * Create a new navigation item.
	 * 
	 * @param title		the panel title
	 * @param content	the content to show
	 */
	public ToolsPanelNavItem(String title, JPanel content) {
		super();
		this.title = title;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public JPanel getContent() {
		return content;
	}

	public void setContent(JPanel content) {
		this.content = content;
	}

}
