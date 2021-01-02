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

package de.lars.remotelightcore;

import de.lars.remotelightcore.event.EventHandler;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public abstract class EffectManager {

	public abstract String getName();
	public abstract void stop();
	public abstract boolean isActive();
	
	protected void turnOffLeds() {
		if(RemoteLightCore.getInstance().getSettingsManager().getSetting(SettingBoolean.class, "out.effects.disableleds").get()) {
			// turn off leds when disabling effect
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
		}
	}
	
	protected EventHandler getEventHandler() {
		return RemoteLightCore.getInstance().getEventHandler();
	}
	
}
