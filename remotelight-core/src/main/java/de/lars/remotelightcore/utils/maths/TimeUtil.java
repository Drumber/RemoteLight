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

package de.lars.remotelightcore.utils.maths;

public class TimeUtil {
	
	private long startTime;
	private int interval;
	private boolean autoReset;
	
	/**
	 * 
	 * @param interval time in milliseconds to wait
	 */
	public TimeUtil(int interval) {
		this(interval, true);
	}
	
	/**
	 * 
	 * @param interval time in milliseconds to wait
	 * @param autoReset automatically reset if the time is over
	 */
	public TimeUtil(int interval, boolean autoReset) {
		this.interval = interval;
		this.autoReset = autoReset;
		this.reset();
	}
	
	public void reset() {
		this.startTime = this.getCurrentTime();
	}
	
	public long getCurrentTime() {
		return System.currentTimeMillis();
	}
	
	public boolean hasReached() {
		if(this.getRemainingTime() >= interval) {
			if(autoReset) {
				this.reset();
			}
			return true;
		}
		return false;
	}
	
	public long getRemainingTime() {
		return this.getCurrentTime() - startTime;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	public int getInterval() {
		return this.interval;
	}

}
