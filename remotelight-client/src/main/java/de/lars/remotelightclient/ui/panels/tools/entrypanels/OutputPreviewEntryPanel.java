package de.lars.remotelightclient.ui.panels.tools.entrypanels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanelEntry;
import de.lars.remotelightcore.out.OutputManager;

public class OutputPreviewEntryPanel extends ToolsPanelEntry {
	
	/**
	 * TODO:
	 * right-click menu with options:
	 * - always on top
	 * - black background
	 * - FPS presets (15, 30, 60, 120)
	 */
	
	private OutputPreviewFrame frame;

	@Override
	public String getName() {
		return "Output Preview";
	}

	@Override
	public void onClick() {
		if(frame == null) {
			frame = new OutputPreviewFrame();
		}
		frame.setVisible(!frame.isVisible());
	}
	
	
	private class OutputPreviewFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		private OutputManager om;
		
		public OutputPreviewFrame() {
			om = Main.getInstance().getCore().getOutputManager();
			this.setTitle("Output Preview");
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setSize(600, 300);
			this.setMinimumSize(new Dimension(200, 100));
			Panel panel = new Panel();
			this.setContentPane(panel);
			
			// draw every 35 milliseconds (= 1000/35 = 29 FPS)
			Timer timer = new Timer(35, e -> {
				if(isVisible()) {
					panel.repaint();
				}
			});
			timer.start();
		}
		
		private class Panel extends JPanel {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				de.lars.remotelightcore.utils.color.Color[] strip = om.getLastColors();
				if(strip == null || strip.length < 1) return;
				
				int width = this.getBounds().width;
				int height = this.getBounds().height;
				
				float fraction = 1.0f * width / strip.length;
				int length = strip.length;
				if(fraction <= 0) {
					length = width;
				}
				
				int[] x = new int[length];
				int[] yRed = new int[length];
				int[] yGreen = new int[length];
				int[] yBlue = new int[length];
				
				for(int i = 0; i < length; i++) {
					x[i] = (int) Math.floor(fraction * i);
					yRed[i] = (int) (height - height / 255.0f * strip[i].getRed());
					yGreen[i] = (int) (height - height / 255.0f * strip[i].getGreen());
					yBlue[i] = (int) (height - height / 255.0f * strip[i].getBlue());
				}
				
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.RED);
				g2.drawPolyline(x, yRed, length);
				g2.setColor(Color.GREEN);
				g2.drawPolyline(x, yGreen, length);
				g2.setColor(Color.BLUE);
				g2.drawPolyline(x, yBlue, length);
				
				g2.dispose();
			}
		}
		
	}
	
}
