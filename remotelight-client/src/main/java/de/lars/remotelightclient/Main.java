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
package de.lars.remotelightclient;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.tinylog.Logger;

import com.formdev.flatlaf.FlatLaf;

import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.FlatLafThemesUtil;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.utils.ExceptionHandler;

public class Main {
	
	public final static String VERSION = "pre0.2.0.9.2";
	
	private static Main instance;
	private RemoteLightCore remoteLightCore;
	private MainFrame mainFrame;

	public static void main(String[] args) {
		new Main(args, true);
	}
	
	public Main(String[] args, boolean uiMode) {
		instance = this;
		remoteLightCore = new RemoteLightCore(args, uiMode);
		// register shutdown hook
		remoteLightCore.registerShutdownHook();
		
		Logger.info("Starting RemoteLightClient version " + VERSION);
		setupOSSupport();
		Style.loadFonts();				// Load custom fonts
		
		new StartUp(RemoteLightCore.startParameter);
		
		if(uiMode)
			startMainFrame();
	}
	
	/**
	 * Starts the UI
	 */
	public void startMainFrame() {
		// set look and feel
		boolean failed = !setLookAndFeel();
		if(failed) {
			try {
				UIManager.setLookAndFeel(new MetalLookAndFeel());
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		}
		Logger.info((failed ? "[FAILED] switch to standard LaF: " : "Selected Look and Feel: ") + UIManager.getLookAndFeel().getName());
		
		// register error handler in MainFrame
		ExceptionHandler.registerListener(MainFrame.onException);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainFrame = new MainFrame();
					mainFrame.setVisible(!RemoteLightCore.startParameter.tray);
				} catch (Exception e) {
					Logger.error(e);
				}
			}
		});
	}
	
	public boolean setLookAndFeel() {
		SettingSelection sLaF = (SettingSelection) remoteLightCore.getSettingsManager().getSettingFromId("ui.laf");
		UiUtils.setThemingEnabled(true);
		try {
			if(sLaF == null || sLaF.getSelected().equalsIgnoreCase("System default")) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				return true;
			}
			String lafName = sLaF.getSelected();
			
			if(lafName.equalsIgnoreCase("Java default")) {
				UIManager.setLookAndFeel(new MetalLookAndFeel());
				return true;
			}
			
			for(FlatLaf laf : FlatLafThemesUtil.getAllThemes()) {
				if(laf.getName().equalsIgnoreCase(lafName)) {
					FlatLaf.install(laf);
					UiUtils.setThemingEnabled(false);
					return true;
				}
			}
		} catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	
	public static void setupOSSupport() {
		// MacOS menu bar application name
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.application.name", "RemoteLight");
	}
	
	
	public static Main getInstance() {
		return instance;
	}
	
	public RemoteLightCore getCore() {
		return remoteLightCore;
	}
	
	public MainFrame getMainFrame() {
		return mainFrame;
	}
	
	/**
	 * Wrapper method
	 * @return settings manager of RemoteLightCore
	 */
	public SettingsManager getSettingsManager() {
		return getCore().getSettingsManager();
	}

	/**
	 * Returns the number of LEDs of the active output
	 * @return
	 */
	public static int getLedNum() {
		return RemoteLightCore.getLedNum();
	}
	
	/**
	 * Notification show wrapper method
	 * @param notificationType 	Notification type
	 * @param text				displayed message
	 */
	public void showNotification(NotificationType notificationType, String text) {
		remoteLightCore.showNotification(notificationType, text, Notification.NORMAL);
	}
	
	/**
	 * Notification show wrapper method
	 * @param notificationType 	Notification type
	 * @param text				displayed message
	 * @param displayTime		display time length
	 */
	public void showNotification(NotificationType notificationType, String text, int displayTime) {
		remoteLightCore.showNotification(notificationType, text, displayTime);
	}
	
	/**
	 * Notification show wrapper method
	 * @param notificationType 	Notification type
	 * @param title				notification title
	 * @param text				displayed message
	 */
	public void showNotification(NotificationType notificationType, String title, String text) {
		remoteLightCore.showNotification(new Notification(notificationType, title, text));
	}
	
	/**
	 * Notification wrapper method
	 * @param notification new notification
	 */
	public void showNotification(Notification notification) {
		remoteLightCore.showNotification(notification);
	}

}
