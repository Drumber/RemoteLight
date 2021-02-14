package de.lars.remotelightclient.ui.panels.colors.gradients;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import de.lars.remotelightclient.ui.components.GlowBorder;
import de.lars.remotelightclient.utils.ColorTool;
import de.lars.remotelightcore.utils.color.palette.AbstractPalette;
import de.lars.remotelightcore.utils.color.palette.ColorPalette;
import de.lars.remotelightcore.utils.color.palette.GradientPalette;

public class GradientBar extends JPanel {
	private static final long serialVersionUID = -251881323706634078L;
	
	private AbstractPalette colorPalette;
	private int cornerRadius = 0;
	private int paddingHorizontal = 0;
	private int paddingVertical = 0;
	
	public GradientBar(AbstractPalette colorPalette) {
		this.colorPalette = colorPalette;
		setOpaque(false);
	}
	
	public AbstractPalette getColorPalette() {
		return colorPalette;
	}

	public void setColorPalette(AbstractPalette colorPalette) {
		this.colorPalette = colorPalette;
	}

	public int getCornerRadius() {
		return cornerRadius;
	}

	public void setCornerRadius(int cornerRadius) {
		this.cornerRadius = cornerRadius;
	}

	public int getPaddingHorizontal() {
		return paddingHorizontal;
	}

	public void setPaddingHorizontal(int paddingHorizontal) {
		this.paddingHorizontal = paddingHorizontal;
	}

	public int getPaddingVertical() {
		return paddingVertical;
	}

	public void setPaddingVertical(int paddingVertical) {
		this.paddingVertical = paddingVertical;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(colorPalette == null) return;
		Rectangle bounds = getBounds();
		final int width = bounds.width - paddingHorizontal*2;
		final int height = bounds.height - paddingVertical*2;
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
		
		g.drawImage(img, x, y, null);
		g.dispose();
	}
	
}
