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
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel.SettingChangedListener;
import de.lars.remotelightclient.utils.SettingsUtil;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingObject;

public class AnimationOptionsPanel extends JPanel {
	private static final long serialVersionUID = -9051769253598831077L;
	
	private JPanel parentPanel;
	private JPanel panelMain, panelBackground, panelTitle, bgrScroll;
	private TScrollPane scrollPane;
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
		UiUtils.bindBackground(panelMain, Style.panelDarkBackground());
		add(panelMain, BorderLayout.CENTER);
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
		
		panelTitle = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelTitle.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		UiUtils.bindBackground(panelTitle, Style.panelAccentBackground());
		panelTitle.setPreferredSize(new Dimension(200, 25));
		panelTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
		panelMain.add(panelTitle);
		
		panelBackground = new JPanel();
		UiUtils.bindBackground(panelBackground, Style.panelDarkBackground());
		panelBackground.setLayout(new BoxLayout(panelBackground, BoxLayout.Y_AXIS));
		
		bgrScroll = new JPanel();
		bgrScroll.setBackground(Color.RED);
		bgrScroll.setLayout(new BorderLayout(0, 0));
		bgrScroll.add(panelBackground);
		
		scrollPane = new TScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		panelMain.add(scrollPane);
		scrollPane.setViewportView(bgrScroll);

		if((boolean) sm.getSettingObject("animationspanel.optionshidden").get()) { //$NON-NLS-1$
			hide();
		} else {
			expand();
		}
	}
	
	public void hide() {
		hidden = true;
		scrollPane.setVisible(false);
		panelTitle.removeAll();
		
		JLabel lblExpand = new JLabel(i18n.getString("AnimationOptionsPanel.AnimationOptions")); //$NON-NLS-1$
		lblExpand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblExpand.setFont(Style.getFontBold(11));
		UiUtils.bindForeground(lblExpand, Style.accent());
		lblExpand.addMouseListener(expandListener);
		lblExpand.setIcon(Style.getFontIcon(MenuIcon.SETTINGS, 11, Style.textColorDarker().get()));
		panelTitle.add(lblExpand);
		
		parentPanel.setMaximumSize(hiddenSize);
		
		updateUI();
	}
	
	public void expand() {
		hidden = false;
		scrollPane.setVisible(true);
		panelBackground.removeAll();
		panelTitle.removeAll();
		
		JLabel lblHide = new JLabel(i18n.getString("AnimationOptionsPanel.Hide")); //$NON-NLS-1$
		lblHide.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblHide.setFont(Style.getFontBold(10));
		UiUtils.bindForeground(lblHide, Style.textColorDarker());
		lblHide.addMouseListener(expandListener);
		panelTitle.add(lblHide);
		
		parentPanel.setMaximumSize(expandedSize);
		
		showOptions();
	}
	
	public synchronized void showOptions() {
		panelBackground.removeAll();
		JLabel lblTitel = new JLabel(i18n.getString("AnimationOptionsPanel.Options")); //$NON-NLS-1$
		lblTitel.setFont(Style.getFontBold(11));
		panelBackground.add(lblTitel);
		
		List<Setting> settings = RemoteLightCore.getInstance().getAnimationManager().getCurrentAnimationOptions()
				.stream()
				.filter(s -> !s.hasFlag(Setting.HIDDEN))
				.collect(Collectors.toList());
		if(settings == null || settings.size() <= 0) {
			lblTitel.setText(i18n.getString("AnimationOptionsPanel.CurrentAnimationNoOption")); //$NON-NLS-1$
		} else {
			for(Setting s : settings) {
				SettingPanel spanel = SettingsUtil.getSettingPanel(s);
				spanel.setAlignmentX(Component.LEFT_ALIGNMENT);
				spanel.setSettingChangedListener(optionsChangeListener);
				spanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 27));
				UiUtils.bindBackground(spanel, Style.panelDarkBackground());
				panelBackground.add(spanel);
				settingPanels.add(spanel);
			}
		}
		
		panelBackground.revalidate();
		panelBackground.repaint();
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
			settingPanel.setValue();
			Main.getInstance().getCore().getAnimationManager().onEffectOptionChanged();
		}
	};

}
