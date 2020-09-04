package de.lars.remotelightclient.ui.font;

import java.awt.Font;

/**
 * Interface for getting font resource files.
 */
public interface FontResource {
	
	/** font family name */
	public String getName();
	
	/** regular font */
	public Font getRegular();
	/** bold font */
	public Font getBold();
	/** italic font */
	public Font getItalic();

}
