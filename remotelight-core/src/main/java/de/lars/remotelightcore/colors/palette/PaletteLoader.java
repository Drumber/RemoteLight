package de.lars.remotelightcore.colors.palette;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.lars.remotelightcore.colors.palette.model.AbstractPalette;
import de.lars.remotelightcore.colors.palette.model.ColorPalette;
import de.lars.remotelightcore.colors.palette.model.EvenGradientPalette;
import de.lars.remotelightcore.colors.palette.model.GradientPalette;
import de.lars.remotelightcore.colors.palette.model.PaletteData;
import de.lars.remotelightcore.utils.DirectoryUtil;

public class PaletteLoader {
	
	private static PaletteLoader instance;
	
	public final static String ID_COLOR_PALETTE = "color_palette";
	public final static String ID_EVEN_GRADIENT_PALETTE = "even_gradient_palette";
	public final static String ID_GRADIENT_PALETTE = "gradient_palette";
	
	private final Gson gson;
	private final File dataFile;
	private final Type paletteListType;
	protected final Map<Type, String> paletteTypes;
	
	/**
	 * Singleton for accessing the palette loader.
	 * @return	new or existing stance of the {@link PaletteLoader}
	 */
	public static PaletteLoader getInstance() {
		if(instance == null) {
			instance = new PaletteLoader();
			PaletteLoader.registerDefaultPaletteTypes(instance);
		}
		return instance;
	}
	
	public static void registerDefaultPaletteTypes(PaletteLoader loader) {
		loader.registerPaletteType(ColorPalette.class, ID_COLOR_PALETTE);
		loader.registerPaletteType(EvenGradientPalette.class, ID_EVEN_GRADIENT_PALETTE);
		loader.registerPaletteType(GradientPalette.class, ID_GRADIENT_PALETTE);
	}
	
	public PaletteLoader() {
		dataFile = new File(DirectoryUtil.getDataStoragePath() + DirectoryUtil.PALETTES_FILE_NAME);
		paletteListType = new TypeToken<List<PaletteData>>() {}.getType();
		paletteTypes = new HashMap<Type, String>();
		gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(PaletteData.class, new PaletteSerializer(this))
				.create();
	}
	
	public synchronized void load() {
		List<PaletteData> palettes = null;
		
		if(dataFile.exists()) {
			try(Reader reader = Files.newBufferedReader(dataFile.toPath())) {
				palettes = gson.fromJson(reader, paletteListType);
			} catch (IOException e) {
				Logger.error(e, "Error while loading color palettes from " + dataFile.getAbsolutePath());
			}
		}
		

		if(palettes != null && !palettes.isEmpty()) {
			Logger.info("Loaded " + palettes.size() + " color palettes.");
			palettes.forEach(p -> {
				if(p != null) {
					Palettes.addPalette(p);
				}
			});
		}
		if(Palettes.getAll().isEmpty()) {
			Palettes.restoreDefaults();
		}
	}
	
	public synchronized void store() {
		List<PaletteData> palettes = new ArrayList<PaletteData>(Palettes.getAll());
		Logger.info("Storing " + palettes.size() + " color palettes.");
		
		try(Writer writer = Files.newBufferedWriter(dataFile.toPath())) {
			gson.toJson(palettes, paletteListType, writer);
			writer.flush();
		} catch (IOException e) {
			Logger.error(e, "Error while storing color palettes to " + dataFile.getAbsolutePath());
		}
	}
	
	
	public void registerPaletteType(Class<? extends AbstractPalette> paletteClass, String uniqueId) {
		paletteTypes.put(paletteClass, uniqueId);
	}
	
	public void unregisterPaletteType(Class<? extends AbstractPalette> paletteClass) {
		paletteTypes.remove(paletteClass);
	}

}
