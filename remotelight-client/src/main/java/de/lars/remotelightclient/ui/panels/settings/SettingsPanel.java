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
package de.lars.remotelightclient.ui.panels.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatLaf;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.lang.i18n;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.MainFrame.NotificationType;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.settings.settingComps.*;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.*;

public class SettingsPanel extends MenuPanel {
	private static final long serialVersionUID = 3954953325346082615L;
	
	private SettingsManager sm;
	private MainFrame mainFrame;
	private List<SettingPanel> settingPanels;
	
	/** some settings that should be displayed in the right order */
	private final String[] GENERAL_SETTING_ORDER = {"ui.language", "ui.style", "ui.laf", "%remains%"};
	private final String[] OTHERS_SETTING_ORDER = {"%remains%"};

	/**
	 * Create the panel.
	 */
	public SettingsPanel(MainFrame mainFrame , SettingsManager sm) {
		this.mainFrame = mainFrame;
		this.sm = sm;
		mainFrame.showControlBar(false);
		settingPanels = new ArrayList<SettingPanel>();
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout(0, 0));
		
		JPanel main = new JPanel();
		main.setBackground(Style.panelBackground);
		main.setAlignmentX(Component.LEFT_ALIGNMENT);
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.add(getSettingsBgr(SettingCategory.General, i18n.getString("SettingsPanel.General"), GENERAL_SETTING_ORDER)); //$NON-NLS-1$
		main.add(getSettingsBgr(SettingCategory.Others, i18n.getString("SettingsPanel.Others"), OTHERS_SETTING_ORDER)); //$NON-NLS-1$
		
		JButton btnSave = new JButton(i18n.getString("SettingsPanel.Save")); //$NON-NLS-1$
		UiUtils.configureButton(btnSave);
        btnSave.setBorderPainted(false);
        btnSave.setFocusPainted(false);
        btnSave.setBackground(Style.buttonBackground);
        btnSave.setForeground(Style.textColor);
        btnSave.addActionListener(btnSaveListener);
        btnSave.setPreferredSize(new Dimension(getWidth(), 30));
		add(btnSave, BorderLayout.SOUTH);
		
		JScrollPane scrollPane = new JScrollPane(main);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);
		
		this.updateUI();
	}
	
	private JPanel getSettingsBgr(SettingCategory category, String title, String[] order) {
		JPanel bgr = new JPanel();
		bgr.setBorder(new EmptyBorder(10, 10, 10, 0));
		bgr.setBackground(Style.panelBackground);
		bgr.setAlignmentX(Component.LEFT_ALIGNMENT);
		bgr.setLayout(new BoxLayout(bgr, BoxLayout.Y_AXIS));
		
		JLabel lblTitle = new JLabel(title, SwingConstants.LEFT);
		lblTitle.setFont(Style.getFontBold(12));
		lblTitle.setForeground(Style.accent);
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		bgr.add(lblTitle);
		
		List<String> listOrder = Arrays.asList(order);
		
		for(String id : listOrder) {
			if(id.equals("%remains%")) {
				// add all settings of category except those in the list
				for(Setting s : sm.getSettingsFromCategory(category)) {
					if(listOrder.contains(s.getId()))
						continue;
					SettingPanel spanel = this.getSettingPanel(s);
					spanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					bgr.add(spanel);
					settingPanels.add(spanel);
				}
				continue;
			}
			// add setting panel for ordered ID
			Setting s = sm.getSettingFromId(id);
			if(s == null) continue;
			SettingPanel spanel = this.getSettingPanel(s);
			spanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			bgr.add(spanel);
			settingPanels.add(spanel);
		}
		return bgr;
	}
	
	private SettingPanel getSettingPanel(Setting s) {
		if(s instanceof SettingString) {
			return new SettingStringPanel((SettingString) s);
		}
		if(s instanceof SettingBoolean) {
			return new SettingBooleanPanel((SettingBoolean) s);
		}
		if(s instanceof SettingColor) {
			return new SettingColorPanel((SettingColor) s);
		}
		if(s instanceof SettingDouble) {
			return new SettingDoublePanel((SettingDouble) s);
		}
		if(s instanceof SettingInt) {
			return new SettingIntPanel((SettingInt) s);
		}
		if(s instanceof SettingSelection) {
			return new SettingSelectionPanel((SettingSelection) s);
		}
		return null;
	}
	
	private ActionListener btnSaveListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			//save values
			for(SettingPanel sp : settingPanels) {
				sp.setValue();
			}
			// set Look And Feel
			Main.getInstance().setLookAndFeel();
			// set style
			Style.setStyle();
			FlatLaf.updateUILater();
			//repaint ui
			mainFrame.updateFrame();
			//display settings
			mainFrame.displayPanel(new SettingsPanel(mainFrame, Main.getInstance().getSettingsManager()));
			mainFrame.printNotification(i18n.getString("SettingsPanel.SavedSettings"), NotificationType.Info); //$NON-NLS-1$
		}
	};
	
	@Override
	public void onEnd(MenuPanel newPanel) {
		//save values
		for(SettingPanel sp : settingPanels) {
			sp.setValue();
		}
		super.onEnd(newPanel);
	}

}
