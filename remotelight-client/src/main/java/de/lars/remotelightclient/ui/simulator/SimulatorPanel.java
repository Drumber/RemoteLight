package de.lars.remotelightclient.ui.simulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

public class SimulatorPanel extends JPanel implements ActionListener {
	
	private final Timer timer;
	private Color[] data;
	private boolean needsRepaint = true;
	
	private int ledWidth;
	private int spacing;
	
	public SimulatorPanel(int delay, int ledWidth, int spacing) {
		this.ledWidth = ledWidth;
		this.spacing = spacing;
		timer = new Timer(delay, this);
		timer.start();
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				needsRepaint = true;
			}
		});
	}
	
	public void setDelay(int delay) {
		timer.setDelay(delay);
	}
	
	public void setLedWidth(int ledWidth) {
		this.ledWidth = ledWidth;
		needsRepaint = true;
	}
	
	public void setSpacing(int spacing) {
		this.spacing = spacing;
		needsRepaint = true;
	}
	
	public void pushData(Color[] data) {
		this.data = data;
		needsRepaint = true;
	}
	
	private boolean shouldRepaint() {
		return data != null && needsRepaint;
	}
	
	/**
	 * Render loop receiver.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(shouldRepaint()) {
			this.repaint();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if(data == null) return;
		
		final int lWidth = ledWidth;
		final int spacing = this.spacing;
		final Color[] leds = data.clone();
		
		// panel size
		final int pWidth = getParent().getWidth();
		final int pHeight = 2*spacing + lWidth;
		
		int y = spacing;
		int x = spacing;
		for(int i = 0; i < data.length; i++) {
			drawLed(g2, x, y, lWidth, leds[i]);
			
			x += lWidth + spacing;
			if(x + lWidth + 1 >= pWidth) {
				// use next row
				x = spacing;
				y += lWidth + spacing;
			}
		}
		
		// make panel bigger if current height is too small
		int usedHeight = y + spacing + lWidth;
		if(usedHeight > pHeight) {
			this.setPreferredSize(new Dimension(pWidth, usedHeight));
			revalidate();
		}
		
		needsRepaint = false;
		g2.dispose();
	}
	
	private void drawLed(Graphics2D g2, int x, int y, int width, Color color) {
		g2.setColor(color);
		g2.fillRect(x, y, width, width);
	}
	
}
