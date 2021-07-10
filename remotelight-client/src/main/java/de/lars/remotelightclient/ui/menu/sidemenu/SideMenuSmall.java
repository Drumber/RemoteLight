/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightclient.ui.menu.sidemenu;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.menu.MenuItem;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;

public class SideMenuSmall extends SideMenu {
	private static final long serialVersionUID = 1604913473609403672L;
	
	/**
	 * Create the panel.
	 */
	public SideMenuSmall(MainFrame mainFrame) {
		super(mainFrame);
		//setPreferredSize(new Dimension(40, 300));
	}
	
	/**
	 * Adds all menu items to the sidebar
	 */
	@Override
	protected void addMenuItems() {
		// add extend/collapse button
		JButton btnExtend = new JButton("");
		btnExtend.setName("extend");
		btnExtend.setIcon(Style.getFontIcon(MenuIcon.MENU));
		this.configureMenuButton(btnExtend);
		root.add(btnExtend);
		
		for(MenuItem item : mainFrame.getMenuItems()) {
			JButton btn = getMenuButton(item);
			// add glue before about button
			if(item.getId().equalsIgnoreCase("about")) {
				Component glue = Box.createGlue();
				root.add(glue);
			}
			// add button
			root.add(btn);
		}
	}
	
	private JButton getMenuButton(MenuItem item) {
		Icon icon = item.getIconCode() == null ? item.getIcon() : Style.getFontIcon(item.getIconCode());
		JButton btn = new JButton("");
		btn.setName(item.getId());
		btn.setIcon(icon);
		this.configureMenuButton(btn);
		return btn;
	}	
	
	@Override
	protected void onMenuItemClicked(JButton btn, String name) {
		super.onMenuItemClicked(btn, name);
		
		if(name.equals("extend")) {
			mainFrame.replaceSideMenu(new SideMenuExtended(mainFrame));
			Main.getInstance().getSettingsManager().getSettingObject("ui.sidemenu.extended").setValue(true);
		}
	}

}
