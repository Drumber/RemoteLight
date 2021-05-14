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

package de.lars.remotelightclient.ui.panels.scripts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.BigTextButton;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightclient.ui.components.dialogs.ErrorDialog;
import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.ui.panels.controlbars.comps.SpeedSlider;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel;
import de.lars.remotelightclient.utils.SettingsUtil;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.AnimationManager;
import de.lars.remotelightcore.event.Listener;
import de.lars.remotelightcore.event.events.types.EffectOptionsUpdateEvent;
import de.lars.remotelightcore.event.events.types.EffectToggleEvent.LuaScriptToggleEvent;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.lua.LuaManager;
import de.lars.remotelightcore.lua.LuaManager.LuaExceptionListener;
import de.lars.remotelightcore.lua.LuaScript;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.notification.listeners.NotificationOptionListener;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingObject;
import de.lars.remotelightcore.utils.DirectoryUtil;
import de.lars.remotelightcore.utils.maths.MathHelper;

public class ScriptsPanel extends MenuPanel {
	private static final long serialVersionUID = 1750844483582377827L;
	
	private MainFrame mainFrame;
	private SettingsManager sm;
	private LuaManager luaManager;
	private JPanel bgrScripts, bgrOptions, bgrSettings, bgrNotification;
	private JTextArea textNotification;
	
	public ScriptsPanel() {
		sm = Main.getInstance().getSettingsManager();
		luaManager = RemoteLightCore.getInstance().getLuaManager();
		luaManager.setLuaExceptionListener(exceptionListener);
		mainFrame = Main.getInstance().getMainFrame();
		
		sm.addSetting(new SettingObject("scripts.speed", null, 50));
		mainFrame.showControlBar(true);
		addControlBar();
		setBackground(Style.panelBackground);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		bgrNotification = new JPanel();
		bgrNotification.setBackground(Style.panelDarkBackground);
		bgrNotification.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		bgrNotification.setVisible(false);
		bgrNotification.setLayout(new BorderLayout());
		add(bgrNotification);
		
		JScrollPane scrollNotification = new JScrollPane();
		scrollNotification.getVerticalScrollBar().setUnitIncrement(16);
		scrollNotification.setViewportBorder(null);
		scrollNotification.setBorder(BorderFactory.createEmptyBorder());
		scrollNotification.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		bgrNotification.add(scrollNotification, BorderLayout.CENTER);
		
		textNotification = new JTextArea();
		textNotification.setRows(2);
		textNotification.setForeground(new Color(255, 60, 60));
		textNotification.setBackground(Style.panelDarkBackground);
		textNotification.setWrapStyleWord(true);
		textNotification.setLineWrap(true);
		textNotification.setEditable(false);
		scrollNotification.setViewportView(textNotification);
		
		JLabel lblCloseNotification = new JLabel(" X ");
		lblCloseNotification.setForeground(new Color(255, 60, 60));
		lblCloseNotification.setFont(Style.getFontBold(12));
		lblCloseNotification.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblCloseNotification.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				bgrNotification.setVisible(false);
				updateUI();
			}
		});
		bgrNotification.add(lblCloseNotification, BorderLayout.EAST);
		
		JPanel bgrScroll = new JPanel();
		add(bgrScroll);
		bgrScroll.setLayout(new BorderLayout(0, 0));
		
		TScrollPane scrollPane = new TScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		bgrScroll.add(scrollPane, BorderLayout.CENTER);
		
		bgrScripts = new JPanel();
		WrapLayout wlayout = new WrapLayout(FlowLayout.LEFT);
		bgrScripts.setLayout(wlayout);
		bgrScripts.setBackground(Style.panelBackground);
		scrollPane.setViewportView(bgrScripts);
		
		bgrSettings = new JPanel();
		bgrSettings.setLayout(new BoxLayout(bgrSettings, BoxLayout.Y_AXIS));
		bgrSettings.setBackground(Style.panelDarkBackground);
		
		TScrollPane scrollSettings = new TScrollPane(bgrSettings);
		scrollSettings.getVerticalScrollBar().setUnitIncrement(16);
		scrollSettings.setViewportBorder(null);
		scrollSettings.setBorder(BorderFactory.createEmptyBorder());
		scrollSettings.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		bgrScroll.add(scrollSettings, BorderLayout.SOUTH);
		
		bgrOptions = new JPanel();
		bgrOptions.setBackground(Style.panelDarkBackground);
		WrapLayout wlayout2 = new WrapLayout(FlowLayout.LEFT);
		bgrOptions.setLayout(wlayout2);
		add(bgrOptions);
		
		addOptionButtons();
		addScriptPanels();
		// register lua script listener
		Main.getInstance().getCore().getEventHandler().register(onLuaScriptEvent);
		Main.getInstance().getCore().getEventHandler().register(onLuaOptionsUpdate);
	}
	
	private void addOptionButtons() {
		bgrOptions.removeAll();
		
		JButton btnScriptDir = new JButton(i18n.getString("ScriptsPanel.openScriptsFolder"));
		UiUtils.configureButton(btnScriptDir);
		btnScriptDir.setName("scriptdir");
		btnScriptDir.addActionListener(optionButtonsListener);
		bgrOptions.add(btnScriptDir);
		
		JButton btnScan = new JButton(i18n.getString("ScriptsPanel.scan"));
		UiUtils.configureButton(btnScan);
		btnScan.setName("scan");
		btnScan.addActionListener(optionButtonsListener);
		bgrOptions.add(btnScan);
		
		bgrOptions.updateUI();
	}
	
	private ActionListener optionButtonsListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String name = ((JButton) e.getSource()).getName();
			
			/*
			 * Open lua scripts directory
			 */
			if(name.equals("scriptdir")) {
				try {
					Desktop.getDesktop().open(new File(DirectoryUtil.getLuaPath()));
				} catch (IOException ex) {
					Logger.error(ex, "Could not open scripts directory!");
				}
			}
			/*
			 * Scan for lua files
			 */
			if(name.equals("scan")) {
				luaManager.scanLuaScripts(DirectoryUtil.getLuaPath());
				addScriptPanels();
			}
		}
	};
	
	/**
	 * Adds the script panels of lua scripts in /.RemoteLight/lua_scripts/
	 */
	public void addScriptPanels() {
		bgrScripts.removeAll();
		for(File script : luaManager.getLuaScripts(DirectoryUtil.getLuaPath())) {
			
			BigTextButton btn = new BigTextButton(DirectoryUtil.getFileName(script), "");
			btn.setName(script.getAbsolutePath());
			btn.addMouseListener(scriptPanelsListener);
			
			if(luaManager.getActiveLuaScriptPath() != null && luaManager.getActiveLuaScriptPath().equals(script.getAbsolutePath())) {
				btn.setBorder(BorderFactory.createLineBorder(Style.accent));
			}
			bgrScripts.add(btn);
		}
		updateUI();
	}
	
	/**
	 * Lister for mouse click events on the script panels
	 */
	private MouseAdapter scriptPanelsListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			BigTextButton btn = (BigTextButton) e.getSource();
			if(luaManager.getActiveLuaScriptPath() != null && luaManager.getActiveLuaScriptPath().equals(btn.getName())) {
				luaManager.stopLuaScript();
			} else {
				for(File script : luaManager.getLuaScripts(DirectoryUtil.getLuaPath())) {
					if(script.getAbsolutePath().equals(btn.getName())) {
						luaManager.runLuaScript(script.getAbsolutePath());
						break;
					}
				}
			}
		}
	};
	
	private Listener<LuaScriptToggleEvent> onLuaScriptEvent = new Listener<LuaScriptToggleEvent>() {
		@Override
		public void onEvent(LuaScriptToggleEvent event) {
			addScriptPanels();
			addScriptSettings();
		}
	};
	
	private Listener<EffectOptionsUpdateEvent> onLuaOptionsUpdate = new Listener<EffectOptionsUpdateEvent>() {
		@Override
		public void onEvent(EffectOptionsUpdateEvent event) {
			if(event.getType() == EffectType.Lua) {
				addScriptSettings();
			}
		}
	};
	
	
	public void addScriptSettings() {
		bgrSettings.removeAll();
		LuaScript activeScript = luaManager.getActiveLuaScript();
		if(activeScript != null) {
			for(Setting s : activeScript.getScriptSettings()) {
				SettingPanel spanel = SettingsUtil.getSettingPanel(s);
				spanel.setBackground(bgrSettings.getBackground());
				spanel.setSettingChangedListener(p -> {
					p.setValue();
				});
				bgrSettings.add(spanel);
			}
		}
		updateUI();
	}
	
	
	/**
	 * Adds control bar with a speed slider
	 */
	private void addControlBar() {
		DefaultControlBar controlBar = new DefaultControlBar();
		SpeedSlider speedSlider = new SpeedSlider(Style.panelDarkBackground);
		speedSlider.getSlider().addChangeListener(sliderSpeedListener);
		speedSlider.getSlider().setValue((int) sm.getSettingObject("scripts.speed").get());
		controlBar.setActionPanel(speedSlider);
		mainFrame.setControlBarPanel(controlBar);
	}
	
	private ChangeListener sliderSpeedListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			int value = ((JSlider) e.getSource()).getValue();
			int speed = MathHelper.invertInRange(value, AnimationManager.MIN_SPEED, AnimationManager.MAX_SPEED);
			sm.getSettingObject("scripts.speed").setValue(value); // we save the actual slider value
			luaManager.setDelay(speed);
		}
	};
	
	
	private LuaExceptionListener exceptionListener = new LuaExceptionListener() {
		@Override
		public void onLuaException(Exception e) {
			String text = e.getMessage();
			if(text.contains("stack traceback:")) {
				text = e.getMessage().substring(0, e.getMessage().indexOf("stack traceback:"));
			}
			textNotification.setText(text);
			bgrNotification.setVisible(true);
			updateUI();
			// show notification
			showErrorNotification(e);
		}
	};
	
	private void showErrorNotification(Exception e) {
		Notification notification = new Notification(NotificationType.ERROR,
				"Lua Error", "An error occurred while executing the Lua script.",
				new String[] {"Details"}, Notification.LONG,
				new NotificationOptionListener() {
					@Override
					public void onOptionClicked(String option, int index) {
						// show details error dialog
						ErrorDialog.showErrorDialog(e, "Lua Error", false);
					}
				});
		// show notification
		Main.getInstance().showNotification(notification);
	}
	
	@Override
	public String getName() {
		return i18n.getString("Basic.Scripts");
	}
	
	@Override
	public void onEnd(MenuPanel newPanel) {
		Main.getInstance().getCore().getEventHandler().unregister(LuaScriptToggleEvent.class, onLuaScriptEvent);
		Main.getInstance().getCore().getEventHandler().unregister(EffectOptionsUpdateEvent.class, onLuaOptionsUpdate);
	}
	
}
