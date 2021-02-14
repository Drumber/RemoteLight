package de.lars.remotelightclient.ui.panels.colors.gradients;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.GlowBorder;
import de.lars.remotelightclient.utils.ColorTool;
import de.lars.remotelightcore.utils.color.palette.AbstractPalette;
import de.lars.remotelightcore.utils.color.palette.ColorPalette;
import de.lars.remotelightcore.utils.color.palette.GradientPalette;
import de.lars.remotelightcore.utils.maths.MathHelper;

public class GradientBar extends JPanel {
	private static final long serialVersionUID = -251881323706634078L;
	
	private AbstractPalette colorPalette;
	private int cornerRadius = 0;
	private int paddingHorizontal = 0;
	private int paddingVertical = 0;
	
	private BufferedImage gradientCache;
	private boolean shouldRenderGradient = true;
	
	private MarkerListener markerListener;
	private List<Marker> listMarkers;
	private boolean showMarkers = false;
	
	public GradientBar(AbstractPalette colorPalette) {
		this.colorPalette = colorPalette;
		listMarkers = new ArrayList<GradientBar.Marker>();
		setOpaque(false);
		
		addMouseListener(markerMouseAdapter);
		addMouseMotionListener(markerMouseAdapter);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				shouldRenderGradient = true;
				super.componentResized(e);
			}
		});
	}
	
	public AbstractPalette getColorPalette() {
		return colorPalette;
	}

	public void setColorPalette(AbstractPalette colorPalette) {
		this.colorPalette = colorPalette;
		shouldRenderGradient = true;
	}

	public int getCornerRadius() {
		return cornerRadius;
	}

	public void setCornerRadius(int cornerRadius) {
		this.cornerRadius = cornerRadius;
		shouldRenderGradient = true;
	}

	public int getPaddingHorizontal() {
		return paddingHorizontal;
	}

	public void setPaddingHorizontal(int paddingHorizontal) {
		this.paddingHorizontal = paddingHorizontal;
		shouldRenderGradient = true;
	}

	public int getPaddingVertical() {
		return paddingVertical;
	}

	public void setPaddingVertical(int paddingVertical) {
		this.paddingVertical = paddingVertical;
		shouldRenderGradient = true;
	}

	public boolean isShowMarkers() {
		return showMarkers;
	}
	
	public void setMarkerListener(MarkerListener l) {
		this.markerListener = l;
	}
	
	public void setSelectedMarker(int index) {
		Marker.selectedIndex = index;
	}
	
	public int getSelectedMarkerIndex() {
		return Marker.selectedIndex;
	}
	
	public void resetMarkerSelection() {
		Marker.selectedIndex = -1;
	}

	/**
	 * Show selection markers,
	 * this will automatically set vertical padding to {@code 10}.
	 * @param showMarkers
	 */
	public void setShowMarkers(boolean showMarkers) {
		this.showMarkers = showMarkers;
		if(paddingVertical < 10)
			paddingVertical = 10;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(colorPalette == null) return;
		final int x = paddingHorizontal;
		final int y = paddingVertical;
		
		float[] fractions = new float[colorPalette.size()];
		Color[] colors = new Color[colorPalette.size()];
		
		if(colorPalette instanceof GradientPalette) {
			GradientPalette palette = (GradientPalette) colorPalette;
			// convert position and color ArrayList
			for(int i = 0; i < palette.size(); i++) {
				fractions[i] = palette.getPositions().get(i);
				colors[i] = ColorTool.convert(palette.getColors().get(i));
			}
		} else if(colorPalette instanceof ColorPalette) {
			ColorPalette palette = (ColorPalette) colorPalette;
			for(int i = 0; i< palette.size(); i++) {
				fractions[i] = 1.0f / (palette.size() - 1) * i;
				colors[i] = ColorTool.convert(palette.get(i));
			}
		} else {
			throw new IllegalStateException("Color palette '" + colorPalette.getClass().getSimpleName() + "' is not supported.");
		}
		
		if(shouldRenderGradient || gradientCache == null) {
			shouldRenderGradient = false;
			gradientCache = renderColorGradient(g, fractions, colors);
		}
		g.drawImage(gradientCache, x, y, null);
		
		if(showMarkers) {
			renderMarkers(g, fractions, colors);
		}
		
		g.dispose();
	}
	
	private BufferedImage renderColorGradient(Graphics g, float[] fractions, Color[] colors) {
		Rectangle bounds = getBounds();
		final int width = bounds.width - paddingHorizontal*2;
		final int height = bounds.height - paddingVertical*2;
		
		BufferedImage img = GlowBorder.createCompatibleImage(width, height);
		Graphics2D g2 = img.createGraphics();
		
		// draw rounded corners
		if(cornerRadius > 0) {
			g2.setComposite(AlphaComposite.Clear);
			g2.fillRect(0, 0, width, height);
			
			g2.setComposite(AlphaComposite.Src);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.WHITE);
			g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
			g2.setComposite(AlphaComposite.SrcAtop);
		}
		
		LinearGradientPaint gradientPaint = new LinearGradientPaint(0, 0, width, 0, fractions, colors);
		g2.setPaint(gradientPaint);
		g2.fillRect(0, 0, width, height);
		g2.dispose();
		return img;
	}
	
	private void renderMarkers(Graphics g, float[] fractions, Color[] colors) {
		Rectangle bounds = getBounds();
		final int width = bounds.width - paddingHorizontal*2;
		final int height = bounds.height - paddingVertical*2;
		final int offsetX = paddingHorizontal;
		final int offsetY = paddingVertical;
		
		if(listMarkers.size() != fractions.length)
			listMarkers.clear();
		
		for(int i = 0; i < fractions.length; i++) {
			float fraction = fractions[i];
			float pos = MathHelper.map(fraction, 0.0f, 1.0f, offsetX, offsetX + width);
			
			if(i >= listMarkers.size()) {
				listMarkers.add(new Marker(i, pos));
			} else {
				Marker marker = listMarkers.get(i);
				marker.index = i;
				marker.position = pos;
			}
			
			int center = Math.round(pos);
			g.setColor(Marker.selectedIndex == i ? Style.accent : Color.WHITE);
			g.fillRect(center-1, offsetY, 3, height);
		}
	}
	
	private MouseAdapter markerMouseAdapter = new MouseAdapter() {
		private int pressedMarker = -1;
		
		public void mousePressed(MouseEvent e) {
			Marker marker = getMarkerAtPoint(e.getPoint());
			if(marker != null) {
				System.out.println("selected: " + marker);
				Marker.selectedIndex = marker.index;
				pressedMarker = marker.index;
				if(markerListener != null) {
					markerListener.onMarkerSelected(marker.index);
				}
				repaint();
			}
		};
		
		public void mouseReleased(MouseEvent e) {
			pressedMarker = -1;
			setCursor(null);
		};
		
		public void mouseMoved(MouseEvent e) {
			if(getMarkerAtPoint(e.getPoint()) != null) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			} else {
				setCursor(null);
			}
		};
		
		public void mouseDragged(MouseEvent e) {
			Marker marker = null;
			if(pressedMarker != -1  && (marker = getMarkerAtIndex(pressedMarker)) != null) {
				setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				System.out.println("dragged marker: " + marker);
				
				float newFraction = changeMarkerPosition(marker, e.getX());
				
				if(markerListener != null) {
					markerListener.onMarkerDragged(marker.index, newFraction);
				}
			}
		};
	};
	
	
	private float changeMarkerPosition(Marker marker, int x) {
		Rectangle bounds = getBounds();
		final int width = bounds.width - paddingHorizontal*2;
		final int offsetX = paddingHorizontal;
		
		x = Math.max(x, offsetX);
		x = Math.min(x, offsetX + width);
		
		float fraction = MathHelper.map(x, offsetX, offsetX + width, 0.0f, 1.0f);
		System.out.println("new fraction: " + fraction);
		// TODO: handle gradient marker repositioning
		
		return fraction;
	}
	
	protected Marker getMarkerAtPoint(Point p) {
		Marker marker = null;
		for(Marker m : listMarkers) {
			float distance = Math.abs(m.position - p.x);
			if(distance < 3.5f) {
				if(marker != null) { // check which marker is closer
					float distance2 = Math.abs(marker.position - p.x);
					if(distance > distance2) { // continue loop when current distance is larger
						continue;
					}
				}
				marker = m;
			}
		}
		return marker;
	}
	
	protected Marker getMarkerAtIndex(int index) {
		for(Marker m : listMarkers) {
			if(m.index == index)
				return m;
		}
		return null;
	}
	
	
	private static class Marker {
		static int selectedIndex = -1;
		int index;
		float position;
		public Marker(int index, float position) {
			super();
			this.index = index;
			this.position = position;
		}
		@Override
		public String toString() {
			return "Marker [index=" + index + ", position=" + position + "]";
		}
	}
	
}
