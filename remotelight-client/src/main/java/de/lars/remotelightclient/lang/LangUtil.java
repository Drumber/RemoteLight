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
package de.lars.remotelightclient.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LangUtil {
	
	public final static String[] LANGUAGES = {"en", "de"};
	
	
	public static String[] langCodeToName(String[] codes) {
		List<String> tmp = new ArrayList<>();
		for(String s : codes) {
			tmp.add(langCodeToName(s));
		}
		return tmp.toArray(new String[tmp.size()]);
	}
	
	public static String langCodeToName(String code) {
		return new Locale(code).getDisplayName(new Locale("en"));
	}
	
	public static String langNameToCode(String name) {
		for(int i = 0; i < LANGUAGES.length; i++) {
			if(name.equalsIgnoreCase(langCodeToName(LANGUAGES[i]))) {
				return LANGUAGES[i];
			}
		}
		return "en"; //prevent NullPointException
	}

}
