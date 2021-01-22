package de.lars.remotelightclient.ui.components;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;

import com.jhlabs.image.GaussianFilter;

import de.lars.remotelightclient.utils.ColorTool;
import de.lars.remotelightcore.utils.color.Color;

public class GlowBorder extends AbstractBorder {
	private static final long serialVersionUID = 860851399421040415L;

	protected final JComponent component;
	protected int borderSize;
	protected final int thickness = 1; // thickness of the color ring
	protected Color[] lineColor;

	public GlowBorder(JComponent component, Color[] color, int borderSize) {
		this.component = component;
		lineColor = color;
		this.borderSize = borderSize;
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
		if (lineColor != null && this.borderSize > 1 && g instanceof Graphics2D) {
			BufferedImage img = createCompatibleImage(width, height);
			Graphics2D g2d = img.createGraphics();
			java.awt.Color oldColor = g.getColor();
			
			int colorLength = lineColor.length;
			int offset = borderSize - thickness;
			offset += 1;
			
			// calculate the amount of pixels per color
			float pixelWidthRatio = 1.0f * (width) / (width+height - 2*thickness);
			float pixelHeightRatio = 1.0f * (height - 2*thickness) / (width+height - 2*thickness);
			int pixelsWidth = (int) (colorLength * pixelWidthRatio);
			int pixelsHeight = (int) (colorLength * pixelHeightRatio);
			int pixelsWidthHalf = Math.round(pixelsWidth / 2.0f);
			int pixelsHeightHalf = Math.round(pixelsHeight / 2.0f);
			
			java.awt.Color[] colorsTop	  = new java.awt.Color[pixelsWidthHalf];
			java.awt.Color[] colorsRight  = new java.awt.Color[pixelsHeightHalf];
			java.awt.Color[] colorsBottom = new java.awt.Color[pixelsWidthHalf];
			java.awt.Color[] colorsLeft   = new java.awt.Color[pixelsHeightHalf];
			
			float[] fractionsTop 	= new float[pixelsWidthHalf];
			float[] fractionsRight 	= new float[pixelsHeightHalf];
			float[] fractionsBottom = new float[pixelsWidthHalf];
			float[] fractionsLeft 	= new float[pixelsHeightHalf];
			// calculate the fraction step size
			float fractionWidth = 1.0f / (pixelsWidthHalf - 1);
			float fractionHeight = 1.0f / (pixelsHeightHalf - 1);
			
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
				int colorIndex = i + pixelsWidth + pixelsHeightHalf;
				if(colorIndex >= lineColor.length) {
					colorIndex = lineColor.length - 1;
				}
				colorsLeft[i] = ColorTool.convert(lineColor[colorIndex]);
				fractionsLeft[i] = i * fractionHeight;
			}
			
			// create linear gradients for each border side
			LinearGradientPaint top = new LinearGradientPaint(new Point2D.Float(x+offset, y+offset), new Point2D.Float(x + width - offset, y-offset),
					fractionsTop, colorsTop);
			LinearGradientPaint right = new LinearGradientPaint(new Point2D.Float(x + width - offset, y + thickness + offset), new Point2D.Float(x + width - offset, y + height - thickness - offset),
					fractionsRight, colorsRight);
			LinearGradientPaint bottom = new LinearGradientPaint(new Point2D.Float(x + width - offset, y + height - offset), new Point2D.Float(x + offset, y + height - offset),
					fractionsBottom, colorsBottom);
			LinearGradientPaint left = new LinearGradientPaint(new Point2D.Float(x + offset, y + height - thickness - offset), new Point2D.Float(x + offset, y + thickness + offset),
					fractionsLeft, colorsLeft);
			
			drawLinearGradient(g2d, top, new Rectangle(x + offset, y + offset, width - 2*offset, thickness));
			drawLinearGradient(g2d, right, new Rectangle(x + width - thickness - offset, y + thickness + offset, thickness, y + height - thickness - 2*offset));
			drawLinearGradient(g2d, bottom, new Rectangle(x + offset, y + height - thickness - offset, width - 2*offset, thickness));
			drawLinearGradient(g2d, left, new Rectangle(x + offset, y + thickness + offset, thickness, height - thickness - 2*offset));

			// generate blur filter
			BufferedImage mask = generateBlur(img, borderSize+2, 1.0f);
			// add image on top of blur mask
			Graphics2D mg2d = mask.createGraphics();
			mg2d.drawImage(img, x, y, null);
			mg2d.dispose();
			
			// draw everything
			g.drawImage(mask, x, y, null);
			//g.drawImage(img, x, y, null);
			g.setColor(oldColor);
		}
	}
	
	private void drawLinearGradient(Graphics2D g2d, LinearGradientPaint gradient, Rectangle rect) {
		g2d.setPaint(gradient);
		g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
	}
	
	
	/**
	 * Generate a glow effect using a Gaussian filter.
	 * original version from https://stackoverflow.com/a/34124063/12821118
	 */
	public static BufferedImage generateBlur(BufferedImage imgSource, int size, float alpha) {
		GaussianFilter filter = new GaussianFilter(size); // looks best
		//BoxBlurFilter filter2 = new BoxBlurFilter(size, size, 5); // better performance

		int imgWidth = imgSource.getWidth();
		int imgHeight = imgSource.getHeight();

		BufferedImage imgBlur = createCompatibleImage(imgWidth, imgHeight);
		Graphics2D g2 = imgBlur.createGraphics();

		g2.drawImage(imgSource, 0, 0, null);
        g2.dispose();

		imgBlur = filter.filter(imgBlur, null);
		//imgBlur = filter2.filter(imgBlur, null);
		
		// amplify alpha values to achieve more brilliant colors
		for (int cx = 0; cx < imgBlur.getWidth(); cx++) {
			for (int cy = 0; cy < imgBlur.getHeight(); cy++) {
				int rgba = imgBlur.getRGB(cx, cy);
				int alphaVal = (rgba >> 24) & 0xff; // get alpha value from color
				
				if(alphaVal > 0) { // amplify color if alpha is greater then 0
					alphaVal *= 3f;
					alphaVal = Math.min(255, alphaVal); // limit value to 255
					rgba |= ((alphaVal & 0xFF) << 24); // set new alpha value
				}
				imgBlur.setRGB(cx, cy, rgba);
			}
		}

		return imgBlur;
	}

	public static BufferedImage createCompatibleImage(int width, int height) {
		return createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	}

	public static BufferedImage createCompatibleImage(int width, int height, int transparency) {
		BufferedImage image = getGraphicsConfiguration().createCompatibleImage(width, height, transparency);
		image.coerceData(true);
		return image;
	}

	public static GraphicsConfiguration getGraphicsConfiguration() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
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
		insets.set(borderSize, borderSize, borderSize, borderSize);
		return insets;
	}

	/**
	 * Returns the thickness of the border.
	 */
	public int getBorderSize() {
		return borderSize;
	}

	/**
	 * Returns whether or not the border is opaque.
	 */
	public boolean isBorderOpaque() {
		return false;
	}

}
