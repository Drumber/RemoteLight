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
package de.lars.remotelightclient;

import java.awt.Dimension;
import java.util.Locale;

import de.lars.remotelightclient.lang.LangUtil;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.comps.UpdateDialog;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.cmd.StartParameterHandler;
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
		
		//set language
		Locale.setDefault(new Locale(LangUtil.langNameToCode(((SettingSelection) s.getSettingFromId("ui.language")).getSelected())));
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				//check for update (this block blocks the thread)
				if(((SettingBoolean) s.getSettingFromId("main.checkupdates")).getValue() || startParameter.updateChecker) {
					// show update dialog
					UpdateChecker updateChecker = new UpdateChecker(Main.VERSION);
					Main.getInstance();
					if(updateChecker.isNewVersionAvailable() && !RemoteLightCore.isHeadless()) {
						UpdateDialog.showUpdateDialog(updateChecker);
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
	
	/**
	 * Enables backward compatibility
	 * <p>
	 * Updates old package names to new package names
	 */
	public void updateDataFileContent() {
		
	}

}
