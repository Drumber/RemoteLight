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

package de.lars.remotelightclient;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import org.tinylog.Logger;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightclient.ui.components.dialogs.UpdateDialog;
import de.lars.remotelightclient.ui.font.FontManager;
import de.lars.remotelightclient.utils.ui.FlatLafThemesUtil;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.cmd.StartParameterHandler;
import de.lars.remotelightcore.lang.LangUtil;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.notification.listeners.NotificationOptionListener;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingObject;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
import de.lars.remotelightcore.settings.types.SettingString;
import de.lars.remotelightcore.utils.DirectoryUtil;
import de.lars.remotelightcore.utils.UpdateChecker;

public class StartUp {
	
	private SettingsManager s = Main.getInstance().getSettingsManager();

	public StartUp(StartParameterHandler startParameter) {
		// install FlatLaf Look and Feels
		FlatLafThemesUtil.installFlatLafThemes();
		
		//register default settings
		registerSettings();
		
		// set default language
		String langCode = ((SettingSelection) s.getSettingFromId("ui.language")).getSelected();
		i18n.setLocale(langCode);
		
		// init menu icons and custom font
		Style.initFonts();
		
		copyLuaExamples();
		
		// check for updates
		if(((SettingBoolean) s.getSettingFromId("main.checkupdates")).get() || startParameter.updateChecker) {
			checkForUpdates(false);
		}
		
		// add Touch-Scroll setting listeners
		s.getSetting(SettingBoolean.class, "ui.touchscroll").setValueListener(l -> {
			TScrollPane.setDefaultDragScrolling((boolean) l.get());
		});
		s.getSetting(SettingBoolean.class, "ui.touchscroll.invert").setValueListener(l -> {
			TScrollPane.setDefaultScrollingInverted((boolean) l.get());
		});
		
		// start REST API web server
		SettingBoolean sRestApi = s.getSetting(SettingBoolean.class, "restapi.enable");
		if(sRestApi.get()) {			
			Main.getInstance().startRestApi();
		}
		// add REST API setting listener 
		sRestApi.setValueListener(l -> {
			if(sRestApi.get() && !Main.getInstance().isRestApiRunning())
				Main.getInstance().startRestApi();
			else if(!sRestApi.get() && Main.getInstance().isRestApiRunning())
				Main.getInstance().stopRestApi();
		});
	}
	
	/**
	 * Check for updates in separate thread.
	 * @param notAvailableMsg	whether to show notification if no update available
	 */
	public static void checkForUpdates(boolean notAvailableMsg) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// show update dialog
				UpdateChecker updateChecker = new UpdateChecker(Main.VERSION);
				if(updateChecker.isNewVersionAvailable() && !RemoteLightCore.isHeadless() &&
				  (!updateChecker.isPreRelease() || Main.getInstance().getSettingsManager().getSetting(SettingBoolean.class, "main.checkupdates.prerelease").get()))
				{
					// show update notification
					String[] options = {"Details", "Download"};
					Notification notification = new Notification(NotificationType.IMPORTANT,
							"Update available", "RemoteLight " + updateChecker.getNewTag() + " is available.",
							options, Notification.LONG, new NotificationOptionListener() {
								@Override
								public void onOptionClicked(String option, int index) {
									if(index == 0) {
										// show detail dialog
										UpdateDialog.showUpdateDialog(updateChecker);
									} else if(index == 1) {
										// open download site
										UpdateDialog.openDownloadSite(updateChecker.getNewUrl());
									}
										
								}
							});
					notification.setHideOnOptionClick(false);
					Main.getInstance().showNotification(notification);
					Logger.info(String.format("There is an update available! Current version: %s New version: %s Download here: %s", 
							Main.VERSION, updateChecker.getNewTag(), updateChecker.getNewUrl()));
				} else if(notAvailableMsg) {
					Main.getInstance().showNotification(NotificationType.INFO, "No Update Available", "There is no new version available.");
				}
			}
		}, "UpdateChecker Thread").start();
	}
	
	public void copyLuaExamples() {
		File luaDir = new File(DirectoryUtil.getLuaPath());
		// copy Lua example files
		try {
			DirectoryUtil.copyFolderFromJar(DirectoryUtil.RESOURCES_CLASSPATH + "lua_examples", luaDir, false);
		} catch (IOException e) {
			Logger.error("Could not copy lua example files: " + e.getMessage());
		}
	}
	
	public void registerSettings() {
		// UI Theme
		s.addSetting(new SettingString("ui.theme", "Theme Class Name", SettingCategory.Intern, null, FlatMaterialDarkerIJTheme.class.getName()));
		
		//General
		s.addSetting(new SettingSelection("ui.language", "Language", SettingCategory.General, "UI Language", LangUtil.langCodeToName(LangUtil.LANGUAGES), "English", Model.ComboBox));
		s.addSetting(new SettingBoolean("ui.windowdecorations", "Custom window decorations", SettingCategory.General, "Only available on Windows 10 and in combination with FlatLaf Look and Feels", false));
		s.addSetting(new SettingBoolean("ui.windowdecorations.unified", "Unified Window Decorations", SettingCategory.General, null, true));
		s.addSetting(new SettingSelection("ui.font", "UI Font", SettingCategory.General, "Set a custom UI font", FontManager.getAllFontNames().toArray(new String[0]), "Muli", Model.ComboBox));
		s.addSetting(new SettingInt("ui.fontsize", "UI Font size", SettingCategory.General, "Default UI font size", 11, 9, 20, 1));
		s.addSetting(new SettingBoolean("ui.glow.button", "Glowing Effect Buttons", SettingCategory.General, "Enable glow effect for buttons.", true));
		s.addSetting(new SettingBoolean("ui.sidemenu.unified", "Unified Side Menu Background", SettingCategory.General, null, false));
		
		//Others
		s.addSetting(new SettingBoolean("ui.hideintray", "Hide in tray", SettingCategory.Others, "Hide in system tray when closing.", false));
		s.addSetting(new SettingBoolean("main.checkupdates", "Check for updates", SettingCategory.Others, "Shows a notification when a new version is available.", true));
		s.addSetting(new SettingBoolean("main.checkupdates.prerelease", "Notification for pre-releases", SettingCategory.Others, "If activated, a notification is also shown for pre-releases.", true));
		s.addSetting(new SettingBoolean("plugins.enable", "Enable plugins", SettingCategory.Others, "Option to enable or disable the plugin system. Requires a restart to take effect.", true));
		s.addSetting(new SettingBoolean("restapi.enable", "Enable REST API", SettingCategory.Others, "Enable the web sever for the RemoteLight REST API", true));
		s.addSetting(new SettingInt("restapi.port", "REST API Port", SettingCategory.Others, "Web server port for the REST API.", 8080, 0, Integer.MAX_VALUE, 1));
		s.addSetting(new SettingBoolean("ui.touchscroll", "Touch Scroll support", SettingCategory.Others, "Enable scroll-on-drag feature.", false));
		s.addSetting(new SettingBoolean("ui.touchscroll.invert", "Invert Touch Scroll", SettingCategory.Others, "Invert the scroll direction.", false));
		
		//Intern
		s.addSetting(new SettingObject("mainFrame.size", "Window size", new Dimension(850, 450)));
		s.addSetting(new SettingObject("simulatorFrame.size", "Simulator Window size", new Dimension(650, 150)));
	}

}
