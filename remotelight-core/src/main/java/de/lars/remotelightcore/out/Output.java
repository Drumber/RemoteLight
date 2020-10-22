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

package de.lars.remotelightcore.out;

import de.lars.remotelightcore.utils.color.Color;
import java.io.Serializable;

import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.out.patch.OutputPatch;

public abstract class Output implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4585718970709898453L;
	private String id;
	private int pixels;
	private OutputPatch outputPatch;
	
	public Output(String id, int pixels) {
		this.id = id;
		this.pixels = pixels;
		outputPatch = new OutputPatch(pixels);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public int getPixels() {
		return pixels;
	}

	public void setPixels(int pixels) {
		this.pixels = pixels;
		getOutputPatch().setPixelNumber(pixels);
	}
	
	public OutputPatch getOutputPatch() {
		// backward compatible
		if(outputPatch == null)
			outputPatch = new OutputPatch(pixels);
		return outputPatch;
	}
	
	public void onActivate() {
	}
	
	public void onDeactivate() {
	}
	
	public abstract ConnectionState getState();

	public void onOutput(Color[] pixels) {
	}

}
