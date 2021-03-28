package de.lars.remotelightcore.utils.color.palette;

public class PaletteStorage {
	
	public static void onLoad() {
		// TODO: store palettes (perhaps in individual file)
		if(Palettes.getAll().size() == 0) {
			Palettes.restoreDefaults();
		}
	}

}
