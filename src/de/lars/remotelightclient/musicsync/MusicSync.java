package de.lars.remotelightclient.musicsync;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.tarosdsp.PitchDetector;
import de.lars.remotelightclient.musicsync.ws281x.Bump;
import de.lars.remotelightclient.musicsync.ws281x.EQ;
import de.lars.remotelightclient.musicsync.ws281x.LevelBar;
import de.lars.remotelightclient.musicsync.ws281x.Rainbow;
import de.lars.remotelightclient.musicsync.ws281x.RunningLight;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

public class MusicSync {
	
	private static boolean guiOpen;
	private static JFrame frame;
	
	public MusicSync() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (Exception e) {
						//ignore failure to set default look en feel;
					}
					frame = new PitchDetector();
					frame.pack();
					frame.dispose();
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void openGUI() {
		if(!frame.isVisible()) {
			frame.setVisible(true);
			guiOpen = true;
		}
	}
	
	public static void closeGUI() {
		if(guiOpen) {
			frame.dispose();
			guiOpen = false;
		}
	}
	
	
	
	/* =================
	 *
	 *  Visualization
	 *  
	 * =================
	 */
	private static boolean loop;
	private final static int DELAY = 80;
	private static boolean bump = false;
	private static double volume, lastVolume, maxVolume = 2.0,
						spl, maxSpl, minSpl, lastMaxSpl, lastMinSpl, avgBump, sensitivity = 1;
	private static float pitch;
	private static Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.WHITE, Color.PINK};
	private static int color = 0;
	private static String animation = "FADE";
	private static double pitchTime;
	
	private static int noInfoCounter, sameMinSplCounter, sameMaxSplCounter;
	private static boolean noSoundInfo;
	
	
	public static void soundToLight(float pitch, double rms, double time) {
		volume = rms;
		MusicSync.pitch = pitch;
		pitchTime = time;
	}
	
	public static void setSensitivity(double sensitivity) {
		MusicSync.sensitivity = sensitivity;
	}
	
	public static double getSensitivity() {
		return sensitivity;
	}
	
	public static void setAnimation(String animation) {
		MusicSync.animation = animation;
	}
	
	public static String getAnimation() {
		return animation;
	}
	
	public static boolean isLoopActive() {
		return loop;
	}
	
	//loop
	public static void startLoop() {
		if(!loop) {
			loop = true;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(loop) {
						spl = PitchDetector.getCurrentSPL();
						//increase minSpl / decrease maxSpl a little bit if the song has only a short quiet/loud part
						if(lastMinSpl == minSpl) {
							if(sameMinSplCounter <= 800) sameMinSplCounter++;
							else
								if((minSpl + 1) < maxSpl) minSpl = minSpl + 1;
						} else
							sameMinSplCounter = 0;
						if(lastMaxSpl == maxSpl) {
							if(sameMaxSplCounter <= 1400) sameMaxSplCounter++;
							else
								if((maxSpl - 1) > minSpl) maxSpl = maxSpl - 1;
						} else
							sameMaxSplCounter = 0;
						
						if(minSpl == 0) {
							minSpl = spl;
							sameMinSplCounter = 0;
						}
						if(spl < minSpl) {
							minSpl = spl;
							sameMinSplCounter = 0;
						}
						if(spl > maxSpl) {
							maxSpl = spl;
							sameMaxSplCounter = 0;
						}
						
						lastMinSpl = minSpl;
						lastMaxSpl = maxSpl;
						
						if(volume > maxVolume) maxVolume = volume;
						if(volume < 0.02) volume = 0; //no sound = silent
						
						if(volume - lastVolume > sensitivity * 2) avgBump = (avgBump + (volume - lastVolume)) / 2.0; //if there is a big change in volume
						bump = (volume - lastVolume > avgBump * .9); //trigger a bump
						
						lastVolume = volume;
						
						/*
						 * no sound info detection
						 */
						if(spl == 0) {
							if(noInfoCounter <= 15) noInfoCounter++;
							else { //time is over
								noSoundInfo = true;
								spl = 0;
								maxSpl = 0;
							}
						} else {
							noSoundInfo = false;
							noInfoCounter = 0;
						}
						
						/*
						 * MusicSync Animations
						 */
						switch (MusicSync.animation.toUpperCase()) {
						case "FADE":
							fade();
							break;
						case "PULSE":
							pulse();
							break;
							
						case "LEVELBAR":
							LevelBar.levelBar(bump);
							break;
						case "RAINBOW":
							Rainbow.rainbow(bump);
							break;
						case "RUNNINGLIGHT":
							RunningLight.runningLight(pitch, pitchTime, volume);
							break;
						case "BUMP":
							Bump.bump(bump);
							break;
						case "EQ":
							EQ.eq();
							break;

						default:
							fade();
							break;
						}
						
						try {
							Thread.sleep(DELAY);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			}).start();
		}
	}
	
	public static void stopLoop() {
		loop = false;
	}
	
	private static Color dimColor(Color color, int dimValue) {
		int r = color.getRed() - dimValue;
		int g = color.getGreen() - dimValue;
		int b = color.getBlue() - dimValue;
		if(r < 0) r = 0;
		if(g < 0) g = 0;
		if(b < 0) b = 0;
		return new Color(r, g, b);
	}
	
	private static Color changeBrightness(Color color, int brightness) {
		int diff = (255 - brightness);
		int r = color.getRed() - diff;
		int g = color.getGreen() - diff;
		int b = color.getBlue() - diff;
		//System.out.println(r + " " + g + " " + b);
		if(r < 0) r = 0;
		if(g < 0) g = 0;
		if(b < 0) b = 0;
		if(r > 255) r = 255;
		if(g > 255) g = 255;
		if(b > 255) b = 255;
		
		return new Color(r, g, b);
	}
	
	// Fade Effect
	private static Color fadeLastColor = colors[0];
	private static void fade() {
		if(bump) {
			System.out.println("bump");
			if(color < colors.length - 1) {
				color++;
			}
			else color = 0;
			fadeLastColor = colors[color];
		}
		Main.getRgbGUI().setMusicSyncColorPanel(fadeLastColor, fadeLastColor);
		Client.send(new String[] {Identifier.COLOR_COLOR, fadeLastColor.getRed()+"", fadeLastColor.getGreen()+"", fadeLastColor.getBlue()+""});
		
		if((fadeLastColor.getRed() != 0) || (fadeLastColor.getGreen() != 0) || (fadeLastColor.getBlue() != 0)) fadeLastColor = dimColor(fadeLastColor, 2);
		else {
			if(color < colors.length - 1) {
				color++;
			}
			else color = 0;
			fadeLastColor = colors[color];
		}
	}
	
	// Pulse effect
	private static int pulseLastHz;
	private static Color[] deepColors = {Color.RED, Color.BLUE, new Color(255, 0, 120), new Color(180, 50, 50), new Color(0, 20, 200)};
	private static Color[] highColors = {Color.GREEN, Color.CYAN, Color.YELLOW, Color.PINK, Color.MAGENTA};
	private static Color pulseColor = Color.GREEN;
	
	private static void pulse() {
		int max = (int) (maxSpl * 10.);
		int min = (int) (minSpl * 10.);
		int spl = (int) (MusicSync.spl * 10.);
		int pulseBrightness = 0;
		
		if(maxSpl == 0)
			pulseBrightness = 10;
		else if(MusicSync.spl == maxSpl) {
			pulseBrightness = 255;
		} else {
			try {
				int span = max - min;
				int m = span / (max - spl);
				pulseBrightness = 255 - (255 / m);
				if(pulseBrightness < 10)
					pulseBrightness = 10;
			} catch(ArithmeticException e) {
				pulseBrightness = 200;
			}
		}
		
		//System.out.println("SPL: " + MusicSync.spl + " Min/Max: " + minSpl + "/" + maxSpl + " bright: " + pulseBrightness);
		
		int hz = (int) pitch;
		if(hz < pulseLastHz) { //deeper sound
			if((pulseLastHz - hz) > 200) { //if difference between last loop is bigger
				int r = new Random().nextInt(deepColors.length - 1);
				pulseColor = deepColors[r];
			}
		} else if(hz > pulseLastHz) { //higher sound
			if((pulseLastHz - hz) > 200) { //if difference between last loop is bigger
				int r = new Random().nextInt(highColors.length - 1);
				pulseColor = highColors[r];
			}
		}
		pulseLastHz = hz;
		Color c = changeBrightness(pulseColor, pulseBrightness);
		Main.getRgbGUI().setMusicSyncColorPanel(c, c);
		Client.send(new String[] {Identifier.COLOR_COLOR, c.getRed()+"", c.getGreen()+"", c.getBlue()+""});
	}
	

}
