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

package de.lars.remotelightcore.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.tinylog.Logger;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationType;

public class AutoSave implements Runnable {
	
	public interface AutoSaveEvent {
		void onAutoSave(FileStorage storage);
	}
	
	private ScheduledThreadPoolExecutor service;
	private ScheduledFuture<?> future;
	/** auto save delay in minutes */
	private int delay = 5;
	
	private FileStorage fileStorage;
	private List<AutoSaveEvent> saveListeners;
	
	/**
	 * Create a new AutoSaver instance.
	 * This will trigger all added AutoSave listeners every {@link #delay} minutes.
	 * The AutoSaver is not active on initialization, it needs to be started using {@link #start()}.
	 * 
	 * @param fileStorage FileStorage instance
	 */
	public AutoSave(FileStorage fileStorage) {
		this.fileStorage = fileStorage;
		saveListeners = new ArrayList<>();
		service = new ScheduledThreadPoolExecutor(1);
		service.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		service.setRemoveOnCancelPolicy(true);
	}
	
	public void start() {
		if(future == null || future.isDone()) {
			future = service.scheduleWithFixedDelay(this, delay, delay, TimeUnit.MINUTES);
		}
	}
	
	public void stop() {
		if(future != null)
			future.cancel(false);
	}
	
	public void setEnabled(boolean enabled) {
		if(enabled && !isActive())
			start();
		else
			stop();
	}
	
	public boolean isActive() {
		return service.getQueue().size() > 0 || (future != null && !future.isDone());
	}
	
	public void addSaveListener(AutoSaveEvent listener) {
		saveListeners.add(listener);
	}
	
	public void removeSaveListener(AutoSaveEvent listener) {
		saveListeners.remove(listener);
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
		// restart
		if(isActive()) {
			stop();
			start();
		}
	}
	

	@Override
	public void run() {
		if(fileStorage == null) {
			Logger.error("Could not perform auto save. FileStorage instance is null!");
			RemoteLightCore.getInstance().showNotification(
					new Notification(NotificationType.ERROR, "AutoSave", "Automatic saving failed. No FileStorage instance was created."));
			return;
		}
		
		Logger.debug("[AutoSave] Saving data...");
		
		for(AutoSaveEvent listener : saveListeners) {
			if(listener == null) {
				saveListeners.remove(listener);
				continue;
			}
			// trigger all valid listeners
			listener.onAutoSave(fileStorage);
		}
		
		// saving to data file
		try {
			fileStorage.save();
		} catch (IOException e) {
			Logger.error(e, "Error while auto saving data.");
			RemoteLightCore.getInstance().showErrorNotification(e, "AutoSave");
		}
	}

}
