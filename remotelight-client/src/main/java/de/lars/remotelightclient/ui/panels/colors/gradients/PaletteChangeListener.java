package de.lars.remotelightclient.ui.panels.colors.gradients;

import de.lars.remotelightcore.colors.palette.model.PaletteData;

public interface PaletteChangeListener {
	
	public void onPaletteChange(PaletteData eventPalette, boolean nameOnly);

}
