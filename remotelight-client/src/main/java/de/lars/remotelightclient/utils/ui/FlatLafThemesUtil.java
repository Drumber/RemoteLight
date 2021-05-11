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

package de.lars.remotelightclient.utils.ui;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;

public class FlatLafThemesUtil {
	
	/**
	 * FlatLaf Core Themes
	 */
	public final static FlatLaf[] CORE_THEMES = {
			new FlatLightLaf(), new FlatDarkLaf(), new FlatIntelliJLaf(), new FlatDarculaLaf()
	};
	
	
	public static void installFlatLafThemes() {
		for(FlatLaf laf : CORE_THEMES) {
			FlatLaf.installLafInfo(laf.getName(), laf.getClass());
		}
		for(LookAndFeelInfo info: FlatAllIJThemes.INFOS) {
			UIManager.installLookAndFeel(info);
		}
	}
	
}
