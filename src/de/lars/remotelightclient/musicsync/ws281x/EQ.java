//package de.lars.remotelightclient.musicsync.ws281x;
//
//import java.awt.Color;
//import java.util.HashMap;
//
//import de.lars.remotelightclient.Main;
//import de.lars.remotelightclient.musicsync.MusicSyncOld;
//import de.lars.remotelightclient.musicsync.sound.SoundProcessing;
//import de.lars.remotelightclient.network.Client;
//
//public class EQ {
//	
//	public static void eq(SoundProcessing soundProcessor) {
//		int pix = Main.getLedNum();
//		int half = pix / 2;
//		int pixBand = half / 6; // pixels per frequency at each side(left/right) (6 bands)
//		double mul = 0.318 * MusicSyncOld.getSensitivity(); // multiplier for brightness
//		HashMap<Integer, Color> pixelHash = new HashMap<>();
//		int[] amp = soundProcessor.getAmplitudes();
//
//		// half 1 (left)
//
//		// low1
//		int cl1 = (int) (amp[0] * mul); // cl1 = color low1
//		if (cl1 > 255)
//			cl1 = 255;
//		for (int p = 0; p < pixBand; p++) {
//			pixelHash.put(half - 1 - p, new Color(cl1, 0, 0));
//		}
//		// low2
//		int cl2 = (int) (amp[1] * mul);
//		if (cl2 > 255)
//			cl2 = 255;
//		for (int p = 0; p < pixBand; p++) {
//			pixelHash.put(half - 1 - pixBand - p, new Color(0, cl2, 0));
//		}
//		// mid1
//		int cm1 = (int) (amp[2] * mul);
//		if (cm1 > 255)
//			cm1 = 255;
//		for (int p = 0; p < pixBand; p++) {
//			pixelHash.put(half - 1 - 2 * pixBand - p, new Color(0, 0, cm1));
//		}
//		// mid2
//		int cm2 = (int) (amp[3] * mul);
//		if (cm2 > 255)
//			cm2 = 255;
//		for (int p = 0; p < pixBand; p++) {
//			pixelHash.put(half - 1 - 3 * pixBand - p, new Color(0, cm2, 0));
//		}
//		// high1
//		int ch1 = (int) (amp[4] * mul);
//		if (ch1 > 255)
//			ch1 = 255;
//		for (int p = 0; p < pixBand; p++) {
//			pixelHash.put(half - 1 - 4 * pixBand - p, new Color(ch1, 0, 0));
//		}
//		// mid2
//		int ch2 = (int) (amp[5] * mul);
//		if (ch2 > 255)
//			ch2 = 255;
//		for (int p = 0; p < pixBand; p++) {
//			pixelHash.put(half - 1 - 5 * pixBand - p, new Color(0, 0, ch2));
//		}
//
//		
//		/*
//		 * half 2 (right)
//		 */
//		// low1
//		for (int p = 0; p < pixBand; p++) {
//			pixelHash.put(half + p, new Color(cl1, 0, 0));
//		}
//		// low2
//		for (int p = 0; p < pixBand; p++) {
//			pixelHash.put(half + pixBand + p, new Color(0, cl2, 0));
//		}
//		// mid1
//		for (int p = 0; p < pixBand; p++) {
//			pixelHash.put(half + 2 * pixBand + p, new Color(0, 0, cm1));
//		}
//		// mid2
//		for (int p = 0; p < pixBand; p++) {
//			pixelHash.put(half + 3 * pixBand + p, new Color(0, cm2, 0));
//		}
//		// high1
//		for (int p = 0; p < pixBand; p++) {
//			pixelHash.put(half + 4 * pixBand + p, new Color(ch1, 0, 0));
//		}
//		// high2
//		for (int p = 0; p < pixBand; p++) {
//			pixelHash.put(half + 5 * pixBand + p, new Color(0, 0, ch2));
//		}
//
//		Client.sendWS281xList(pixelHash);
//	}
//
//}
