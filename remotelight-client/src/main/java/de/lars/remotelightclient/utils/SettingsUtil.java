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
package de.lars.remotelightclient.utils;

import de.lars.remotelightclient.ui.panels.settings.settingComps.*;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.types.*;

public class SettingsUtil {
	
	/**
	 * 
	 * @param s Setting subclass
	 * @return The corresponding setting panel
	 */
	public static SettingPanel getSettingPanel(Setting s) {
		if(s instanceof SettingString) {
			return new SettingStringPanel((SettingString) s);
		}
		if(s instanceof SettingBoolean) {
			return new SettingBooleanPanel((SettingBoolean) s);
		}
		if(s instanceof SettingColor) {
			return new SettingColorPanel((SettingColor) s);
		}
		if(s instanceof SettingDouble) {
			return new SettingDoublePanel((SettingDouble) s);
		}
		if(s instanceof SettingInt) {
			return new SettingIntPanel((SettingInt) s);
		}
		if(s instanceof SettingSelection) {
			return new SettingSelectionPanel((SettingSelection) s);
		}
		return null;
	}

}
