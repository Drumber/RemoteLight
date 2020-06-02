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

package de.lars.remotelightclient.ui.panels.scenes;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.comps.BigTextButton;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.scene.Scene;
import de.lars.remotelightcore.scene.SceneManager;

public class ScenesPanel extends MenuPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4955214837008536168L;
	private MainFrame mainFrame;
	private SceneManager scm = RemoteLightCore.getInstance().getSceneManager();
	private JPanel bgrScenes;
	private JPanel bgrSettings;

	/**
	 * Create the panel.
	 */
	public ScenesPanel() {
		mainFrame = Main.getInstance().getMainFrame();
		mainFrame.showControlBar(true);
		this.addControlBar();
		setBackground(Style.panelBackground);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel bgrScroll = new JPanel();
		add(bgrScroll);
		bgrScroll.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		bgrScroll.add(scrollPane);
		
		bgrScenes = new JPanel();
		WrapLayout wlayout = new WrapLayout(FlowLayout.LEFT);
		bgrScenes.setLayout(wlayout);
		bgrScenes.setBackground(Style.panelBackground);
		scrollPane.setViewportView(bgrScenes);
		
		bgrSettings = new JPanel();
		bgrSettings.setBackground(Style.panelDarkBackground);
		bgrSettings.setVisible(false);
		add(bgrSettings);
		
		this.addScenePanels();
	}
	
	private void addScenePanels() {
		bgrScenes.removeAll();
		for(Scene s : scm.getScenes()) {
			BigTextButton btn = new BigTextButton(s.getDisplayname(), "");
			btn.setName(s.getName());
			btn.addMouseListener(btnSceneListener);
			
			if(scm.getActiveScene() != null && scm.getActiveScene().getName().equals(s.getName())) {
				btn.setBorder(BorderFactory.createLineBorder(Style.accent));
			}
			bgrScenes.add(btn);
		}
		updateUI();
	}
	
	private MouseAdapter btnSceneListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			BigTextButton btn = (BigTextButton) e.getSource();
			
			if(scm.getActiveScene() != null && scm.getActiveScene().getName().equals(btn.getName())) {
				scm.stop();
			} else {
				for(Scene s : scm.getScenes()) {
					if(s.getName().equals(btn.getName())) {
						scm.start(s);
						break;
					}
				}
			}
			addScenePanels();
		}
	};
	
	private void addControlBar() {
		DefaultControlBar controlBar = new DefaultControlBar();
		mainFrame.setControlBarPanel(controlBar);
	}

}
