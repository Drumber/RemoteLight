package de.lars.remotelightclient.utils;

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

}
