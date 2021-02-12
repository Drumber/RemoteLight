package de.lars.remotelightclient.ui.components;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 * Adds basic touch scrolling to the {@link JScrollPane}.
 */
public class TScrollPane extends JScrollPane {
	private static final long serialVersionUID = -5425784975725070086L;
	
	private static boolean defaultEnableDragScrolling = true;
	private static boolean defaultInvertScrolling = true;
	private static float scrollSpeed = 0.1f;
	
	private boolean invertScrolling = defaultInvertScrolling;

	public TScrollPane() {
		this(null);
	}
	
	public TScrollPane(Component view) {
		super(view);
		getViewport().addMouseListener(mouseAdapter);
		getViewport().addMouseMotionListener(mouseAdapter);
	}
	
	public void setScrollingInverted(boolean inverted) {
		this.invertScrolling = inverted;
	}
	
	public boolean isScrollingInverted() {
		return invertScrolling;
	}
	
	public static void setDefaultDragScrolling(boolean enabled) {
		defaultEnableDragScrolling = enabled;
	}
	
	public static boolean isDefaultEnableDragScrolling() {
		return defaultEnableDragScrolling;
	}
	
	public static void setDefaultScrollingInverted(boolean inverted) {
		defaultInvertScrolling = inverted;
	}
	
	public static boolean isDefaultScrollingInverted() {
		return defaultInvertScrolling;
	}
	
	protected MouseAdapter mouseAdapter = new MouseAdapter() {
		private Point origin;
		
		@Override
		public void mousePressed(MouseEvent e) {
			origin = new Point(e.getPoint());
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if(origin == null || defaultEnableDragScrolling == false) return;
			JViewport viewPort = TScrollPane.this.getViewport();
			if(viewPort != null) {
				int deltaX = origin.x - e.getX();
				int deltaY = origin.y - e.getY();
				if(!invertScrolling) {
					deltaX = e.getX() - origin.x;
					deltaY = e.getY() - origin.y;
				}
				
				Rectangle view = viewPort.getViewRect();
				
				view.x = (int) (deltaX * scrollSpeed);
				view.y = (int) (deltaY * scrollSpeed);
				viewPort.scrollRectToVisible(view);
			}
		}
	};
	
}
