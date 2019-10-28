/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient.ui.panels.scenes;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.scene.Scene;
import de.lars.remotelightclient.scene.SceneManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.comps.BigTextButton;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.utils.WrapLayout;

public class ScenesPanel extends MenuPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4955214837008536168L;
	private MainFrame mainFrame;
	private SceneManager scm = Main.getInstance().getSceneManager();
	private SettingsManager sm = Main.getInstance().getSettingsManager();
	private JPanel bgrScenes;
	private JPanel bgrSettings;

	/**
	 * Create the panel.
	 */
	public ScenesPanel() {
		sm.addSetting(new SettingObject("animations.speed", null, 50));
		
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
