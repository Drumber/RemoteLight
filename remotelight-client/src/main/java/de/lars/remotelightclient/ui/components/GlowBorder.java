package de.lars.remotelightclient.ui.components;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;

import de.lars.remotelightclient.utils.ColorTool;
import de.lars.remotelightcore.utils.color.Color;

public class GlowBorder extends AbstractBorder {
	private static final long serialVersionUID = 860851399421040415L;

	protected final JComponent component;
	protected int thickness;
	protected Color[] lineColor;

	public GlowBorder(JComponent component, Color[] color, int thickness) {
		this.component = component;
		lineColor = color;
		this.thickness = thickness;
	}

	/**
	 * Paints the border for the specified component with the specified position and
	 * size.
	 * 
	 * @param c      the component for which this border is being painted
	 * @param g      the paint graphics
	 * @param x      the x position of the painted border
	 * @param y      the y position of the painted border
	 * @param width  the width of the painted border
	 * @param height the height of the painted border
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		if (lineColor != null && this.thickness > 0 && g instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) g;
			java.awt.Color oldColor = g2d.getColor();
			
			int colorLength = lineColor.length;
			
			// calculate the amount of pixels per color
			float pixelWidthRatio = 1.0f * width / (width+height - 2*thickness);
			float pixelHeightRatio = 1.0f * (height - 2*thickness) / (width+height - 2*thickness);
			int pixelsWidth = (int) (colorLength * pixelWidthRatio);
			int pixelsHeight = (int) (colorLength * pixelHeightRatio);
			int pixelsWidthHalf = pixelsWidth / 2;
			int pixelsHeightHalf = pixelsHeight / 2;
			
			java.awt.Color[] colorsTop	  = new java.awt.Color[pixelsWidthHalf];
			java.awt.Color[] colorsRight  = new java.awt.Color[pixelsHeightHalf];
			java.awt.Color[] colorsBottom = new java.awt.Color[pixelsWidthHalf];
			java.awt.Color[] colorsLeft   = new java.awt.Color[pixelsHeightHalf];
			
			float[] fractionsTop 	= new float[pixelsWidthHalf];
			float[] fractionsRight 	= new float[pixelsHeightHalf];
			float[] fractionsBottom = new float[pixelsWidthHalf];
			float[] fractionsLeft 	= new float[pixelsHeightHalf];
			// calculate the fraction step size
			float fractionWidth = 1.0f / (pixelsWidth / 2.0f - 1);
			float fractionHeight = 1.0f / (pixelsHeight / 2.0f - 1);
			
			// fill color and fraction arrays for each border side
			for(int i = 0; i < colorsTop.length; i++) {
				colorsTop[i] = ColorTool.convert(lineColor[i]);
				fractionsTop[i] = i * fractionWidth;
			}
			for(int i = 0; i < colorsRight.length; i++) {
				colorsRight[i] = ColorTool.convert(lineColor[i + pixelsWidthHalf]);
				fractionsRight[i] = i * fractionHeight;
			}
			for(int i = 0; i < colorsBottom.length; i++) {
				colorsBottom[i] = ColorTool.convert(lineColor[i + pixelsWidthHalf + pixelsHeightHalf]);
				fractionsBottom[i] = i * fractionWidth;
			}
			for(int i = 0; i < colorsLeft.length; i++) {
				colorsLeft[i] = ColorTool.convert(lineColor[i + pixelsWidth + pixelsHeightHalf]);
				fractionsLeft[i] = i * fractionHeight;
			}
			
			// create linear gradients for each border side
			LinearGradientPaint top = new LinearGradientPaint(new Point2D.Float(x, y), new Point2D.Float(x + width, y),
					fractionsTop, colorsTop);
			LinearGradientPaint right = new LinearGradientPaint(new Point2D.Float(x + width, y + thickness), new Point2D.Float(x + width, y + height - thickness),
					fractionsRight, colorsRight);
			LinearGradientPaint bottom = new LinearGradientPaint(new Point2D.Float(x + width, y + height), new Point2D.Float(x, y + height),
					fractionsBottom, colorsBottom);
			LinearGradientPaint left = new LinearGradientPaint(new Point2D.Float(x, y + height - thickness), new Point2D.Float(x, y - thickness),
					fractionsLeft, colorsLeft);
			
			drawLinearGradient(g2d, top, new Rectangle(x, y, width, thickness));
			drawLinearGradient(g2d, right, new Rectangle(x + width - thickness, y + thickness, x + width, y + height - thickness));
			drawLinearGradient(g2d, bottom, new Rectangle(x, y + height - thickness, width, thickness));
			drawLinearGradient(g2d, left, new Rectangle(x, y + thickness, thickness, height - thickness));

			g2d.setColor(oldColor);
		}
	}
	
	private void drawLinearGradient(Graphics2D g2d, LinearGradientPaint gradient, Rectangle rect) {
		g2d.setPaint(gradient);
		g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * Update the border color and repaints the component.
	 * 
	 * @param color	new color array
	 */
	public void updateColor(Color[] color) {
		this.lineColor = color;
		if(component != null)
			component.repaint();
	}

	/**
	 * Reinitialize the insets parameter with this Border's current Insets.
	 * 
	 * @param c      the component for which this border insets value applies
	 * @param insets the object to be reinitialized
	 */
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.set(thickness, thickness, thickness, thickness);
		return insets;
	}

	/**
	 * Returns the thickness of the border.
	 */
	public int getThickness() {
		return thickness;
	}

	/**
	 * Returns whether or not the border is opaque.
	 */
	public boolean isBorderOpaque() {
		return false;
	}

}
