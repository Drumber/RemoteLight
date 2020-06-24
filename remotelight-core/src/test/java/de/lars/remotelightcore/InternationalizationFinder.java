package de.lars.remotelightcore;

import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager;

public class InternationalizationFinder {
	
	/**
	 * Find missing translations of settings and
	 * output them formatted in the console
	 * 
	 * @param args start parameter (not used)
	 */
	public static void main(String[] args) {
		RemoteLightCore core = new RemoteLightCore(args, true);
		SettingsManager s = core.getSettingsManager();
		i18n.setLocale("en");
		
		System.out.println("====================");
		
		for(Setting setting : s.getSettings()) {
			String id = setting.getId();
			String name = setting.getName();
			String description = setting.getDescription();
			
			if(!isEmpty(name) && i18n.getSettingString(id + ".name") == null) {
				System.out.println(id + ".name = " + name);
			}
			
			if(!isEmpty(description) && i18n.getSettingString(id + ".description") == null) {
				System.out.println(id + ".description = " + description);
			}
		}
		
		System.out.println("====================");
	}
	
	static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
	
}
