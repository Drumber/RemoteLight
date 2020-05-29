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
package de.lars.remotelightcore;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.tinylog.Logger;

import de.lars.remotelightcore.cmd.CommandParser;
import de.lars.remotelightcore.cmd.StartParameterHandler;
import de.lars.remotelightcore.cmd.exceptions.CommandException;
import de.lars.remotelightcore.lang.LangUtil;
import de.lars.remotelightcore.out.Output;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingObject;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
import de.lars.remotelightcore.ui.Style;
import de.lars.remotelightcore.ui.comps.UpdateDialog;
import de.lars.remotelightcore.utils.DirectoryUtil;
import de.lars.remotelightcore.utils.UpdateChecker;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class StartUp {
	
	private SettingsManager s = Main.getInstance().getSettingsManager();

	public StartUp(StartParameterHandler startParameter) {
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
				if(s.getSettingFromType(new SettingBoolean("out.autoconnect", null, null, null, false)).getValue() || startParameter.autoConnect) {
					Output output = (Output) s.getSettingFromType(new SettingObject("out.lastoutput", null, null)).getValue();
					if(output != null) {
						Main.getInstance().getOutputManager().setActiveOutput(output);
					}
				}
				
				//auto enable last effect
				if(s.getSetting(SettingBoolean.class, "manager.lastactive.enabled").getValue()) {
					String cmd = (String) s.getSettingObject("manager.lastactive.command").getValue();
					if(cmd != null) {
						try {
							CommandParser commandParser = Main.getInstance().getCommandParser();
							commandParser.setOutputEnabled(false);
							commandParser.parse(cmd.split(" "));
							commandParser.setOutputEnabled(true);
						} catch (CommandException e) {
							Logger.error(e, "Could not enable last effect. Command: " + cmd);
						}
					}
				}
				
				//check for update (this block blocks the thread)
				if(((SettingBoolean) s.getSettingFromId("main.checkupdates")).getValue() || startParameter.updateChecker) {
					// show update dialog
					UpdateChecker updateChecker = new UpdateChecker(Main.VERSION);
					Main.getInstance();
					if(updateChecker.isNewVersionAvailable() && !Main.isHeadless()) {
						UpdateDialog.showUpdateDialog(updateChecker);
					}
				}
				
			}
		}, "Startup thread").start();
	}
	
	private void init() {
		RainbowWheel.init();
		// Lua
		File luaDir = new File(DirectoryUtil.getLuaPath());
		// copy Lua example files
		try {
			DirectoryUtil.copyFolderFromJar("resources/lua_examples", luaDir, false);
		} catch (IOException e) {
		}
	}
	
	public void registerSettings() {
		//General
		s.addSetting(new SettingSelection("ui.language", "Language", SettingCategory.General, "UI Language", LangUtil.langCodeToName(LangUtil.LANGUAGES), "English", Model.ComboBox));
		s.addSetting(new SettingSelection("ui.style", "Style", SettingCategory.General, "Colors of the UI", Style.STYLES, "Dark", Model.ComboBox));
		s.addSetting(new SettingSelection("ui.laf", "LookAndFeel", SettingCategory.General, "UI Components Look and Feel", Style.getLookAndFeels().toArray(new String[0]),  "System default", Model.ComboBox));
		// update style setting if already set
		((SettingSelection) s.getSettingFromId("ui.style")).setValues(Style.STYLES);
		s.addSetting(new SettingInt("out.delay", "Output delay", SettingCategory.General, "Delay (ms) between sending output packets.", 50, 5, 500, 5));
		s.addSetting(new SettingBoolean("out.autoconnect", "Auto connect", SettingCategory.General, "Automaticly connect/open last used output.", false));
		s.addSetting(new SettingBoolean("manager.lastactive.enabled", "Auto enable last effect", SettingCategory.General, "Automaticly enable last used effect/animation.", false));
		
		//Others
		s.addSetting(new SettingBoolean("ui.hideintray", "Hide in tray", SettingCategory.Others, "Hide in system tray when closing.", false));
		s.addSetting(new SettingBoolean("main.checkupdates", "Check for updates", SettingCategory.Others, "Shows a notification when a new version is available.", true));
		
		//Intern
		s.addSetting(new SettingObject("out.lastoutput", "Last active Output", null));
		s.addSetting(new SettingObject("out.brightness", null, 100));
		s.addSetting(new SettingObject("mainFrame.size", "Window size", new Dimension(850, 450)));
		s.addSetting(new SettingObject("simulatorFrame.size", "Simulator Window size", new Dimension(650, 150)));
		s.addSetting(new SettingObject("manager.lastactive.command", "Last active effect start command", null));
		
	}
	
	public void setSettingValues() {
		Main.getInstance().getOutputManager().setBrightness((int) s.getSettingObject("out.brightness").getValue());
	}

}
