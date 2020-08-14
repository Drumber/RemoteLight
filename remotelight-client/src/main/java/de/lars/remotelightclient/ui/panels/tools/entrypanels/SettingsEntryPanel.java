package de.lars.remotelightclient.ui.panels.tools.entrypanels;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanelEntry;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;

public class SettingsEntryPanel extends ToolsPanelEntry {

	@Override
	public String getName() {
		return "Settings";
	}
	
	@Override
	public Icon getIcon() {
		return Style.getFontIcon(MenuIcon.SETTINGS);
	}
	
	@Override
	public JPanel getMenuPanel() {
		JPanel root = new JPanel();
		root.setBackground(Color.cyan);
		root.add(new JLabel("TEST"));
		return root;
	}

}
