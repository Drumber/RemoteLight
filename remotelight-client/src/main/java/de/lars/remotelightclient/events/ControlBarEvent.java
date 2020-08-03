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

package de.lars.remotelightclient.events;

import de.lars.remotelightclient.ui.panels.controlbars.ControlBar;
import de.lars.remotelightcore.event.events.Event;

/**
 * Class holding the {@link ControlBarChangeEvent} and {@link ControlBarVisibleEvent}.
 */
public class ControlBarEvent {
	
	/**
	 * ControlBarChangeEvent called when the control bar is changed.
	 */
	public static class ControlBarChangeEvent implements Event {
		
		private final ControlBar controlBar;
		
		public ControlBarChangeEvent(final ControlBar controlBar) {
			this.controlBar = controlBar;
		}
		
		/**
		 * Get the new ControlBar or null.
		 * 
		 * @return the ControlBar or null
		 */
		public ControlBar getControlBar() {
			return controlBar;
		}
		
	}
	
	/**
	 * ControlBarVisibleEvent called when the visibility of the control bar
	 * is changed.
	 */
	public static class ControlBarVisibleEvent implements Event {
		
		private final boolean visible;
		
		public ControlBarVisibleEvent(final boolean visible) {
			this.visible = visible;
		}
		
		/**
		 * Get the visibility state.
		 * 
		 * @return true if the ControlBar is visible, false otherwise
		 */
		public boolean isVisible() {
			return visible;
		}
		
	}

}
