/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Drumber
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightcore.animation.animations;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class SortAlgo extends Animation {
	
	private int MAX_SHUFFLE_COUNT = 300;
	private int SHUFFLE_PER_LOOP = 15;
	
	private Random random;
	
	private SortAlgorithm[] algorithms = {new BubbleSort(), new SelectionSort(), new InsertionSort(), new BogoSort(), new QuickSort(), new MergeSort(), new RadixSort()};
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
		
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
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
						if(next < algorithms.length && algorithms[next].getName().equals("BogoSort"))
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
	
	
	private class MergeSort implements SortAlgorithm {
		@Override
		public String getName() {
			return "MergeSort";
		}
		
		int curr_size = 1;
		int left_start = 0;
		
		int mergeLoop = -1; // -1 = disabled, 0 = merge, 1 = n1-loop, 2 = n2-loop
		int i, j, k;
		int n1;
		int n2;
		int[] L;
		int[] R;
		
		@Override
		public boolean sort(int[] arr, SortAlgo instance) {
			if(mergeLoop == -1) {
				int mid = Math.min(left_start + curr_size - 1, arr.length-1);
				
				int right_end = Math.min(left_start + 2*curr_size - 1, arr.length-1);
				
				// merge
				mergeLoop = 0;
				
				n1 = mid - left_start + 1;
				n2 = right_end - mid;
				
				L = new int[n1];
				R = new int[n2];
				
				for(i = 0; i < n1; i++)
					L[i] = arr[left_start + i];
				for(j = 0; j < n2; j++)
					R[j] = arr[mid + 1 + j];
				
				i = 0;
				j = 0;
				k = left_start;
				
			} else {
				// merge loop
				if(mergeLoop == 0) {
					if(i < n1 && j < n2) {
						if(L[i] <= R[j]) {
							instance.setSingle(k, L[i]);
							i++;
						} else {
							instance.setSingle(k, R[j]);
							j++;
						}	
						k++;
					} else {
						mergeLoop = 1;
					}
				}
				// n1-loop
				else if(mergeLoop == 1) {
					if(i < n1) {
						instance.setSingle(k, L[i]);
						i++;
						k++;
					} else {
						mergeLoop = 2;
					}
				}
				// n2-loop
				else if (mergeLoop == 2) {
					if(j < n2) {
						instance.setSingle(k, R[j]);
						j++;
						k++;
					} else {
						// merge loop finish
						mergeLoop = -1;
					}
				}
			}
			
			if(mergeLoop == -1) {
				left_start += 2*curr_size;
				if(left_start >= arr.length - 1) {
					curr_size = 2*curr_size;
					left_start = 0;
					if(curr_size > arr.length - 1) {
						//finish
						return true;
					}
				}
			}
			
			return false;
		}

		@Override
		public void reset() {
			curr_size = 1;
			left_start = 0;
			mergeLoop = -1;
		}
	}
	
	
	private class RadixSort implements SortAlgorithm {
		@Override
		public String getName() {
			return "RadixSort";
		}
		
		final int radix = 2; // default would be 10, but it would be too fast
		int largest = -1;
		int exp = 1;
		int i = 0;
		int n = -1;
		int countLoop = -1; // -1 = main, 0 = first, 1 = second, 2 third
		int[] result;
		int[] count;
		
		@Override
		public boolean sort(int[] arr, SortAlgo instance) {
			if(n == -1 || largest == -1) {
				n = arr.length;
				largest = getMax(arr, n);
				exp = 1;
				countLoop = -1;
				result = new int[n];
			}
			
			if(countLoop == -1) {
				// radixsort main loop
				if(largest/exp <= 0) {
					// finish
					return true;
				}
				i = 0;
				count = new int[radix];
				count = countingSort(arr, exp);
				countLoop = 0;
			}
			
			if(countLoop == 0) {
				instance.setSingle(i, result[i] = arr[i]);
				if(++i >= n) {
					// end of loop 0
					i = 1;
					countLoop = 1;
				}
			}
			else if(countLoop == 1) {
				count[i] += count[i - 1];
				if(++i >= radix) {
					// end of loop 1
					i = n - 1;
					countLoop = 2;
				}
			}
			else if(countLoop == 2) {
				instance.setSingle(--count[ (result[i]/exp)%radix ], result[i]);
				if(--i < 0) {
					// end of loop 2
					i = 0;
					exp *= radix;
					countLoop = -1;
				}
			}
			
			return false;
		}
		
		private int getMax(int[] arr, int n) {
			int mx = arr[0];
			for(int i = 1; i < n; i++) {
				if(arr[i] > mx)
					mx = arr[i];
			}
			return mx;
		}
		
		private int[] countingSort(int[] arr, int exp) {
			Arrays.fill(count, 0);
			for(int i = 0; i < arr.length; i++)
				count[ (arr[i]/exp)%radix ]++;
			return count;
		}

		@Override
		public void reset() {
			n = -1;
			largest = -1;
		}
	}
	
}
