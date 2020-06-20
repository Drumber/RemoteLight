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

package de.lars.remotelightcore;

import java.io.File;
import java.io.IOException;

import org.tinylog.Logger;

import de.lars.remotelightcore.cmd.CommandParser;
import de.lars.remotelightcore.cmd.exceptions.CommandException;
import de.lars.remotelightcore.devices.Device;
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
					
					String outputID = (String) s.getSettingObject("out.lastoutput").getValue();
					if (outputID != null && core.getDeviceManager().isIdUsed(outputID)) {
						Device device = core.getDeviceManager().getDevice(outputID);
						core.getOutputManager().setActiveOutput(device);
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
