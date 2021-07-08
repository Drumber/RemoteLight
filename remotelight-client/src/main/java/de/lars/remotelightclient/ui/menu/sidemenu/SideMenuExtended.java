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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightclient.ui.menu.MenuItem;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.lang.i18n;

public class SideMenuExtended extends SideMenu {
	private static final long serialVersionUID = 7722774169681804161L;
	
	private MainFrame mainFrame;
	private JPanel root;

	/**
	 * Create the panel.
	 */
	public SideMenuExtended(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		UiUtils.bindBackground(this, Style.panelAccentBackground());
		setPreferredSize(new Dimension(150, 300));
		setLayout(new BorderLayout());
		
		root = new JPanel();
		root.setBackground(null);
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		
		TScrollPane scrollPane = new TScrollPane(root);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(8);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, root.getHeight()));
		add(scrollPane, BorderLayout.CENTER);
		
		// add all menu items
		addMenuItems();
	}
	
	/**
	 * Adds all menu items to the sidebar
	 */
	private void addMenuItems() {
		// add extend/collapse button
		JButton btnExtend = new JButton(""); //$NON-NLS-1$
		btnExtend.setName("extend"); //$NON-NLS-1$
		btnExtend.setIcon(Style.getFontIcon(MenuIcon.MENU)); //$NON-NLS-1$
		this.configureButton(btnExtend);
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
	
	/**
	 * Will remove and re-add all menu items.
	 */
	@Override
	public void updateMenuItems() {
		root.removeAll();
		addMenuItems();
	}
	
	private JButton getMenuButton(MenuItem item) {
		String displayname = item.getI18nID() == null ? item.getDisplayname() : i18n.getString(item.getI18nID());
		Icon icon = item.getIconCode() == null ? item.getIcon() : Style.getFontIcon(item.getIconCode());
		JButton btn = new JButton(displayname);
		btn.setName(item.getId());
		btn.setIcon(icon);
		this.configureButton(btn);
		return btn;
	}
	
	private void configureButton(JButton btn) {
        btn.setBorderPainted(false);
        btn.setFocusable(false);
        btn.setBackground(null);
        btn.setMaximumSize(new Dimension(150, 30));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addMouseListener(buttonHoverListener);
        btn.addActionListener(buttonActionListener);
        btn.setRolloverEnabled(false);
        if(mainFrame.getSelectedMenu().equals(btn.getName())) {
        	btn.setBackground(Style.accent().get());
        }
	}
	
	private MouseAdapter buttonHoverListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			if(!mainFrame.getSelectedMenu().equals(btn.getName())) {
				btn.setBackground(Style.hoverBackground().get());
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			if(!mainFrame.getSelectedMenu().equals(btn.getName())) {
				btn.setBackground(null);
			}
		}
	};
	
	
	private ActionListener buttonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();

			if(!btn.getName().equals("extend")) { //$NON-NLS-1$
				UiUtils.getComponentByName(root, new JButton(), mainFrame.getSelectedMenu()).setBackground(null); //reset background of previous selected button
				btn.setBackground(null);
				btn.setBackground(Style.accent().get());
				mainFrame.setSelectedMenu(btn.getName());
			}
			
			if(btn.getName().equals("extend")) { //$NON-NLS-1$
				mainFrame.replaceSideMenu(new SideMenuSmall(mainFrame));
				Main.getInstance().getSettingsManager().getSettingObject("ui.sidemenu.extended").setValue(false);
			} else {
				mainFrame.showMenuPanel(btn.getName());
			}
		}
	};

}
