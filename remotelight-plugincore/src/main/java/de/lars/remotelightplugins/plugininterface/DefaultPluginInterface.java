package de.lars.remotelightplugins.plugininterface;

import de.lars.remotelightcore.EffectManagerHelper;
import de.lars.remotelightcore.RemoteLightCore;
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
 * Default implementation of the plugin interface.
 */
public class DefaultPluginInterface implements PluginInterface {
	
	final RemoteLightCore core;
	final PluginManager manager;
	
	/**
	 * Create a new default plugin interface
	 * 
	 * @param core		{@link RemoteLightCore} instance
	 * @param manager	{@link PluginManager} instance
	 */
	public DefaultPluginInterface(final RemoteLightCore core, final PluginManager manager) {
		this.core = core;
		this.manager = manager;
	}

	@Override
	public PluginManager getPluginManager() {
		return manager;
	}

	@Override
	public SettingsManager getSettingsManager() {
		return core.getSettingsManager();
	}

	@Override
	public EventHandler getEventHandler() {
		return core.getEventHandler();
	}

	@Override
	public NotificationManager getNotificationManager() {
		return core.getNotificationManager();
	}

	@Override
	public OutputManager getOutputManager() {
		return core.getOutputManager();
	}

	@Override
	public DeviceManager getDeviceManager() {
		return core.getDeviceManager();
	}

	@Override
	public EffectManagerHelper getEffectManagerHelper() {
		return core.getEffectManagerHelper();
	}

	@Override
	public ColorManager getColorManager() {
		return core.getColorManager();
	}

	@Override
	public AnimationManager getAnimationManager() {
		return core.getAnimationManager();
	}

	@Override
	public SceneManager getSceneManager() {
		return core.getSceneManager();
	}

	@Override
	public MusicSyncManager getMusicSyncManager() {
		return core.getMusicSyncManager();
	}

	@Override
	public ScreenColorManager getScreenColorManager() {
		return core.getScreenColorManager();
	}

	@Override
	public LuaManager getLuaManager() {
		return core.getLuaManager();
	}

}
