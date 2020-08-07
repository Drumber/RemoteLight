package de.lars.remotelightclient.ui.console;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class ConsoleDocumentFilter extends DocumentFilter {
	
	private boolean disableEdit = true;
	
	public void setDisableEdit(boolean disable) {
		disableEdit = disable;
	}
	
	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		super.remove(fb, offset, length);
	}
	
	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
		super.replace(fb, offset, length, text, attrs);
	}

}
