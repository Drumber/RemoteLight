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

package de.lars.remotelightclient.ui.panels.sidemenu;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;

import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;

public class SideMenuSmall extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1604913473609403672L;
	private JPanel sideMenu;
	private MainFrame mainFrame;

	/**
	 * Create the panel.
	 */
	public SideMenuSmall(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.sideMenu = mainFrame.getSideMenu();
		setBackground(Style.panelAccentBackground);
		setPreferredSize(new Dimension(40, 300));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JButton btnExtend = new JButton("");
		btnExtend.setName("extend");
		btnExtend.setIcon(Style.getFontIcon(MenuIcon.MENU));
		this.configureButton(btnExtend);
		add(btnExtend);
		
		JButton btnOutput = new JButton("");
		btnOutput.setName("output");
		btnOutput.setIcon(Style.getFontIcon(MenuIcon.OUTPUTS));
		this.configureButton(btnOutput);
		add(btnOutput);
		
		JButton btnColors = new JButton("");
		btnColors.setName("colors");
		btnColors.setIcon(Style.getFontIcon(MenuIcon.COLOR_PALETTE));
		this.configureButton(btnColors);
		add(btnColors);
		
		JButton btnAnimations = new JButton("");
		btnAnimations.setName("animations");
		btnAnimations.setIcon(Style.getFontIcon(MenuIcon.ANOMATION));
		this.configureButton(btnAnimations);
		add(btnAnimations);
		
		JButton btnScenes = new JButton("");
		btnScenes.setName("scenes");
		btnScenes.setIcon(Style.getFontIcon(MenuIcon.SCENE));
		this.configureButton(btnScenes);
		add(btnScenes);
		
		JButton btnMusicSync = new JButton("");
		btnMusicSync.setName("musicsync");
		btnMusicSync.setIcon(Style.getFontIcon(MenuIcon.MUSICSYNC));
		this.configureButton(btnMusicSync);
		add(btnMusicSync);
		
		JButton btnScreenColor = new JButton("");
		btnScreenColor.setName("screencolor");
		btnScreenColor.setIcon(Style.getFontIcon(MenuIcon.SCREENCOLOR));
		this.configureButton(btnScreenColor);
		add(btnScreenColor);
		
		JButton btnScripts = new JButton("");
		btnScripts.setName("scripts");
		btnScripts.setIcon(Style.getFontIcon(MenuIcon.SCRIPT));
		this.configureButton(btnScripts);
		add(btnScripts);
		
		JButton btnSettings = new JButton("");
		btnSettings.setIcon(Style.getFontIcon(MenuIcon.SETTINGS));
		btnSettings.setName("settings");
		this.configureButton(btnSettings);
		add(btnSettings);
		
		Component glue = Box.createGlue();
		add(glue);
		
		JButton btnAbout = new JButton("");
		btnAbout.setIcon(Style.getFontIcon(MenuIcon.ABOUT));
		btnAbout.setName("about");
		this.configureButton(btnAbout);
		add(btnAbout);

	}
	
	
	private void configureButton(JButton btn) {
		UiUtils.configureButton(btn, false);
		btn.setBorderPainted(false);
		btn.setFocusable(false);
		btn.setBackground(null);
		btn.setMaximumSize(new Dimension(40, 30));
		btn.addMouseListener(buttonHoverListener);
		btn.addActionListener(buttonActionListener);
        if(mainFrame.getSelectedMenu().equals(btn.getName())) {
        	btn.setBackground(Style.accent);
        }
	}
	
	private MouseAdapter buttonHoverListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			if(!mainFrame.getSelectedMenu().equals(btn.getName())) {
				btn.setBackground(Style.hoverBackground);
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
			
			if(!btn.getName().equals("extend")) {
				UiUtils.getComponentByName(SideMenuSmall.this, new JButton(), mainFrame.getSelectedMenu()).setBackground(null); //reset background of previous selected button
				btn.setBackground(Style.accent);
				mainFrame.setSelectedMenu(btn.getName());
			}
			
			if(btn.getName().equals("extend")) {
				sideMenu.removeAll();
				sideMenu.add(new SideMenuExtended(mainFrame), BorderLayout.CENTER);
				Main.getInstance().getSettingsManager().getSettingObject("ui.sidemenu.extended").setValue(true);
				sideMenu.updateUI();
			} else {
				mainFrame.menuSelected(btn.getName());
			}
		}
	};
	

}
