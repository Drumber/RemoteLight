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

package de.lars.remotelightclient.ui.panels.animations;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.GlowButton;
import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.ui.panels.controlbars.comps.SpeedSlider;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.animation.AnimationManager;
import de.lars.remotelightcore.event.Listener;
import de.lars.remotelightcore.event.events.types.EffectOptionsUpdateEvent;
import de.lars.remotelightcore.event.events.types.EffectToggleEvent.AnimationToggleEvent;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingObject;

public class AnimationsPanel extends MenuPanel {
	private static final long serialVersionUID = 5490750381498859042L;
	
	private MainFrame mainFrame;
	private AnimationManager am;
	private SettingsManager sm;
	private AnimationOptionsPanel optionsPanel;
	private JPanel bgrAnimations;
	private JPanel bgrSettings;

	/**
	 * Create the panel.
	 */
	public AnimationsPanel() {
		am = RemoteLightCore.getInstance().getAnimationManager();
		sm = Main.getInstance().getSettingsManager();
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
		bgrSettings.setLayout(new BorderLayout(0, 0));
		add(bgrSettings);
		
		optionsPanel = new AnimationOptionsPanel(bgrSettings);
		bgrSettings.add(optionsPanel, BorderLayout.CENTER);
		this.addAnimationPanels();
		
		Main.getInstance().getCore().getEventHandler().register(onAnimationOptionUpdate);
		Main.getInstance().getCore().getEventHandler().register(onAnimationToggle);
	}
	
	private void addAnimationPanels() {
		bgrAnimations.removeAll();
		for(Animation a : am.getAnimations()) {
			GlowButton btn = new GlowButton(a.getDisplayname(), "", 4, a.getClass());
			btn.setGlowEnabled(sm.getSetting(SettingBoolean.class, "ui.glow.button").get());
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
			GlowButton btn = (GlowButton) e.getSource();
			
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
			optionsPanel.showOptions();
		}
	};
	
	private void addControlBar() {
		DefaultControlBar controlBar = new DefaultControlBar();
		SpeedSlider speedSlider = new SpeedSlider(Style.panelDarkBackground);
		speedSlider.getSlider().addChangeListener(sliderSpeedListener);
		speedSlider.getSlider().setValue((int) sm.getSettingObject("animations.speed").get());
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
	
	private Listener<EffectOptionsUpdateEvent> onAnimationOptionUpdate = new Listener<EffectOptionsUpdateEvent>() {
		@Override
		public void onEvent(EffectOptionsUpdateEvent event) {
			if(event.getType() == EffectType.Animation) {
				optionsPanel.showOptions();
			}
		}
	};
	
	private Listener<AnimationToggleEvent> onAnimationToggle = new Listener<AnimationToggleEvent>() {
		@Override
		public void onEvent(AnimationToggleEvent event) {
			// update panels and options
			addAnimationPanels();
			optionsPanel.showOptions();
		}
	};
	
	@Override
	public String getName() {
		return i18n.getString("Basic.Animations");
	}

}
