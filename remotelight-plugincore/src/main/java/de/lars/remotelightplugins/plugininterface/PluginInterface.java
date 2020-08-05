package de.lars.remotelightplugins.plugininterface;

import de.lars.remotelightcore.EffectManagerHelper;
import de.lars.remotelightcore.animation.AnimationManager;
import de.lars.remotelightcore.colors.ColorManager;
import de.lars.remotelightcore.devices.DeviceManager;
import de.lars.remotelightcore.event.EventHandler;
import de.lars.remotelightcore.lua.LuaManager;
import de.lars.remotelightcore.musicsync.MusicSyncManager;
import de.lars.remotelightcore.notification.NotificationManager;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.scene.SceneManager;
import de.lars.remotelightcore.screencolor.ScreenColorManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightplugins.PluginManager;

/**
 * Interface between RemoteLight application and the plugin.
 */
public interface PluginInterface {

	/**
	 * Get the {@link PluginManager} used for managing
	 * loaded plugins.
	 * 
	 * @return	the plugin manager
	 */
	PluginManager getPluginManager();
	
	/**
	 * Get the settings manager to manage plugin settings
	 * and data.
	 * 
	 * @return	the settings manager instance
	 */
	SettingsManager getSettingsManager();
	
	/**
	 * Get the event handler to register event listener,
	 * to call events or to create new events.
	 * 
	 * @return	the event handler instance
	 */
	EventHandler getEventHandler();
	
	/**
	 * Get the notification manager to show notifications.
	 * 
	 * @return	the notification manager instance
	 */
	NotificationManager getNotificationManager();
	
	/**
	 * Get the output manager to output color arrays, managing
	 * output devices or to set the output delay.
	 * 
	 * @return	the output manager instance
	 */
	OutputManager getOutputManager();
	
	/**
	 * Get the device manager to adding or removing output
	 * devices.
	 * 
	 * @return	the device manager instance
	 */
	DeviceManager getDeviceManager();
	
	/**
	 * Get the effect manager helper to stop or get
	 * active effects.
	 * 
	 * @return	the effect manager instance
	 */
	EffectManagerHelper getEffectManagerHelper();
	
	/**
	 * Get the color manager to show static colors.
	 * 
	 * @return	the color manager instance
	 */
	ColorManager getColorManager();
	
	/**
	 * Get the animation manager to start, stop or add animations.
	 * 
	 * @return	the animation manager instance
	 */
	AnimationManager getAnimationManager();
	
	/**
	 * Get the scene manager to start, stop or add scenes.
	 * 
	 * @return	the scene manager instance
	 */
	SceneManager getSceneManager();
	
	/**
	 * Get the MusicSync manager to start, stop or add
	 * music visualizer.
	 * 
	 * @return	the MusicSync manager instance
	 */
	MusicSyncManager getMusicSyncManager();
	
	/**
	 * Get the ScreenColor manager to start or stop the ScreenColor season.
	 * 
	 * @return	the ScreenColor manager instance
	 */
	ScreenColorManager getScreenColorManager();
	
	/**
	 * Get the Lua manager to controlling Lua scripts.
	 * 
	 * @return	the Lua manager instance
	 */
	LuaManager getLuaManager();
	
}
