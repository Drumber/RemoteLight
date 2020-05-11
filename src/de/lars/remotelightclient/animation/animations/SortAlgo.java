package de.lars.remotelightclient.animation.animations;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.utils.color.PixelColorUtils;

public class SortAlgo extends Animation {
	
	private int MAX_SHUFFLE_COUNT = 300;
	private int SHUFFLE_PER_LOOP = 15;
	
	private Random random;
	
	private SortAlgorithm[] algorithms = {new BubbleSort()};
	private SortAlgorithm currentAlgorithm;
	
	private Color[] strip;
	private int[] values;
	private ArrayList<Integer> markedIndexes;
	
	private boolean shuffleStrip = false;
	private int shuffleCount = 0;

	
	public SortAlgo() {
		super("SortAlgo");
		
		String[] algoNames = new String[algorithms.length];
		for(int i = 0; i < algoNames.length; i++) {
			algoNames[i] = algorithms[i].getName();
		}
		this.addSetting(new SettingSelection("animation.sortalgo.algorithms", "Sort Algorithm", SettingCategory.Intern, null, algoNames, algoNames[0], SettingSelection.Model.ComboBox));
		this.addSetting(new SettingBoolean("animation.sortalgo.circle", "Circle through algorithms", SettingCategory.Intern, null, false));
	}
	
	@Override
	public void onEnable() {
		random = new Random();
		
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum());
		values = new int[strip.length];
		markedIndexes = new ArrayList<>();
		
		MAX_SHUFFLE_COUNT = strip.length * 5;
		SHUFFLE_PER_LOOP = MAX_SHUFFLE_COUNT / 20;
		
		reset();
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		shuffleCount = 0;
		shuffleStrip = false;
		super.onDisable();
	}
	
	
	/**
	 * shuffle
	 */
	private void reset() {
		shuffleCount = 0;
		float stepSize = 1.0f / strip.length;
		for(int i = 0; i < strip.length; i++) {
			float hue = stepSize * i;
			strip[i] = Color.getHSBColor(hue, 1f, 1f);
			values[i] = i;
		}
		markedIndexes.clear();
		shuffleStrip = true;
	}
	
	
	@Override
	public void onLoop() {
		// double speed
		for(int t = 0; t < 2; t++) {
		
			if(shuffleStrip) {
				for(int s = 0; s < SHUFFLE_PER_LOOP; s++) {
					int ranPosA = random.nextInt(strip.length);
					int ranPosB = random.nextInt(strip.length);
					
					swap(ranPosA, ranPosB, false);
					
					shuffleCount++;
					shuffleStrip = shuffleCount <= MAX_SHUFFLE_COUNT;
					
					if(!shuffleStrip) break;
				}
			} else {
				// sort strip
				doSort();
			}
			
			show();
		
		}
		super.onLoop();
	}

	
	/**
	 * show the strip
	 */
	private void show() {
		Color[] out = Arrays.copyOf(strip, strip.length);
		for(int index : markedIndexes) {
			out[index] = Color.WHITE;
		}
		OutputManager.addToOutput(out);
		// clear marked indexes
		markedIndexes.clear();
	}	
	
	
	/**
	 * call sorting algorithm
	 */
	private void doSort() {
		String algo = (((SettingSelection) getSetting("animation.sortalgo.algorithms"))).getSelected();
		boolean circle = (((SettingBoolean) getSetting("animation.sortalgo.circle"))).getValue();
		
		if(!circle && (currentAlgorithm == null || !currentAlgorithm.getName().equals(algo))) {
			// switch algorithm
			if(currentAlgorithm != null) currentAlgorithm.reset();
			for(int i = 0; i < algorithms.length; i++) {
				if(algorithms[i].getName().equals(algo)) {
					currentAlgorithm = algorithms[i];
				}
			}
		}
		
		// call current sorting algorithm
		boolean sorted = currentAlgorithm.sort(values, this);
		
		if(sorted) {
			// reset algorithm
			currentAlgorithm.reset();
			
			if(circle) {
				// select next algorithm in list
				for(int i = 0; i < algorithms.length; i++) {
					if(algorithms[i].getName().equals(currentAlgorithm.getName())) {
						int next = i + 1;
						if(next >= algorithms.length)
							next = 0;
						currentAlgorithm = algorithms[next];
						break;
					}
				}
			}
			
			// reset
			reset();
		}
	}
	
	
	private void swap(int indexA, int indexB) {
		swap(indexA, indexB, true);
	}
	
	private void swap(int indexA, int indexB, boolean mark) {
		int tempVal = values[indexA];
		Color tempCol = strip[indexA];
		
		values[indexA] = values[indexB];
		strip[indexA] = strip[indexB];
		
		values[indexB] = tempVal;
		strip[indexB] = tempCol;
		
		if(mark) {
			//markedIndexes.add(indexA);
			//markedIndexes.add(indexB);
		}
	}
	
	
	/*======================*
	 * 						*
	 *	SORTING ALGORITHMS	*
	 * 						*
	 *======================*/
	
	private interface SortAlgorithm {
		String getName();
		boolean sort(int[] arr, SortAlgo instance);
		void reset();
	}
	
	
	private static class BubbleSort implements SortAlgorithm {
		public String getName() {
			return "BubbleSort";
		}
		
		int i = 0;
		int j = 0;
		
		@Override
		public boolean sort(int[] arr, SortAlgo instance) {
			if(arr[j] > arr[j+1]) {
				instance.swap(j, j+1);
			}
			
			if(++j >= arr.length-i-1) {
				j = 0;
				if(++i >= arr.length-1) {
					//finish
					return true;
				}
			}
			return false;
		}

		@Override
		public void reset() {
			i = 0;
			j = 0;
		}
	}
	
	
	
}
