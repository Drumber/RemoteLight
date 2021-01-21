package de.lars.remotelightcore.effect;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.Color;

public abstract class AbstractEffect {
	
	private String name;
	private String displayname;
	private int pixel;
	
	public AbstractEffect(String name) {
		this.name = name;
		this.displayname = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	
	/**
	 * Get the amount of LEDs/pixels. (Wrapper method
	 * for {@link RemoteLightCore#getLedNum()})
	 * 
	 * @return	the amount of LEDs
	 */
	protected int getLeds() {
		return RemoteLightCore.getLedNum();
	}
	
	protected int getPixel() {
		if(pixel <= 0)
			return getLeds();
		return pixel;
	}
	
	protected void onEnable() {}
	public void onDisable() {}
	
	/**
	 * @deprecated will be replaced by {@link #onEffect()}
	 */
	@Deprecated
	public void onLoop() {
		Color[] strip = onEffect();
		if(strip != null)
			OutputManager.addToOutput(strip);
	}
	
	public void onEnable(int pixel) {
		this.pixel = pixel;
		this.onEnable();
	}
	
	public Color[] onEffect() {
		return null;
	}

}
