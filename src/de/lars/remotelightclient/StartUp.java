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

import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.swing.JOptionPane;

import de.lars.remotelightclient.lang.LangUtil;
import de.lars.remotelightclient.out.Output;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.settings.types.SettingSelection.Model;
import de.lars.remotelightclient.utils.DirectoryUtil;
import de.lars.remotelightclient.utils.RainbowWheel;
import de.lars.remotelightclient.utils.UpdateChecker;

public class StartUp {
	
	private SettingsManager s = Main.getInstance().getSettingsManager();

	public StartUp() {
		//methods which need to be initialized on start up
		this.init();
		//delete old logs
		DirectoryUtil.deleteOldLogs(2);
		//register default settings
		registerSettings();
		
		//set language
		Locale.setDefault(new Locale(LangUtil.langNameToCode(((SettingSelection) s.getSettingFromId("ui.language")).getSelected())));
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				//auto connect feature
				if(s.getSettingFromType(new SettingBoolean("out.autoconnect", null, null, null, false)).getValue()) {
					Output output = (Output) s.getSettingFromType(new SettingObject("out.lastoutput", null, null)).getValue();
					if(output != null) {
						Main.getInstance().getOutputManager().setActiveOutput(output);
					}
				}
				
				//check for update (this block blocks the thread)
				if(((SettingBoolean) s.getSettingFromId("main.checkupdates")).getValue()) {
					UpdateChecker updateChecker = new UpdateChecker(Main.VERSION);
					if(updateChecker.isNewVersionAvailable()) {
						int option = JOptionPane.showOptionDialog(null, "New Version of RemoteLight available!\nCurrent: " + Main.VERSION + " New: " + updateChecker.getNewTag(),
								"Download new version", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
								new String[] {"Download", "Ignore"}, "Download");
						if(option == 0) { // when user click Download, open Browser
							try {
								Desktop.getDesktop().browse(new URI(updateChecker.getNewUrl()));
							} catch (URISyntaxException | IOException ex) {
							}
						}
					}
				}
				
			}
		}, "Startup thread").start();
	}
	
	private void init() {
		RainbowWheel.init();
		new File(DirectoryUtil.getLuaPath()).mkdir();
	}
	
	public void registerSettings() {
		//General
		s.addSetting(new SettingSelection("ui.language", "Language", SettingCategory.General, "UI Language", LangUtil.langCodeToName(LangUtil.LANGUAGES), "English", Model.ComboBox));
		s.addSetting(new SettingSelection("ui.style", "Style", SettingCategory.General, "Colors of the UI", new String[] {"Light", "Dark"}, "Dark", Model.ComboBox));
		s.addSetting(new SettingInt("out.delay", "Output delay", SettingCategory.General, "Delay (ms) between sending output packets.", 50, 5, 500, 5));
		s.addSetting(new SettingBoolean("out.autoconnect", "Auto connect", SettingCategory.General, "Automaticly connect/open last used output.", false));
		
		//Others
		s.addSetting(new SettingBoolean("ui.hideintray", "Hide in tray", SettingCategory.Others, "Hide in system tray when closing.", false));
		s.addSetting(new SettingBoolean("main.checkupdates", "Check for updates", SettingCategory.Others, "Shows a notification when a new version is available.", true));
		
		//Intern
		s.addSetting(new SettingObject("out.lastoutput", "Last active Output", null));
		s.addSetting(new SettingObject("out.brightness", null, 100));
		s.addSetting(new SettingObject("mainFrame.size", "Window size", new Dimension(850, 450)));
		
	}
	
	public void setSettingValues() {
		Main.getInstance().getOutputManager().setBrightness((int) s.getSettingObject("out.brightness").getValue());
	}

}
