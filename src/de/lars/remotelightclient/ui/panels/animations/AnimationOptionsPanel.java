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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsUtil;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel.SettingChangedListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.Cursor;

public class AnimationOptionsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9051769253598831077L;
	private JPanel parentPanel;
	private JPanel panelMain, panelBackground, panelTitel, bgrScroll;
	private JScrollPane scrollPane;
	private List<SettingPanel> settingPanels;
	private boolean hidden = true;
	private Dimension expandedSize = new Dimension(Integer.MAX_VALUE, 120);
	private Dimension hiddenSize = new Dimension(Integer.MAX_VALUE, 10);

	/**
	 * Create the panel.
	 */
	public AnimationOptionsPanel(JPanel parentPanel) {
		settingPanels = new ArrayList<SettingPanel>();
		this.parentPanel = parentPanel;
		SettingsManager sm = Main.getInstance().getSettingsManager();
		sm.addSetting(new SettingObject("animationspanel.optionshidden", null, true));
		
		setLayout(new BorderLayout(0, 0));
		
		panelMain = new JPanel();
		panelMain.setBackground(Style.panelDarkBackground);
		add(panelMain, BorderLayout.CENTER);
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
		
		panelTitel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelTitel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelTitel.setBackground(Style.panelAccentBackground);
		panelTitel.setPreferredSize(new Dimension(200, 20));
		panelTitel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panelMain.add(panelTitel);
		
		panelBackground = new JPanel();
		panelBackground.setBackground(Style.panelDarkBackground);
		panelBackground.setLayout(new BoxLayout(panelBackground, BoxLayout.Y_AXIS));
		
		bgrScroll = new JPanel();
		bgrScroll.setBackground(Color.RED);
		bgrScroll.setLayout(new BorderLayout(0, 0));
		bgrScroll.add(panelBackground);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		panelMain.add(scrollPane);
		scrollPane.setViewportView(bgrScroll);

		if((boolean) sm.getSettingObject("animationspanel.optionshidden").getValue()) {
			hide();
		} else {
			expand();
		}
	}
	
	public void hide() {
		hidden = true;
		scrollPane.setVisible(false);
		panelTitel.removeAll();
		
		JLabel lblExpand = new JLabel("Animation options");
		lblExpand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblExpand.setFont(Style.getFontBold(10));
		lblExpand.setForeground(Style.accent);
		lblExpand.addMouseListener(expandListener);
		panelTitel.add(lblExpand);
		
		parentPanel.setMaximumSize(hiddenSize);
		
		updateUI();
	}
	
	public void expand() {
		hidden = false;
		scrollPane.setVisible(true);
		panelBackground.removeAll();
		panelTitel.removeAll();
		
		JLabel lblHide = new JLabel("Hide");
		lblHide.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblHide.setFont(Style.getFontBold(9));
		lblHide.setForeground(Style.textColorDarker);
		lblHide.addMouseListener(expandListener);
		panelTitel.add(lblHide);
		
		parentPanel.setMaximumSize(expandedSize);
		
		showOptions();
	}
	
	public void showOptions() {
		panelBackground.removeAll();
		JLabel lblTitel = new JLabel("Options");
		lblTitel.setFont(Style.getFontBold(11));
		lblTitel.setForeground(Style.textColor);
		panelBackground.add(lblTitel);
		
		List<Setting> settings = Main.getInstance().getAnimationManager().getCurrentAnimationOptions();
		if(settings == null || settings.size() <= 0) {
			lblTitel.setText("The current animation has no options.");
		} else {
			for(Setting s : settings) {
				SettingPanel spanel = SettingsUtil.getSettingPanel(s);
				spanel.setAlignmentX(Component.LEFT_ALIGNMENT);
				spanel.setSettingChangedListener(optionsChangeListener);
				spanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
				spanel.setBackground(Style.panelDarkBackground);
				panelBackground.add(spanel);
				settingPanels.add(spanel);
			}
		}
		updateUI();
	}
	
	private MouseAdapter expandListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			Main.getInstance().getSettingsManager().getSettingObject("animationspanel.optionshidden").setValue(!hidden);
			if(hidden) {
				expand();
			} else {
				hide();
			}
		};
	};
	
	private SettingChangedListener optionsChangeListener = new SettingChangedListener() {
		@Override
		public void onSettingChanged(SettingPanel settingPanel) {
			for(SettingPanel sp : settingPanels) {
				sp.setValue();
			}
		}
	};

}
