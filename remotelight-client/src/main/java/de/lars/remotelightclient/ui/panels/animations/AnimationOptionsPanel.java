/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Drumber
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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.lang.i18n;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel.SettingChangedListener;
import de.lars.remotelightclient.utils.SettingsUtil;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingObject;

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
		sm.addSetting(new SettingObject("animationspanel.optionshidden", null, true)); //$NON-NLS-1$
		
		setLayout(new BorderLayout(0, 0));
		
		panelMain = new JPanel();
		panelMain.setBackground(Style.panelDarkBackground);
		add(panelMain, BorderLayout.CENTER);
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
		
		panelTitel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelTitel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelTitel.setBackground(Style.panelAccentBackground);
		panelTitel.setPreferredSize(new Dimension(200, 25));
		panelTitel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
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

		if((boolean) sm.getSettingObject("animationspanel.optionshidden").getValue()) { //$NON-NLS-1$
			hide();
		} else {
			expand();
		}
	}
	
	public void hide() {
		hidden = true;
		scrollPane.setVisible(false);
		panelTitel.removeAll();
		
		JLabel lblExpand = new JLabel(i18n.getString("AnimationOptionsPanel.AnimationOptions")); //$NON-NLS-1$
		lblExpand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblExpand.setFont(Style.getFontBold(11));
		lblExpand.setForeground(Style.accent);
		lblExpand.addMouseListener(expandListener);
		lblExpand.setIcon(Style.getFontIcon(MenuIcon.SETTINGS, 11, Style.textColorDarker));
		panelTitel.add(lblExpand);
		
		parentPanel.setMaximumSize(hiddenSize);
		
		updateUI();
	}
	
	public void expand() {
		hidden = false;
		scrollPane.setVisible(true);
		panelBackground.removeAll();
		panelTitel.removeAll();
		
		JLabel lblHide = new JLabel(i18n.getString("AnimationOptionsPanel.Hide")); //$NON-NLS-1$
		lblHide.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblHide.setFont(Style.getFontBold(10));
		lblHide.setForeground(Style.textColorDarker);
		lblHide.addMouseListener(expandListener);
		panelTitel.add(lblHide);
		
		parentPanel.setMaximumSize(expandedSize);
		
		showOptions();
	}
	
	public void showOptions() {
		panelBackground.removeAll();
		JLabel lblTitel = new JLabel(i18n.getString("AnimationOptionsPanel.Options")); //$NON-NLS-1$
		lblTitel.setFont(Style.getFontBold(11));
		lblTitel.setForeground(Style.textColor);
		panelBackground.add(lblTitel);
		
		List<Setting> settings = RemoteLightCore.getInstance().getAnimationManager().getCurrentAnimationOptions();
		if(settings == null || settings.size() <= 0) {
			lblTitel.setText(i18n.getString("AnimationOptionsPanel.CurrentAnimationNoOption")); //$NON-NLS-1$
		} else {
			for(Setting s : settings) {
				SettingPanel spanel = SettingsUtil.getSettingPanel(s);
				spanel.setAlignmentX(Component.LEFT_ALIGNMENT);
				spanel.setSettingChangedListener(optionsChangeListener);
				spanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 27));
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
			Main.getInstance().getSettingsManager().getSettingObject("animationspanel.optionshidden").setValue(!hidden); //$NON-NLS-1$
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
