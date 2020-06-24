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

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.comps.dialogs.UpdateDialog;
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
import de.lars.remotelightcore.settings.types.SettingObject;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
import de.lars.remotelightcore.utils.UpdateChecker;

public class StartUp {
	
	private SettingsManager s = Main.getInstance().getSettingsManager();

	public StartUp(StartParameterHandler startParameter) {
		//register default settings
		registerSettings();
		
		// set default language
		String langCode = ((SettingSelection) s.getSettingFromId("ui.language")).getSelected();
		i18n.setLocale(langCode);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				//check for update (this block blocks the thread)
				if(((SettingBoolean) s.getSettingFromId("main.checkupdates")).getValue() || startParameter.updateChecker) {
					// show update dialog
					UpdateChecker updateChecker = new UpdateChecker(Main.VERSION);
					if(updateChecker.isNewVersionAvailable() && !RemoteLightCore.isHeadless()) {
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
					}
				}
				
			}
		}, "UpdateChecker Thread").start();
	}
	
	public void registerSettings() {
		//General
		s.addSetting(new SettingSelection("ui.language", "Language", SettingCategory.General, "UI Language", LangUtil.langCodeToName(LangUtil.LANGUAGES), "English", Model.ComboBox));
		s.addSetting(new SettingSelection("ui.style", "Style", SettingCategory.General, "Colors of the UI", Style.STYLES, "Dark", Model.ComboBox));
		s.addSetting(new SettingSelection("ui.laf", "LookAndFeel", SettingCategory.General, "UI Components Look and Feel", Style.getLookAndFeels().toArray(new String[0]),  "System default", Model.ComboBox));
		// update style setting if already set
		((SettingSelection) s.getSettingFromId("ui.style")).setValues(Style.STYLES);
		
		//Others
		s.addSetting(new SettingBoolean("ui.hideintray", "Hide in tray", SettingCategory.Others, "Hide in system tray when closing.", false));
		s.addSetting(new SettingBoolean("main.checkupdates", "Check for updates", SettingCategory.Others, "Shows a notification when a new version is available.", true));
		
		//Intern
		s.addSetting(new SettingObject("mainFrame.size", "Window size", new Dimension(850, 450)));
		s.addSetting(new SettingObject("simulatorFrame.size", "Simulator Window size", new Dimension(650, 150)));
	}

}
