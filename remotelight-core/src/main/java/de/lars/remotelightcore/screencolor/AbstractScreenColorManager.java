package de.lars.remotelightcore.screencolor;

import de.lars.remotelightcore.EffectManager;

/**
 * An abstract ScreenColorManager implementation.
 */
public abstract class AbstractScreenColorManager extends EffectManager {
	
	@Override
	public String getName() {
		return "ScreenColorManager";
	}
	
	public abstract void start();
	
	public abstract void stop();

}
