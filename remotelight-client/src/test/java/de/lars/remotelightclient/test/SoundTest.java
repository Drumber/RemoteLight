package de.lars.remotelightclient.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import de.lars.remotelightclient.Main;
import de.lars.remotelightcore.musicsync.MusicSyncManager;

public class SoundTest {
	
	private MusicSyncManager msm;
	
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
			int parts = amplitudes.length / width;
			
			for(int i = 0; i < width; i++) {
				int x = barWidth * i;
				
				// bundle amplitudes
				int start = parts * i;
				float[] subArr = Arrays.copyOfRange(amplitudes, start, start+parts+1);
				// get sum of array
				float sum = 0;
				for(float f : subArr)
					sum += f;
				
				// bar height = average of sub array
				int barHeight = (int) (sum / subArr.length * 10.0 * adjustment);
				barHeight = Math.min(height, barHeight);
				int y = height - barHeight;
				g.fillRect(x, y, barWidth, barHeight);
			}
		}
		
	}

}
