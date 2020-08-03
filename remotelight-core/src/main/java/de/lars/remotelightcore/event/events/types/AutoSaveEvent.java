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

package de.lars.remotelightcore.event.events.types;

import de.lars.remotelightcore.event.events.Event;
import de.lars.remotelightcore.event.events.Stated;
import de.lars.remotelightcore.io.FileStorage;

/**
 * AutoSaveEvent called on auto save. This event has two states:
 * <p>
 * PRE: before saving <br>
 * POST: after saving
 */
public class AutoSaveEvent implements Event, Stated {
	
	private final State state;
	private final FileStorage storage;
	
	public AutoSaveEvent(final FileStorage storage, final State state) {
		this.state = state;
		this.storage = storage;
	}

	@Override
	public State getState() {
		return state;
	}

	/**
	 * Get the FileStorage instance that is saved.
	 * 
	 * @return the event FileStorage instance
	 */
	public FileStorage getStorage() {
		return storage;
	}

}
