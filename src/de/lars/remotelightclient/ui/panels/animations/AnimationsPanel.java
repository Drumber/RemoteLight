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
package de.lars.remotelightclient.ui.panels.animations;

import javax.swing.JPanel;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.animation.AnimationManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.comps.BigTextButton;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.ui.panels.controlbars.comps.SpeedSlider;
import de.lars.remotelightclient.utils.WrapLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AnimationsPanel extends MenuPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5490750381498859042L;
	private MainFrame mainFrame;
	private AnimationManager am = Main.getInstance().getAnimationManager();
	private SettingsManager sm = Main.getInstance().getSettingsManager();
	private JPanel bgrAnimations;
	private JPanel bgrSettings;

	/**
	 * Create the panel.
	 */
	public AnimationsPanel() {
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
		
		bgrAnimations = new JPanel();
		WrapLayout wlayout = new WrapLayout(FlowLayout.LEFT);
		bgrAnimations.setLayout(wlayout);
		bgrAnimations.setBackground(Style.panelBackground);
		scrollPane.setViewportView(bgrAnimations);
		
		bgrSettings = new JPanel();
		bgrSettings.setBackground(Style.panelDarkBackground);
		bgrSettings.setVisible(false);
		add(bgrSettings);
		
		this.addAnimationPanels();
	}
	
	private void addAnimationPanels() {
		bgrAnimations.removeAll();
		for(Animation a : am.getAnimations()) {
			BigTextButton btn = new BigTextButton(a.getDisplayname(), "");
			btn.setName(a.getName());
			btn.addMouseListener(btnAniListener);
			
			if(am.getActiveAnimation() != null && am.getActiveAnimation().getName().equals(a.getName())) {
				btn.setBorder(BorderFactory.createLineBorder(Style.accent));
			}
			bgrAnimations.add(btn);
		}
		updateUI();
	}
	
	private MouseAdapter btnAniListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			BigTextButton btn = (BigTextButton) e.getSource();
			
			if(am.getActiveAnimation() != null && am.getActiveAnimation().getName().equals(btn.getName())) {
				am.stop();
			} else {
				for(Animation a : am.getAnimations()) {
					if(a.getName().equals(btn.getName())) {
						am.start(a);
						break;
					}
				}
			}
			addAnimationPanels();
		}
	};
	
	private void addControlBar() {
		DefaultControlBar controlBar = new DefaultControlBar();
		SpeedSlider speedSlider = new SpeedSlider(Style.panelDarkBackground);
		speedSlider.getSlider().addChangeListener(sliderSpeedListener);
		speedSlider.getSlider().setValue((int) sm.getSettingObject("animations.speed").getValue());
		controlBar.setActionPanel(speedSlider);
		mainFrame.setControlBarPanel(controlBar);
	}
	
	private ChangeListener sliderSpeedListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			int speed = ((JSlider) e.getSource()).getValue();
			sm.getSettingObject("animations.speed").setValue(speed);
			am.setDelay(speed);
		}
	};

}
