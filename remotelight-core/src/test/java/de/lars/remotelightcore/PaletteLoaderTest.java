package de.lars.remotelightcore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import de.lars.remotelightcore.colors.palette.PaletteLoader;
import de.lars.remotelightcore.colors.palette.Palettes;
import de.lars.remotelightcore.utils.DirectoryUtil;

public class PaletteLoaderTest {
	
	@Test
	public void runTest() throws InterruptedException {
		System.out.println("Running palette loader test...");
		final List<Throwable> errorList = new ArrayList<>();
		
		final PaletteLoader loader = PaletteLoader.getInstance();
		loader.load();
		loader.store();
		
		final int prevPaletteSize = Palettes.getAll().size();
		
		final int storeRepeatCount = 10;
		final long waitTime = 5;
		
		final ExecutorService executor = Executors.newFixedThreadPool(storeRepeatCount);
		
		for(int i = 0; i < storeRepeatCount; i++) {
			final int pos = i;
			
			executor.submit(() -> {
				try {
					System.out.printf("[%d/%d] Storing %d palettes to %s...\n",
							pos + 1,
							storeRepeatCount,
							Palettes.getAll().size(),
							DirectoryUtil.getDataStoragePath() + DirectoryUtil.PALETTES_FILE_NAME);
					
					//---- Store
					loader.store();
					
					//---- Clear
					Palettes.getAll().clear();
					assertEquals(0, Palettes.getAll().size());
					
					//---- Load
					loader.load();
					assertEquals(prevPaletteSize, Palettes.getAll().size());
				} catch(Throwable e) {
					errorList.add(e);
					e.printStackTrace();
				}
				
				System.out.printf("[%d] Thread finished", pos + 1);
				return null;
			});
			
			Thread.sleep(waitTime);
		}
		
		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.SECONDS);
		
		if(!errorList.isEmpty()) {
			throw new AssertionFailedError("Expected no error, but test caused " + errorList.size() + " errors.");
		}
	}

}
