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
	
	private SortAlgorithm[] algorithms = {new BubbleSort(), new SelectionSort(), new InsertionSort(), new BogoSort(), new QuickSort()};
	private SortAlgorithm currentAlgorithm;
	
	private Color[] strip;
	private int[] values;
	private ArrayList<Integer> markedIndexes;
	
	private boolean shuffleStrip = false;
	private int shuffleCount = 0;
	
	private boolean finishAnimation = false;
	private int finishAnimationCount = 0;

	
	public SortAlgo() {
		super("SortAlgo");
		
		String[] algoNames = new String[algorithms.length];
		for(int i = 0; i < algoNames.length; i++) {
			algoNames[i] = algorithms[i].getName();
		}
		this.addSetting(new SettingSelection("animation.sortalgo.algorithms", "Sort Algorithm", SettingCategory.Intern, null, algoNames, algoNames[0], SettingSelection.Model.ComboBox));
		this.addSetting(new SettingBoolean("animation.sortalgo.circle", "Circle through algorithms", SettingCategory.Intern, null, false));
		this.addSetting(new SettingBoolean("animation.sortalgo.marker", "Show Marker", SettingCategory.Intern, "Show marker for currently swapped/set color", true));
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
		if(currentAlgorithm != null)
			currentAlgorithm.reset();
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
		for(int t = 0; t < 4; t++) {
		
			if(shuffleStrip) {
				if(finishAnimation) {
					markedIndexes.add(finishAnimationCount);
					if(++finishAnimationCount >= strip.length) {
						finishAnimationCount = 0;
						finishAnimation = false;
						markedIndexes.clear();
					}
					
				} else {
					for(int s = 0; s < SHUFFLE_PER_LOOP; s++) {
						int ranPosA = random.nextInt(strip.length);
						int ranPosB = random.nextInt(strip.length);
						
						swap(ranPosA, ranPosB, false);
						
						shuffleCount++;
						shuffleStrip = shuffleCount <= MAX_SHUFFLE_COUNT;
						
						if(!shuffleStrip) break;
					}
				}
			} else {
				// clear marked indexes
				markedIndexes.clear();
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
		
		boolean showMarker = ((SettingBoolean)getSetting("animation.sortalgo.marker")).getValue();
		if(showMarker) {
			for(int index : markedIndexes) {
				out[index] = Color.WHITE;
			}
		}
		OutputManager.addToOutput(out);
	}	
	
	
	/**
	 * call sorting algorithm
	 */
	private void doSort() {
		String algo = (((SettingSelection) getSetting("animation.sortalgo.algorithms"))).getSelected();
		boolean circle = (((SettingBoolean) getSetting("animation.sortalgo.circle"))).getValue();
		
		if(currentAlgorithm == null || (!circle && !currentAlgorithm.getName().equals(algo))) {
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
						
						// exclude BogoSort because it could take too long :D
						if(algorithms[i].getName().equals("BogoSort"))
							next++;
						
						if(next >= algorithms.length)
							next = 0;
						currentAlgorithm = algorithms[next];
						break;
					}
				}
			}
			
			// reset
			reset();
			// enable finish animation
			finishAnimationCount = 0;
			finishAnimation = true;
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
			markedIndexes.add(indexA);
			markedIndexes.add(indexB);
		}
	}
	
	
	private void setSingle(int index, int value) {
		setSingle(index, value, true);
	}
	
	private void setSingle(int index, int value, boolean mark) {
		values[index] = value;
		strip[index] = getColorFromValue(value);
		
		if(mark) {
			markedIndexes.add(index);
		}
	}
	
	private Color getColorFromValue(int value) {
		float hue = 1.0f / strip.length * value;
		return Color.getHSBColor(hue, 1f, 1f);
	}
	
	
	/*======================*
	 * 						*
	 *	SORTING ALGORITHMS	*
	 * 						*
	 *======================*/
	// implementation of algorithms
	// from https://www.geeksforgeeks.org/sorting-algorithms/
	
	private interface SortAlgorithm {
		String getName();
		boolean sort(int[] arr, SortAlgo instance);
		void reset();
	}
	
	
	private class BubbleSort implements SortAlgorithm {
		@Override
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
	
	
	private class SelectionSort implements SortAlgorithm {
		@Override
		public String getName() {
			return "SelectionSort";
		}
		
		int i = 0;
		int minIndex = i;
		int j = i+1;

		@Override
		public boolean sort(int[] arr, SortAlgo instance) {
			
			if(arr[j] < arr[minIndex]) {
				minIndex = j;
			}
			
			if(++j >= arr.length) {
				j = i+1;
				
				// swap min with first
				instance.swap(minIndex, i);
				
				if(++i >= arr.length-1) {
					//finish
					return true;
				}
				minIndex = i;
			}
			return false;
		}

		@Override
		public void reset() {
			i = 0;
			minIndex = i;
			j = i+1;
		}
	}
	
	
	private class InsertionSort implements SortAlgorithm {
		@Override
		public String getName() {
			return "InsertionSort";
		}
		
		int i = 1;
		int key = -1;
		int j = i-1;

		@Override
		public boolean sort(int[] arr, SortAlgo instance) {
			if(key == -1)
				key = arr[i];
			
			instance.setSingle(j + 1, arr[j]);
			j = j - 1; 
			
			if(j < 0 || arr[j] <= key) {
				instance.setSingle(j + 1, key);
				if(++i >= arr.length) {
					//finish
					return true;
				}
				
				key = arr[i];
				j = i - 1;
			}
			
			return false;
		}

		@Override
		public void reset() {
			i = 1;
			key = -1;
			j = i-1;
		}
	}
	
	
	private class BogoSort implements SortAlgorithm {
		@Override
		public String getName() {
			return "BogoSort";
		}
		
		int i = 1;

		@Override
		public boolean sort(int[] arr, SortAlgo instance) {
			
			if(isSorted(arr)) {
				//finish
				return true;
			}
			
			instance.swap(i, (int)(Math.random()*i));
			
			if(++i >= arr.length)
				i = 1;
			
			return false;
		}
		
		private boolean isSorted(int[] arr) {
			for(int i = 1; i < arr.length; i++) {
				if(arr[i] < arr[i-1])
					return false;
			}
			return true;
		}

		@Override
		public void reset() {
			i = 1;
		}
	}
	
	
	private class QuickSort implements SortAlgorithm {
		@Override
		public String getName() {
			return "QuickSort";
		}
		
		int low = 0;
		int high = -1;
		int i = low - 1;
		int j = low;
		int[] stack;
		int top = -1;
		int pivot = -1;
		int p = -1;
		int loopMode = 0; // 0 = before partition; 1 == partition-loop; 2 == after partition

		@Override
		public boolean sort(int[] arr, SortAlgo instance) {
			if(stack == null) {
				loopMode = 0;
				
				high = arr.length - 1;
				stack = new int[high - low - 1];
				
				top = -1;
				
				stack[++top] = low;
				stack[++top] = high;
			}
			
			if(loopMode == 0) {
				high = stack[top--];
				low = stack[top--];
				
				loopMode = 1;
				pivot = arr[high];
				i = low - 1;
				j = low;
			}
			
			//partition
			if(loopMode == 1) {
				if(j <= high - 1) {
					if(arr[j] < pivot) {
						i++;
						
						instance.swap(i, j);
					}
					j++;
				} else {
					instance.swap(i+1, high);
					// return i + 1
					p = i + 1;
					loopMode = 2;
				}
			}
			
			if(loopMode == 2) {
				if(p - 1 > low) {
					stack[++top] = low;
					stack[++top] = p - 1;
				}
				
				if(p + 1 < high) {
					stack[++top] = p + 1;
					stack[++top] = high;
				}
				loopMode = 0;
				
				if(top < 0) {
					//finish
					return true;
				}
			}
			
			return false;
		}

		@Override
		public void reset() {
			loopMode = 0;
			stack = null;
			low = 0;
			high = -1;
			i = low - 1;
			j = low;
			top = -1;
			pivot = -1;
			p = -1;
		}
	}
	
}
