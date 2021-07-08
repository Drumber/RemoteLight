package de.lars.remotelightclient.ui.components;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

/**
 * Subclass of {@link JPanel} that resizes in a {@link JScrollPane}.
 * <p>
 * Adapted from https://stackoverflow.com/a/15786939/12821118
 */
public class ScrollablePanel extends JPanel implements Scrollable {
	
	private int scrollIncrement;

	public ScrollablePanel() {
		this(16);
	}
	
	public ScrollablePanel(int scrollIncrement) {
		this.scrollIncrement = scrollIncrement;
	}

	public Dimension getPreferredScrollableViewportSize() {
		// tell the JScrollPane that we want to be our 'preferredSize' - but later, we'll say that vertically, it should scroll.
        return super.getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return scrollIncrement;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return scrollIncrement;
    }

    public boolean getScrollableTracksViewportWidth() {
    	// track the width, and re-size as needed.
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
    	// we don't want to track the height, because we want to scroll vertically.
        return false;
    }

}
