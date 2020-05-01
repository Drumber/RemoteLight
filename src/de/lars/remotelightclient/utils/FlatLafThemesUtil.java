package de.lars.remotelightclient.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.*;
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
		new FlatArcIJTheme(), new FlatArcOrangeIJTheme(),
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
			new FlatArcDarkIJTheme(), new FlatArcDarkContrastIJTheme(),
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
