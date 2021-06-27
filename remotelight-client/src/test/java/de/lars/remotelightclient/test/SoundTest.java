package de.lars.remotelightclient.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import de.lars.remotelightclient.Main;
import de.lars.remotelightcore.musicsync.MusicSyncManager;
import de.lars.remotelightcore.out.OutputManager;

public class SoundTest {
	
	private MusicSyncManager msm;
	private OutputManager om;
	
	private final int bars = 60;
	
	// window size
	private final int width = 600;
	private final int height = 255;
	
	public static void main(String[] args) {
		new SoundTest();
	}
	
	public SoundTest() {
		Main main = new Main(null, true);
		msm = main.getCore().getMusicSyncManager();
		om = main.getCore().getOutputManager();
		
		Dimension size = new Dimension(width, height);
		VisualizerPanel visPanel = new VisualizerPanel();
		visPanel.setPreferredSize(size);
		visPanel.setMinimumSize(size);
		visPanel.setMaximumSize(size);
		
		JFrame frame = new JFrame("Sound Test");
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.setContentPane(visPanel);
		frame.pack();
		frame.setVisible(true);
		
		// draw every 35 milliseconds (= 1000/35 = 29 FPS)
		Timer timer = new Timer(35, e -> visPanel.repaint());
		timer.start();
	}
	
	
	private class VisualizerPanel extends JPanel {
		private static final long serialVersionUID = 8816979826805886919L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			//-----------------------
			// draw led output
			//-----------------------
			drawLedOutput((Graphics2D) g, om.getLastColors());
			
			if(msm.getSoundProcessor() == null || !msm.getSoundProcessor().isActive())
				return;
			
			//-----------------------
			// get sound data & adjustment
			//-----------------------
			double adjustment = msm.getAdjustment();
			float[] amplitudes = msm.getSoundProcessor().getAmplitudes();
			int[] processed = msm.getSoundProcessor().computeFFT(amplitudes, bars, adjustment);
			
			//-----------------------
			// draw raw data
			//-----------------------
			g.setColor(Color.black);
			drawRawVisualizer(g, amplitudes, adjustment);
			
			//-----------------------
			// draw processed data
			//-----------------------
			boolean enableProcessedViz = false;
			if(enableProcessedViz) {
				g.setColor(Color.red);
				int barWidth = width / bars;
				
				for(int i = 0; i < bars; i++) {
					int x = barWidth * i;
					int barHeight = processed[i];
					barHeight = Math.min(height, barHeight);
					int y = height - barHeight;
					g.drawRect(x, y, barWidth, barHeight);
				}
			}
			
			//-----------------------
			// draw smoothed data
			//-----------------------
			int smoothValuesAmount = 20;
			float[] smoothed = new float[amplitudes.length];
			
			for(int i = 0; i < smoothed.length; i++) {
				int startIndex = Math.max(0, i - (smoothValuesAmount - 1));
				int endIndex = Math.min(smoothed.length-1, i + (smoothValuesAmount - 1));
				int currValuesAmount = endIndex - startIndex + 1;
				float sum = 0.0f;
				for(int j = 0; j < currValuesAmount; j++) {
					int elementIndex = startIndex + j;
					sum += amplitudes[elementIndex];
				}
				float avg = sum / currValuesAmount;
				// apply filter (reduce low frequencies)
				// this uses an exponential function: -e^(-a*(x+10))+1
				// make a bigger to allow more low frequencies
				float a = -0.01f;
				double volPercent = -Math.exp(a * (i + 10)) + 1;
				smoothed[i] = (float) (volPercent * avg);
			}
			
			g.setColor(Color.GREEN);
			drawRawVisualizer(g, smoothed, adjustment);
			
			//-----------------------
			// draw average line
			//-----------------------
			double totalAvg = IntStream.range(0, smoothed.length)
					.mapToDouble(i -> smoothed[i])
					.average().orElse(0);
			
			int avgY = (int) (totalAvg * 10.0 * adjustment);
			avgY = Math.min(height, avgY);
			avgY = height - avgY; // invert/start at the bottom of the window
			g.setColor(Color.RED);
			g.drawLine(0, avgY, width, avgY);
			
			g.dispose();
		}
		
		void drawRawVisualizer(Graphics g, float[] amplitudes, double adjustment) {
			int barWidth = 1;
			int bars = width;
			int parts = amplitudes.length / width;
			
			if(amplitudes.length < width) {
				barWidth = width / amplitudes.length;
				bars = amplitudes.length;
			}
			
			for(int i = 0; i < bars; i++) {
				int x = barWidth * i;
				float loudness = 0.0f;
				
				// bundle amplitudes
				if(amplitudes.length >= width) {
					int start = parts * i;
					float[] subArr = Arrays.copyOfRange(amplitudes, start, start+parts+1);
					// get sum of array
					float sum = 0;
					for(float f : subArr)
						sum += f;
					// loudness = average of sub array
					loudness = sum / subArr.length;
				} else {
					loudness = amplitudes[i];
				}
				
				// bar height = average of sub array
				int barHeight = (int) (loudness * 10.0 * adjustment);
				barHeight = Math.min(height, barHeight);
				int y = height - barHeight;
				g.fillRect(x, y, barWidth, barHeight);
			}
		}
		
		void drawLedOutput(Graphics2D g, de.lars.remotelightcore.utils.color.Color[] strip) {
			int length = strip.length;
			float fraction = width / length;
			int[] x = new int[length];
			int[] yRed = new int[length];
			int[] yGreen = new int[length];
			int[] yBlue = new int[length];
			
			for(int i = 0; i < strip.length; i++) {
				x[i] = (int) Math.floor(fraction * i);
				yRed[i] = height - height / 255 * strip[i].getRed();
				yGreen[i] = height - height / 255 * strip[i].getGreen();
				yBlue[i] = height - height / 255 * strip[i].getBlue();
			}
			Stroke oldStroke = g.getStroke();
			g.setStroke(new BasicStroke(2));
			g.setColor(Color.RED);
			g.drawPolyline(x, yRed, length);
			g.setColor(Color.GREEN);
			g.drawPolyline(x, yGreen, length);
			g.setColor(Color.BLUE);
			g.drawPolyline(x, yBlue, length);
			g.setStroke(oldStroke);
		}
		
	}

}
