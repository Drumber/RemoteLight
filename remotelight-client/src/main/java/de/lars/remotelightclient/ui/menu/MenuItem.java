package de.lars.remotelightclient.ui.menu;

import javax.swing.Icon;

import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import jiconfont.IconCode;

/**
 * A MenuItem can be registered and shown in the {@link MainFrame}.
 * <p> It holds information about:
 * <ul>
 * <li>ID (must be unique)
 * <li>Displayname (optional, equal to the ID if not available)
 * <li>Icon (show placeholder icon if not available)
 * <li>target menu panel name (must be equal to the name of the {@link MenuPanel})
 * </ul></p>
 */
public class MenuItem {
	
	private String id;
	private String displayname;
	private Icon icon;
	private IconCode iconCode;
	private String targetMenuPanel;
	private String i18nID;
	
	/**
	 * Create a new menu item info holder.
	 * 
	 * @param id			unique menu id
	 * @param displayname	displayed menu name
	 * @param icon			menu icon
	 * @param targetMenuPanel
	 * 						the menu panel this item is bound to
	 */
	public MenuItem(String id, String displayname, Icon icon, String targetMenuPanel) {
		this.id = id;
		this.displayname = displayname;
		this.icon = icon;
		this.targetMenuPanel = targetMenuPanel;
	}
	
	/**
	 * Create a new menu item info holder with dynamic icon.
	 * 
	 * @param id			unique menu id
	 * @param displayname	displayed menu name
	 * @param iconCode		menu icon code (color can be dynamically updated)
	 * @param targetMenuPanel
	 * 						the menu panel this item is bound to
	 */
	public MenuItem(String id, String displayname, IconCode iconCode, String targetMenuPanel) {
		this.id = id;
		this.displayname = displayname;
		this.iconCode = iconCode;
		this.targetMenuPanel = targetMenuPanel;
	}
	
	/**
	 * Create a new simple menu item info holder.
	 * 
	 * @param id			unique menu id (displayname will be the same)
	 * @param targetMenuPanel
	 * 						the menu panel this item is bound to
	 */
	public MenuItem(String id, String targetMenuPanel) {
		this(id, id, MenuIcon.ERROR, targetMenuPanel);
	}
	
	/**
	 * Create a new menu item info holder.
	 * 
	 * @param id			unique menu id (will be also used for the target menu panel)
	 * @param displayname	displayed menu name
	 * @param icon			menu icon
	 */
	public MenuItem(String id, String displayname, Icon icon) {
		this(id, displayname, icon, id);
	}
	
	/**
	 * Create a new menu item info holder.
	 * 
	 * @param id			unique menu id (will be also used for the target menu panel)
	 * @param displayname	displayed menu name (use i18n string if available)
	 * @param i18nID		string id of the i18n bundle (only intern u18n bundle supported)
	 * @param icon			menu icon
	 */
	public MenuItem(String id, String displayname, String i18nID, IconCode iconCode) {
		this(id, displayname, iconCode, id);
		this.i18nID = i18nID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public IconCode getIconCode() {
		return iconCode;
	}

	public void setIconCode(IconCode iconCode) {
		this.iconCode = iconCode;
	}

	public String getTargetMenuPanel() {
		return targetMenuPanel;
	}

	public void setTargetMenuPanel(String targetMenuPanel) {
		this.targetMenuPanel = targetMenuPanel;
	}

	public String getI18nID() {
		return i18nID;
	}

	public void setI18nID(String i18nID) {
		this.i18nID = i18nID;
	}
	
}
