package de.lars.remotelightclient.utils.ui;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import de.lars.remotelightclient.ui.Style;

public class CustomStyledDocument extends DefaultStyledDocument {
	private static final long serialVersionUID = -3971617022514528180L;
	
	public final SimpleAttributeSet plain = new SimpleAttributeSet();
	public final SimpleAttributeSet highlighted = new SimpleAttributeSet();
	public final SimpleAttributeSet brackets = new SimpleAttributeSet();
	public final SimpleAttributeSet value = new SimpleAttributeSet();
	public final SimpleAttributeSet key = new SimpleAttributeSet();
	public final SimpleAttributeSet comment = new SimpleAttributeSet();
	public final SimpleAttributeSet error = new SimpleAttributeSet();
	
	private final Map<String, SimpleAttributeSet> expressionsSet;
	private int[][] arrErrorIndices;
	
	public CustomStyledDocument() {
		expressionsSet = new LinkedHashMap<String, SimpleAttributeSet>();
		
		boolean dark = !Style.isBlackIcons();
		
		// set up attribute sets
		StyleConstants.setForeground(plain, Style.textColor);
		StyleConstants.setItalic(plain, false);
		StyleConstants.setBold(plain, false);
		StyleConstants.setUnderline(plain, false);
		StyleConstants.setForeground(highlighted, Style.accent);
		StyleConstants.setForeground(brackets, dark ? new Color(108, 205, 234) : new Color(134, 214, 250));
		StyleConstants.setForeground(value, dark ? new Color(195, 202, 89) : new Color(5, 134, 91));
		StyleConstants.setForeground(key, dark ? new Color(248, 141, 101) : new Color(163, 23, 25));
		StyleConstants.setForeground(comment, dark ? new Color(185, 233, 136) : new Color(0, 128, 28));
		StyleConstants.setItalic(comment, true);
		StyleConstants.setUnderline(error, true);
		StyleConstants.setForeground(error, Style.error);
	}
	
	public CustomStyledDocument addRegEx(String expression, SimpleAttributeSet attr) {
		expressionsSet.put(expression, attr);
		return this;
	}
	
	public CustomStyledDocument removeRegEx(String expression) {
		if(expressionsSet.containsKey(expression))
			expressionsSet.remove(expression);
		return this;
	}
	
	/**
	 * Set the indices of the character that to which the error style
	 * should be applied.
	 * @param arrErrorIndecies		2 dimensional array: <code>[[start, end], ... ]</code>
	 * 								or set to {@code null} to clear/disable
	 */
	public void setErrorIndices(int[][] arrErrorIndices) {
		this.arrErrorIndices = arrErrorIndices;
	}
	
	public void addErrorIndices(int start, int end) {
		if(this.arrErrorIndices == null || arrErrorIndices.length == 0) {
			this.arrErrorIndices = new int[][] { {start, end} };
		} else {
			int[][] tmp = new int[arrErrorIndices.length][2];
			for(int i = 0; i < arrErrorIndices.length - 1; i++) {
				tmp[i] = new int[] {arrErrorIndices[i][0], arrErrorIndices[i][1]};
			}
			tmp[tmp.length - 1] = new int[] {start, end};
			this.arrErrorIndices = tmp;
		}
	}
	
	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		super.insertString(offs, str, a);
		
		String text = getText(0, getLength());
		updateHighlighting(text);
	}
	
	@Override
	public void remove(int offs, int len) throws BadLocationException {
		super.remove(offs, len);
		
		String text = getText(0, getLength());
		updateHighlighting(text);
	}
	
	protected void updateHighlighting(String text) {
		// reset the formatting for every characters
		setCharacterAttributes(0, text.length(), plain, true);
		
		for(Entry<String, SimpleAttributeSet> entry : expressionsSet.entrySet()) {
			Pattern pattern = Pattern.compile(entry.getKey(), Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(text);
			while(matcher.find()) {
				setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), entry.getValue(), false);
			}
		}
		
		// apply error styles
		if(arrErrorIndices != null && arrErrorIndices.length > 0) {
			for(int i = 0; i < arrErrorIndices.length; i++) {
				if(arrErrorIndices[i].length == 2) {
					int start = arrErrorIndices[i][0];
					int end = arrErrorIndices[i][1];
					setCharacterAttributes(start, end - start, error, false);
				}
			}
		}
	}

}
