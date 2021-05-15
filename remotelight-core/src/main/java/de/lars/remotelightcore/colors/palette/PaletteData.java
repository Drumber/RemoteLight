package de.lars.remotelightcore.colors.palette;

public class PaletteData {
	
	private String name;
	private AbstractPalette palette;
	
	public PaletteData(String name, AbstractPalette palette) {
		this.name = name;
		this.palette = palette;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AbstractPalette getPalette() {
		return palette;
	}

	public void setPalette(AbstractPalette palette) {
		this.palette = palette;
	}

}
