package de.lars.remotelightcore;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonIOException;

import de.lars.remotelightcore.io.FileStorage;

public class FileStoreTest {
	
	@DisplayName("Data storing test")
	@Test
	public void runTest() {
		System.out.println("Running Data Storing Test");
		long startTime = System.currentTimeMillis();
		
		ArrayList<String> arrayStr = getStringArray(2*1000);
		ArrayList<Integer> arrayInt = getIntegerArray(2*1000);
		
		File file = new File("FileStoreTest_out.json");
		final FileStorage fs1 = new FileStorage(file);
		final String storeKey = "File_Store_Test";
		
		// store test arrays
		fs1.store(storeKey + "_String", arrayStr);
		fs1.store(storeKey + "_Int", arrayInt);
		
		// save to file
		//assertDoesNotThrow(() -> fs1.save());
		try {
			fs1.save();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
		
		// create new FileStorage instance
		final FileStorage fs2 = new FileStorage(file);
		
		// load from file
		assertDoesNotThrow(() -> fs2.load());
		
		// load and compare string array
		Object loadedDataStr = fs2.get(storeKey + "_String");
		assertTrue(loadedDataStr instanceof ArrayList);
		@SuppressWarnings("unchecked")
		ArrayList<String> loadedArrayStr = (ArrayList<String>) loadedDataStr;
		assertArrayEquals(arrayStr.toArray(new String[0]), loadedArrayStr.toArray(new String[0]));
		
		// load, convert and compare integer array
		Object loadedDataInt = fs2.get(storeKey + "_Int");
		assertTrue(loadedDataInt instanceof ArrayList);
		@SuppressWarnings("unchecked")
		ArrayList<Double> loadedArrayDouble = (ArrayList<Double>) loadedDataInt;
		int[] prevArrayInt = arrayInt.stream().mapToInt(i -> i).toArray();
		int[] loadedArrayInt = loadedArrayDouble.stream().mapToInt(i -> i.intValue()).toArray();
		assertArrayEquals(prevArrayInt, loadedArrayInt);
		
		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Time elapsed: " + duration + " ms");
		
		// delete file
		file.deleteOnExit();
	}
	
	
	private ArrayList<String> getStringArray(int size) {
		String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz" + ";.-_#'*+~`?{}[]\"\\§$%&";
		int stringLength = 300;
		ArrayList<String> array = new ArrayList<String>();
		
		for(int a = 0; a < size; a++) {
			StringBuilder sb = new StringBuilder();
			
			// generate random string
			for(int i = 0; i < stringLength; i++) {
				int index = (int) (alphaNumericString.length() * Math.random());
				sb.append(alphaNumericString.charAt(index));
			}
			
			// add to array
			array.add(sb.toString());
		}
		return array;
	}
	
	private ArrayList<Integer> getIntegerArray(int size) {
		Random ran = new Random();
		
		ArrayList<Integer> array = new ArrayList<Integer>();
		
		for(int a = 0; a < size; a++) {
			// generate random number
			int num = ran.nextInt();
			// add to array
			array.add(num);
		}
		return array;
	}

}
