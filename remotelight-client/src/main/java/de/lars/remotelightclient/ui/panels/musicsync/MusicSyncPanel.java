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

package de.lars.remotelightclient.ui.panels.musicsync;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.*;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.comps.BigTextButton;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.musicsync.MusicSyncManager;
import de.lars.remotelightcore.settings.Setting;

public class MusicSyncPanel extends MenuPanel {
	private static final long serialVersionUID = -5524656198416878733L;
	
	private MainFrame mainFrame;
	private MusicSyncManager msm;
	private MusicSyncOptionsPanel muiscEffectOptions;
	private JPanel bgrMusicEffects;
	
	public MusicSyncPanel() {
		mainFrame = Main.getInstance().getMainFrame();
		msm = RemoteLightCore.getInstance().getMusicSyncManager();
		mainFrame.showControlBar(true);
		mainFrame.setControlBarPanel(new DefaultControlBar());
		
		setBackground(Style.panelBackground);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel bgrScroll = new JPanel();
		bgrScroll.setPreferredSize(new Dimension(Integer.MAX_VALUE, 300));
		add(bgrScroll);
		bgrScroll.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		bgrScroll.add(scrollPane);
		
		bgrMusicEffects = new JPanel();
		WrapLayout wlayout = new WrapLayout(FlowLayout.LEFT);
		bgrMusicEffects.setLayout(wlayout);
		bgrMusicEffects.setBackground(Style.panelBackground);
		scrollPane.setViewportView(bgrMusicEffects);
		
		JPanel bgrOptions = new JPanel();
		bgrOptions.setBackground(Style.panelDarkBackground);
		bgrOptions.setLayout(new BoxLayout(bgrOptions, BoxLayout.X_AXIS));
		muiscEffectOptions = new MusicSyncOptionsPanel();
		bgrOptions.add(muiscEffectOptions);
		add(bgrOptions);
		
		addMusicEffectPanels();
	}
	
	public void addMusicEffectPanels() {
		bgrMusicEffects.removeAll();
		for(MusicEffect m : msm.getMusicEffects()) {
			BigTextButton btn = new BigTextButton(m.getDisplayname(), "");
			btn.setName(m.getName());
			btn.addMouseListener(btnMusicEffectListener);
			
			if(msm.getActiveEffect() != null && msm.getActiveEffect().getName().equals(m.getName())) {
				btn.setBorder(BorderFactory.createLineBorder(Style.accent));
				showMusicEffectOptions();
			}
			bgrMusicEffects.add(btn);
		}
		updateUI();
	}
	
	private MouseAdapter btnMusicEffectListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			BigTextButton btn = (BigTextButton) e.getSource();
			
			if(msm.getActiveEffect() != null && msm.getActiveEffect().getName().equals(btn.getName())) {
				msm.stop();
				muiscEffectOptions.removeMusicEffectOptions();
			} else {
				for(MusicEffect m : msm.getMusicEffects()) {
					if(m.getName().equals(btn.getName())) {
						msm.start(m);
						showMusicEffectOptions();
						break;
					}
				}
			}
			addMusicEffectPanels();
		}
	};
	
	private void showMusicEffectOptions() {
		if(msm.getCurrentMusicEffectOptions() != null && msm.getCurrentMusicEffectOptions().size() > 0) {
			List<Setting> options = msm.getCurrentMusicEffectOptions();
			muiscEffectOptions.addMusicEffectOptions(options);
		} else {
			muiscEffectOptions.removeMusicEffectOptions();
		}
	}

}
