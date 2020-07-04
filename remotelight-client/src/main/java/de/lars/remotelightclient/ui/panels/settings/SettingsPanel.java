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
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.settings.settingComps.*;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationType;
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
	private final String[] GENERAL_SETTING_ORDER = {"ui.language", "ui.style", "ui.laf", "ui.windowdecorations", "%remains%"};
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
			
			// set default locale
			String langCode = ((SettingSelection) sm.getSettingFromId("ui.language")).getSelected();
			i18n.setLocale(langCode);
			
			// set Look And Feel
			Main.getInstance().setLookAndFeel();
			// set style
			Style.setStyle();
			FlatLaf.updateUILater();
			
			//repaint ui
			mainFrame.updateFrame();
			//display settings
			mainFrame.displayPanel(new SettingsPanel(mainFrame, Main.getInstance().getSettingsManager()));
			
			Main.getInstance().showNotification(
					new Notification(NotificationType.SUCCESS, i18n.getString("Basic.Settings"), i18n.getString("SettingsPanel.SavedSettings"), Notification.SHORT));
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
