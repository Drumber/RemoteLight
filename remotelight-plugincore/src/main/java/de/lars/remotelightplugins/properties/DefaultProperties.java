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

package de.lars.remotelightplugins.properties;

public enum DefaultProperties {
	
	/** plugin main class */
	MAIN("main", true),
	/** plugin name or ID (must be unique) */
	NAME("name", true),
	/** plugin displayname */
	DISPLAYNAME("displayname", false),
	/** plugin author */
	AUTHOR("author", true),
	/** plugin version (should be in format 'x.x.x') */
	VERSION("version", true),
	/** plugin dependencies without which the plugin will not work */
	DEPENDENCIES("dependencies", false),
	/** plugin soft dependencies that are optional */
	SOFTDEPENDENCIES("softdependencies", false),
	/** plugin scope (all, mobile, web, swing) */
	SCOPE("scope", false),
	/** plugin website url */
	URL("url", false),
	/** minimum RemoteLightCore version */
	MIN_VERSION("minVersion", false),
	/** maximum RemoteLightCore version */
	MAX_VERSION("maxVersion", false);
	
	private final String key;
	private final boolean required;
	
	private DefaultProperties(final String key, final boolean required) {
		this.key = key;
		this.required = required;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public String getKey() {
		return toString();
	}
	
	/**
	 * Returns whether the property is required or optional
	 * 
	 * @return true if required, false otherwise
	 */
	public boolean isRequired() {
		return required;
	}

}
