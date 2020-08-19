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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.*;

public class FlatLafThemesUtil {
	
	/**
	 * FlatLaf Core Themes
	 */
	public final static FlatLaf[] CORE_THEMES = {
			new FlatLightLaf(), new FlatDarkLaf(), new FlatIntelliJLaf(), new FlatDarculaLaf()
	};
	
	/**
	 * IntelliJ Themes
	 */
	public final static FlatLaf[] INTELLIJ_THEMES = {
		new FlatArcIJTheme(), new FlatArcOrangeIJTheme(), new FlatArcDarkIJTheme(), new FlatArcDarkOrangeIJTheme(),
		new FlatCarbonIJTheme(),
		new FlatCobalt2IJTheme(),
		new FlatCyanLightIJTheme(),
		new FlatDarkFlatIJTheme(), new FlatDarkPurpleIJTheme(),
		new FlatDraculaIJTheme(),
		new FlatGradiantoDarkFuchsiaIJTheme(), new FlatGradiantoDeepOceanIJTheme(), new FlatGradiantoMidnightBlueIJTheme(),
		new FlatGrayIJTheme(),
		new FlatGruvboxDarkHardIJTheme(), new FlatGruvboxDarkMediumIJTheme(), new FlatGruvboxDarkSoftIJTheme(),
		new FlatHiberbeeDarkIJTheme(),
		new FlatHighContrastIJTheme(),
		new FlatLightFlatIJTheme(),
		new FlatMaterialDesignDarkIJTheme(),
		new FlatMonocaiIJTheme(),
		new FlatNordIJTheme(),
		new FlatOneDarkIJTheme(),
		new FlatSolarizedDarkIJTheme(), new FlatSolarizedLightIJTheme(),
		new FlatSpacegrayIJTheme(),
		new FlatVuesionIJTheme()
	};
	
	/**
	 * Material Theme UI Lite
	 */
	public final static FlatLaf[] Material_THEMES = {
			new com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkIJTheme(), new FlatArcDarkContrastIJTheme(),
			new FlatAtomOneDarkIJTheme(), new FlatAtomOneDarkContrastIJTheme(),
			new FlatAtomOneLightIJTheme(), new FlatAtomOneLightContrastIJTheme(),
			new com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme(), new FlatDraculaContrastIJTheme(),
			new FlatGitHubIJTheme(), new FlatGitHubContrastIJTheme(),
			new FlatLightOwlIJTheme(), new FlatLightOwlContrastIJTheme(),
			new FlatMaterialDarkerIJTheme(), new FlatMaterialDarkerContrastIJTheme(),
			new FlatMaterialDeepOceanIJTheme(), new FlatMaterialDeepOceanContrastIJTheme(),
			new FlatMaterialLighterIJTheme(), new FlatMaterialLighterContrastIJTheme(),
			new FlatMaterialOceanicIJTheme(), new FlatMaterialOceanicContrastIJTheme(),
			new FlatMaterialPalenightIJTheme(), new FlatMaterialPalenightContrastIJTheme(),
			new FlatMonokaiProIJTheme(), new FlatMonokaiProContrastIJTheme(),
			new FlatNightOwlIJTheme(), new FlatNightOwlContrastIJTheme(),
			new com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkIJTheme(), new FlatSolarizedDarkContrastIJTheme(),
			new com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightIJTheme(), new FlatSolarizedLightContrastIJTheme()
	};
	
	
	public static List<FlatLaf> getAllThemes() {
		List<FlatLaf> themes = new ArrayList<>();
		themes.addAll(Arrays.asList(CORE_THEMES));
		themes.addAll(Arrays.asList(INTELLIJ_THEMES));
		themes.addAll(Arrays.asList(Material_THEMES));
		return themes;
	}
	
	public static List<String> getAllThemeNames() {
		List<String> names = new ArrayList<>();
		
		for(FlatLaf fl : getAllThemes()) {
			names.add(fl.getName());
		}
		return names;
	}

}
