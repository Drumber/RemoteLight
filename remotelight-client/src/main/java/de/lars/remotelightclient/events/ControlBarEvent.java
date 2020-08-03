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
