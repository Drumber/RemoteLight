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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.musicsync.nativesound.NativeSoundConfigPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel.SettingChangedListener;
import de.lars.remotelightclient.utils.SettingsUtil;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.musicsync.InputUtil;
import de.lars.remotelightcore.musicsync.MusicSyncManager;
import de.lars.remotelightcore.musicsync.sound.Shared;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingObject;

public class MusicSyncOptionsPanel extends JPanel {
	private static final long serialVersionUID = -100112066309451958L;
	
	private List<SettingPanel> settingPanels;
	private SettingsManager sm;
	private MusicSyncManager msm;
	private JPanel bgrEffectOptions;
	private JPanel bgrEffectOptionsScroll;
	private JPanel panelSensitivity;
	private JLabel lblSensitivity;
	private JSlider sliderSensitivity;
	private JPanel panelAdjustment;
	private JLabel lblAdjustment;
	private JSlider sliderAdjustment;
	private JPanel panelInput;
	private JScrollPane scrollPaneOpt;
	private JPanel bgrOptions;
	private JLabel lblInput;
	private JRadioButton rbuttonNativeSound;

	/**
	 * Create the panel.
	 */
	public MusicSyncOptionsPanel() {
		sm = Main.getInstance().getSettingsManager();
		msm = RemoteLightCore.getInstance().getMusicSyncManager();
		settingPanels = new ArrayList<SettingPanel>();
		sm.addSetting(new SettingObject("musicsync.sensitivity", null, 20)); //$NON-NLS-1$
		sm.addSetting(new SettingObject("musicsync.adjustment", null, 200)); //$NON-NLS-1$
		
		Dimension size = new Dimension(Integer.MAX_VALUE, 150);
		setBackground(Style.panelDarkBackground);
		setAlignmentY(Component.TOP_ALIGNMENT);
		
		setLayout(new GridLayout(0, 2, 0, 0));
		JPanel bgrScrollOptions = new JPanel();
		bgrScrollOptions.setBorder(new EmptyBorder(4, 4, 0, 0));
		bgrScrollOptions.setLayout(new BoxLayout(bgrScrollOptions, BoxLayout.Y_AXIS));
		bgrScrollOptions.setAlignmentY(Component.TOP_ALIGNMENT);
		bgrScrollOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
		bgrScrollOptions.setBackground(Style.panelDarkBackground);
		add(bgrScrollOptions);
		
		scrollPaneOpt = new JScrollPane();
		scrollPaneOpt.setViewportBorder(null);
		scrollPaneOpt.setBorder(BorderFactory.createEmptyBorder());
		scrollPaneOpt.getVerticalScrollBar().setUnitIncrement(8);
		bgrScrollOptions.add(scrollPaneOpt);
		
		bgrOptions = new JPanel();
		bgrOptions.setLayout(new BoxLayout(bgrOptions, BoxLayout.Y_AXIS));
		bgrOptions.setBackground(Style.panelDarkBackground);
		scrollPaneOpt.setViewportView(bgrOptions);
		
		panelSensitivity = new JPanel();
		panelSensitivity.setLayout(new BoxLayout(panelSensitivity, BoxLayout.Y_AXIS));
		panelSensitivity.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelSensitivity.setAlignmentY(Component.TOP_ALIGNMENT);
		panelSensitivity.setBackground(Style.panelDarkBackground);
		bgrOptions.add(panelSensitivity);
		
		lblSensitivity = new JLabel(i18n.getString("MusicSync.Sensitivity")); //$NON-NLS-1$
		lblSensitivity.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblSensitivity.setForeground(Style.textColor);
		panelSensitivity.add(lblSensitivity);
		
		size = new Dimension(180, 20);
		
		sliderSensitivity = new JSlider();
		sliderSensitivity.setPreferredSize(size);
		sliderSensitivity.setMaximumSize(size);
		sliderSensitivity.setMinimum(10);
		sliderSensitivity.setMaximum(300);
		sliderSensitivity.setFocusable(false);
		sliderSensitivity.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderSensitivity.setBackground(Style.panelDarkBackground);
		sliderSensitivity.setName("sensitivity"); //$NON-NLS-1$
		sliderSensitivity.addChangeListener(sliderListener);
		UiUtils.addSliderMouseWheelListener(sliderSensitivity);
		sliderSensitivity.setValue((int) sm.getSettingObject("musicsync.sensitivity").getValue()); //$NON-NLS-1$
		panelSensitivity.add(sliderSensitivity);
		
		panelAdjustment = new JPanel();
		panelAdjustment.setLayout(new BoxLayout(panelAdjustment, BoxLayout.Y_AXIS));
		panelAdjustment.setAlignmentY(Component.TOP_ALIGNMENT);
		panelAdjustment.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelAdjustment.setBackground(Style.panelDarkBackground);
		bgrOptions.add(panelAdjustment);
		
		lblAdjustment = new JLabel(i18n.getString("MusicSync.Adjustment")); //$NON-NLS-1$
		lblAdjustment.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblAdjustment.setForeground(Style.textColor);
		panelAdjustment.add(lblAdjustment);
		
		sliderAdjustment = new JSlider();
		sliderAdjustment.setMinimum(50);
		sliderAdjustment.setMaximum(700);
		sliderAdjustment.setPreferredSize(size);
		sliderAdjustment.setMaximumSize(size);
		sliderAdjustment.setFocusable(false);
		sliderAdjustment.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderAdjustment.setBackground(Style.panelDarkBackground);
		sliderAdjustment.setName("adjustment"); //$NON-NLS-1$
		sliderAdjustment.addChangeListener(sliderListener);
		UiUtils.addSliderMouseWheelListener(sliderAdjustment);
		sliderAdjustment.setValue((int) sm.getSettingObject("musicsync.adjustment").getValue()); //$NON-NLS-1$
		panelAdjustment.add(sliderAdjustment);
		
		JPanel panelNativeSoundConfig = new JPanel();
		panelNativeSoundConfig.setAlignmentY(Component.TOP_ALIGNMENT);
		panelNativeSoundConfig.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelNativeSoundConfig.setBackground(Style.panelDarkBackground);
		panelNativeSoundConfig.setLayout(new BoxLayout(panelNativeSoundConfig, BoxLayout.X_AXIS));
		panelNativeSoundConfig.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
		bgrOptions.add(panelNativeSoundConfig);
		
		JButton btnTest = new JButton("Configure native sound input");
		UiUtils.configureButton(btnTest);
		btnTest.addActionListener(buttonNativeSoundConfigClicked);
		panelNativeSoundConfig.add(btnTest);
		
		panelInput = new JPanel();
		panelInput.setAlignmentY(Component.TOP_ALIGNMENT);
		panelInput.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelInput.setBackground(Style.panelDarkBackground);
		bgrOptions.add(panelInput);
		panelInput.setLayout(new BorderLayout(0, 0));
		
		bgrEffectOptionsScroll = new JPanel();
		bgrEffectOptionsScroll.setVisible(false);
		add(bgrEffectOptionsScroll);
		bgrEffectOptionsScroll.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		bgrEffectOptionsScroll.add(scrollPane);
		
		bgrEffectOptions = new JPanel();
		bgrEffectOptions.setBorder(new EmptyBorder(4, 4, 0, 0));
		bgrEffectOptions.setBackground(Style.panelDarkBackground);
		scrollPane.setViewportView(bgrEffectOptions);
		bgrEffectOptions.setLayout(new BoxLayout(bgrEffectOptions, BoxLayout.Y_AXIS));
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initInputPanel();
			}
		});
	}
	
	
	private ChangeListener sliderListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider slider = (JSlider) e.getSource();
			
			if(slider.getName().equals("sensitivity")) { //$NON-NLS-1$
				sm.getSettingObject("musicsync.sensitivity").setValue(slider.getValue()); //$NON-NLS-1$
				msm.setSensitivity(slider.getValue() / 100.0);
				
			} else if(slider.getName().equals("adjustment")) { //$NON-NLS-1$
				sm.getSettingObject("musicsync.adjustment").setValue(slider.getValue()); //$NON-NLS-1$
				msm.setAdjustment(slider.getValue() / 100.0);
			}
		}
	};
	
	
	public void addMusicEffectOptions(List<Setting> settings) {
		bgrEffectOptionsScroll.setVisible(true);
		bgrEffectOptions.removeAll();
		
		JLabel lblTitle = new JLabel(i18n.getString("MusicSync.EffectOptions"), SwingConstants.LEFT); //$NON-NLS-1$
		lblTitle.setFont(Style.getFontBold(11));
		lblTitle.setForeground(Style.accent);
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		bgrEffectOptions.add(lblTitle);
		
		for(Setting s : settings) {
			SettingPanel spanel = SettingsUtil.getSettingPanel(s);
			spanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			spanel.setSettingChangedListener(effectOptionsChangeListener);
			spanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 27));
			spanel.setBackground(Style.panelDarkBackground);
			bgrEffectOptions.add(spanel);
			settingPanels.add(spanel);
		}
		updateUI();
	}
	
	public void removeMusicEffectOptions() {
		bgrEffectOptions.removeAll();
		bgrEffectOptionsScroll.setVisible(false);
		updateUI();
	}
	
	private SettingChangedListener effectOptionsChangeListener = new SettingChangedListener() {
		@Override
		public void onSettingChanged(SettingPanel settingPanel) {
			for(SettingPanel sp : settingPanels) {
				sp.setValue();
			}
		}
	};
	
	private void initInputPanel() {
		String input = (String) sm.getSettingObject("musicsync.input").getValue(); //$NON-NLS-1$
		
		JPanel buttonPanel = new JPanel(new GridLayout(0,1));
		buttonPanel.setBackground(Style.panelDarkBackground);
		ButtonGroup group = new ButtonGroup();
		
		// add native sound radio button
		rbuttonNativeSound = new JRadioButton();
		rbuttonNativeSound.setBackground(Style.panelDarkBackground);
		rbuttonNativeSound.setForeground(Style.textColor);
		buttonPanel.add(rbuttonNativeSound);
		group.add(rbuttonNativeSound);
		rbuttonNativeSound.setActionCommand("_NativeSound_");
		rbuttonNativeSound.addActionListener(inputSelectedListener);
		if(!msm.isNativeSoundConfigured()) {
			rbuttonNativeSound.setEnabled(false);
			rbuttonNativeSound.setText("Use native sound library (not configured)");
		} else {
			rbuttonNativeSound.setText("Use native sound library " + msm.getNativeSoundDevice().getName());
			if(input.equals("_NativeSound_")) {
				rbuttonNativeSound.setSelected(true);
			}
		}
		
		for(Mixer.Info info : Shared.getMixerInfo(false, true)) {
			Mixer mixer = AudioSystem.getMixer(info);

			if(InputUtil.isLineSupported(mixer)) {
				JRadioButton button = new JRadioButton();
				button.setBackground(Style.panelDarkBackground);
				button.setForeground(Style.textColor);
				button.setText(Shared.toLocalString(info));
				buttonPanel.add(button);
				group.add(button);
				button.setActionCommand(info.toString());
				button.addActionListener(inputSelectedListener);
				//set last time input as selected
				if(input != null) {
					if(input.equals(info.toString())) {
						button.setSelected(true);
					}
				}
			}
		}
		
		lblInput = new JLabel(i18n.getString("MusicSync.SelectInput")); //$NON-NLS-1$
		lblInput.setForeground(Style.textColor);
		panelInput.add(lblInput, BorderLayout.NORTH);
		panelInput.add(buttonPanel);
	}
	
	private ActionListener inputSelectedListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("_NativeSound_")) {
				sm.getSettingObject("musicsync.input").setValue("_NativeSound_");
				if(msm.getNativeSoundDevice() == null || !msm.getNativeSoundDevice().isValid()) {
					Main.getInstance().showNotification(NotificationType.ERROR, "Native sound device is not valid!");
					rbuttonNativeSound.setEnabled(false);
					rbuttonNativeSound.setText("Use native sound library (not configured)");
					return;
				} else {
					msm.setNativeSoundEnabled(true);
					RemoteLightCore.getInstance().getMusicSyncManager().newSoundProcessor();
				}
			} else {
				msm.setNativeSoundEnabled(false);
				for(Mixer.Info info : Shared.getMixerInfo(false, true)) {
					if(e.getActionCommand().equals(info.toString())){
						Mixer newMixer = AudioSystem.getMixer(info);
						msm.getSoundProcessor().setMixer(newMixer);
						//save last selected to data file
						sm.getSettingObject("musicsync.input").setValue(info.toString()); //$NON-NLS-1$
						//refresh SoundProcessor
						RemoteLightCore.getInstance().getMusicSyncManager().newSoundProcessor();
						break;
					}
				}
			}
		}
	};
	
	private ActionListener buttonNativeSoundConfigClicked = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(msm.isActive() && msm.getSoundProcessor().isNativeSoundEnabled()) {
				Main.getInstance().showNotification(NotificationType.ERROR, "Please stop the active effect before configuring.");
				return;
			}
			NativeSoundConfigPanel.showDialog();
			msm.updateNativeSoundDevice();
			if(msm.getNativeSoundDevice().isValid()) {
				rbuttonNativeSound.setEnabled(true);
				rbuttonNativeSound.setText("Use native sound library " + msm.getNativeSoundDevice().getName());
			}
		}
	};
	
}
