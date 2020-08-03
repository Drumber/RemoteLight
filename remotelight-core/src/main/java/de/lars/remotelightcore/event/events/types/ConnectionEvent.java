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
import de.lars.remotelightcore.out.Output;

/**
 * ConnectionEvent called on activating or deactivating an output.
 */
public class ConnectionEvent implements Event {
	
	public enum Action {
		ACTIVATE,
		DEACTIVATE;
	}
	
	private final Output output;
	private final Action action;
	
	public ConnectionEvent(Output output, Action action) {
		this.output = output;
		this.action = action;
	}

	/**
	 * Get the event output
	 * 
	 * @return the event output
	 */
	public Output getOutput() {
		return output;
	}

	/**
	 * Get the connection type
	 * 
	 * @return 	{@link Action#ACTIVATE} on enabling output or <br>
	 * 			{@link Action#DEACTIVATE} on disabling output
	 */
	public Action getAction() {
		return action;
	}

}
