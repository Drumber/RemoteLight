package de.lars.remotelightplugins.test;

import java.io.File;

import de.lars.remotelightplugins.PluginManager;

public class PluginTest {
	
	public static void main(String[] args) {
		
		File pluginDir = new File("plugins");
		System.out.println(pluginDir.getAbsolutePath());
		pluginDir.mkdir();
		PluginManager manager = new PluginManager(pluginDir, null, null);
		System.out.println("Loading plugins with manager...");
		manager.loadPlugins();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		manager.disablePlugins();
	}

}
