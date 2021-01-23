package de.lars.remotelightclient.ui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

import org.tinylog.Logger;

import de.lars.remotelightcore.effect.AbstractEffect;

public class GlowButton extends BigTextButton implements ActionListener {
	private static final long serialVersionUID = 1702652196883299731L;
	
	private final Class<? extends AbstractEffect> clazz;
	private AbstractEffect effect;
	private final GlowBorder border;
	private final Timer timer;
	private int renderDelay = 30;
	private boolean isGlowEnabled = true;
	
	public GlowButton(String title, String text, int glowSize, Class<? extends AbstractEffect> effectClass) {
		super(title, text);
		this.clazz = effectClass;
		border = new GlowBorder(this, null, glowSize);
		this.setOpaque(false);
		this.addMouseListener(mouseAdapter);
		timer = new Timer(0, this);
		timer.setDelay(renderDelay);
	}
	
	private MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			if(!isGlowEnabled || effect != null) // return if glow is disabled or effect is already running
				return;
			
			if(getBorder() == null) { // set border if current border is empty
				setBorder(border);
			}
			try { // initialize effect
				effect = clazz.newInstance();
			} catch(Exception ex) {}
			
			if(effect != null) {
				try {
					effect.onEnable(60);
				} catch (Exception ex) {
					Logger.error(ex);
				}
				timer.restart();
			}
		};
		
		@Override
		public void mouseExited(MouseEvent e) {
			if(!isGlowEnabled) return;
			if(getBorder() instanceof GlowBorder) { // check if set border is GlowBorder
				setBorder(null); // hide border
			}
			// disable effect and set it to null causing the timer to stop
			if(effect != null) {
				timer.stop();
				try {
					effect.onDisable();
				} catch(Exception ex) {
					Logger.error(ex);
				} finally {
					effect = null;
				}
			}
		};
	};
	
	/**
	 * Set the delay in milliseconds for the render timer.
	 * @param renderDelay	delay between repainting the border
	 */
	public void setRenderDelay(int renderDelay) {
		this.renderDelay = renderDelay;
		timer.setDelay(renderDelay);
	}
	
	/**
	 * Whether the glowing border effect is enabled or disabled.
	 * @return true if enabled, false otherwise
	 */
	public boolean isGlowEnabled() {
		return isGlowEnabled;
	}
	
	/**
	 * Toggle the glowing border effect.
	 * @param isGlowEnabled	true if the glowing border should be enabled
	 */
	public void setGlowEnabled(boolean isGlowEnabled) {
		this.isGlowEnabled = isGlowEnabled;
		// removed GlowBorder if set to disable
		if(!isGlowEnabled && getBorder() instanceof GlowBorder) { // check if set border is GlowBorder
			setBorder(null); // hide border
		}
	}

	/**
	 * Triggered when the border should be repainted.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(!(getBorder() instanceof GlowBorder))
			return;
		GlowBorder border = (GlowBorder) getBorder();
		if(effect == null || !isDisplayable()) { // stop timer when effect is null or component was removed
			border.updateColor(null);
			effect = null;
			timer.stop();
			return;
		}
		try {
			border.updateColor(effect.onEffect());
		} catch(Exception ex) {
			Logger.error(ex);
		}
	}

}
