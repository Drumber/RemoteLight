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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.lang.i18n;

public class SideMenuExtended extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7722774169681804161L;
	private JPanel sideMenu;
	private MainFrame mainFrame;

	/**
	 * Create the panel.
	 */
	public SideMenuExtended(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.sideMenu = mainFrame.getSideMenu();
		setBackground(Style.panelAccentBackground);
		setPreferredSize(new Dimension(150, 300));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JButton btnExtend = new JButton(""); //$NON-NLS-1$
		btnExtend.setName("extend"); //$NON-NLS-1$
		btnExtend.setIcon(Style.getFontIcon(MenuIcon.MENU)); //$NON-NLS-1$
		this.configureButton(btnExtend);
		add(btnExtend);
		
		JButton btnOutput = new JButton(i18n.getString("Basic.Output")); //$NON-NLS-1$
		btnOutput.setName("output"); //$NON-NLS-1$
		btnOutput.setIcon(Style.getFontIcon(MenuIcon.OUTPUTS)); //$NON-NLS-1$
		this.configureButton(btnOutput);
		add(btnOutput);
		
		JButton btnColors = new JButton(i18n.getString("Basic.Colors")); //$NON-NLS-1$
		btnColors.setName("colors"); //$NON-NLS-1$
		btnColors.setIcon(Style.getFontIcon(MenuIcon.COLOR_PALETTE)); //$NON-NLS-1$
		this.configureButton(btnColors);
		add(btnColors);
		
		JButton btnAnimations = new JButton(i18n.getString("Basic.Animations")); //$NON-NLS-1$
		btnAnimations.setName("animations"); //$NON-NLS-1$
		btnAnimations.setIcon(Style.getFontIcon(MenuIcon.ANOMATION)); //$NON-NLS-1$
		this.configureButton(btnAnimations);
		add(btnAnimations);
		
		JButton btnScenes = new JButton(i18n.getString("Basic.Scenes")); //$NON-NLS-1$
		btnScenes.setName("scenes"); //$NON-NLS-1$
		btnScenes.setIcon(Style.getFontIcon(MenuIcon.SCENE)); //$NON-NLS-1$
		this.configureButton(btnScenes);
		add(btnScenes);
		
		JButton btnMusicSync = new JButton(i18n.getString("Basic.MusicSync")); //$NON-NLS-1$
		btnMusicSync.setName("musicsync"); //$NON-NLS-1$
		btnMusicSync.setIcon(Style.getFontIcon(MenuIcon.MUSICSYNC)); //$NON-NLS-1$
		this.configureButton(btnMusicSync);
		add(btnMusicSync);
		
		JButton btnScreenColor = new JButton(i18n.getString("Basic.ScreenColor")); //$NON-NLS-1$
		btnScreenColor.setName("screencolor"); //$NON-NLS-1$
		btnScreenColor.setIcon(Style.getFontIcon(MenuIcon.SCREENCOLOR)); //$NON-NLS-1$
		this.configureButton(btnScreenColor);
		add(btnScreenColor);
		
		JButton btnScripts = new JButton(i18n.getString("Basic.Scripts")); //$NON-NLS-1$
		btnScripts.setName("scripts");
		btnScripts.setIcon(Style.getFontIcon(MenuIcon.SCRIPT)); //$NON-NLS-1$
		this.configureButton(btnScripts);
		add(btnScripts);
		
		JButton btnSettings = new JButton(i18n.getString("Basic.Settings")); //$NON-NLS-1$
		btnSettings.setIcon(Style.getFontIcon(MenuIcon.SETTINGS)); //$NON-NLS-1$
		btnSettings.setName("settings"); //$NON-NLS-1$
		this.configureButton(btnSettings);
		add(btnSettings);

		Component glue = Box.createGlue();
		add(glue);
		
		JButton btnAbout = new JButton(i18n.getString("Basic.About")); //$NON-NLS-1$
		btnAbout.setIcon(Style.getFontIcon(MenuIcon.ABOUT)); //$NON-NLS-1$
		btnAbout.setName("about"); //$NON-NLS-1$
		this.configureButton(btnAbout);
		add(btnAbout);
		
	}
	
	
	private void configureButton(JButton btn) {
		UiUtils.configureButton(btn, false);
        btn.setBorderPainted(false);
        btn.setFocusable(false);
        btn.setBackground(null);
        btn.setForeground(Style.textColor);
        btn.setMaximumSize(new Dimension(150, 30));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
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

			if(!btn.getName().equals("extend")) { //$NON-NLS-1$
				UiUtils.getComponentByName(SideMenuExtended.this, new JButton(), mainFrame.getSelectedMenu()).setBackground(null); //reset background of previous selected button
				btn.setBackground(null);
				btn.setBackground(Style.accent);
				mainFrame.setSelectedMenu(btn.getName());
			}
			
			if(btn.getName().equals("extend")) { //$NON-NLS-1$
				sideMenu.removeAll();
				sideMenu.add(new SideMenuSmall(mainFrame), BorderLayout.CENTER);
				Main.getInstance().getSettingsManager().getSettingObject("ui.sidemenu.extended").setValue(false);
				sideMenu.updateUI();
			} else {
				mainFrame.menuSelected(btn.getName());
			}
		}
	};

}
