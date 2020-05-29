package de.lars.remotelightcore;

import java.io.File;
import java.io.IOException;

import org.tinylog.Logger;

import de.lars.remotelightcore.cmd.CommandParser;
import de.lars.remotelightcore.cmd.exceptions.CommandException;
import de.lars.remotelightcore.out.Output;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingObject;
import de.lars.remotelightcore.utils.DirectoryUtil;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class SetupHelper {

	private RemoteLightCore core;
	private SettingsManager s;

	public SetupHelper(RemoteLightCore core) {
		this.core = core;
		s = core.getSettingsManager();

		this.init();
		this.registerSettings();
		this.clean();
		this.automation();
	}

	private void init() {
		// Rainbow array
		RainbowWheel.init();
		// Lua
		File luaDir = new File(DirectoryUtil.getLuaPath());
		// copy Lua example files
		try {
			DirectoryUtil.copyFolderFromJar(DirectoryUtil.RESOURCES_CLASSPATH + "lua_examples", luaDir, false);
		} catch (IOException e) {
		}
	}

	private void clean() {
		// delete old logs
		int days = s.getSetting(SettingInt.class, "logs.deletedays").getValue();
		DirectoryUtil.deleteOldLogs(days);
	}

	private void registerSettings() {
		s.addSetting(new SettingInt("out.delay", "Output delay", SettingCategory.General,
				"Delay (ms) between sending output packets.", 50, 5, 500, 5));
		s.addSetting(new SettingBoolean("out.autoconnect", "Auto connect", SettingCategory.General,
				"Automaticly connect/open last used output.", false));
		s.addSetting(new SettingBoolean("manager.lastactive.enabled", "Auto enable last effect",
				SettingCategory.General, "Automaticly enable last used effect/animation.", false));
		s.addSetting(new SettingInt("logs.deletedays", "Delete log files older than x days", SettingCategory.Others,
				null, 2, 1, 30, 1));

		// Intern
		s.addSetting(new SettingObject("out.lastoutput", "Last active Output", null));
		s.addSetting(new SettingObject("out.brightness", null, 100));
		s.addSetting(new SettingObject("manager.lastactive.command", "Last active effect start command", null));

		// set values
		core.getOutputManager().setBrightness((int) s.getSettingObject("out.brightness").getValue());
	}

	private void automation() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				// auto connect feature)
				if (s.getSetting(SettingBoolean.class, "out.autoconnect").getValue()
						|| RemoteLightCore.startParameter.autoConnect) {
					
					Output output = (Output) s.getSettingObject("out.lastoutput").getValue();
					if (output != null) {
						core.getOutputManager().setActiveOutput(output);
					}
				}

				// auto enable last effect
				if (s.getSetting(SettingBoolean.class, "manager.lastactive.enabled").getValue()) {
					String cmd = (String) s.getSettingObject("manager.lastactive.command").getValue();
					if (cmd != null) {
						try {
							CommandParser commandParser = core.getCommandParser();
							commandParser.setOutputEnabled(false);
							commandParser.parse(cmd.split(" "));
							commandParser.setOutputEnabled(true);
						} catch (CommandException e) {
							Logger.error(e, "Could not enable last effect. Command: " + cmd);
						}
					}
				}
			}
		}, "Start Thread").start();
	}

}
