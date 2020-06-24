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

package de.lars.remotelightcore.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.tinylog.Logger;

import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.io.FileStorage;
import de.lars.remotelightcore.settings.Setting;

public class DataFileUpdater {
	
	public DataFileUpdater(FileStorage fileStorage) {
		if(DataStorage.getstorage() != null)
			DataStorage.save();
		
		DataStorage.start();
		if(DataStorage.getstorage() == null) {
			Logger.error("Error: Failed to initialize old DataStorage. Can not update data to new format!");
			return;
		}
		
		Object deviceArray = DataStorage.getData(DataStorage.DEVICES_LIST);
		// should be Device array
		if(deviceArray instanceof Device[]) {
			// update to new format
			List<Device> devices = Arrays.asList((Device[]) deviceArray);
			fileStorage.store(fileStorage.KEY_DEVICES_LIST, devices);
			Logger.debug("Updated device array to new file storage.");
		} else {
			Logger.warn("Could not update device list. Expected Device array, got " + deviceArray.getClass().getName());
		}
		
		Object settingsList = DataStorage.getData(DataStorage.SETTINGSMANAGER_KEY);
		// should be List
		if(settingsList instanceof List<?>) {
			@SuppressWarnings("unchecked")
			List<Setting> settings = (List<Setting>) settingsList;
			fileStorage.store(fileStorage.KEY_SETTINGS_LIST, settings);
			Logger.debug("Updated settings list to new file storage.");
		} else {
			Logger.warn("Could not update settings list. Expected List<?>, got " + settingsList.getClass().getName());
		}
		
		Logger.debug("Trying to save new file storage.");
		try {
			fileStorage.save();
			Logger.debug("...saved");
		} catch (IOException e) {
			Logger.error(e, "Attemp to save new file storage failed.");
		}
	}

}
