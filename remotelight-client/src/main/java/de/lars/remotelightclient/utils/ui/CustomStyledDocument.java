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
	
	private final Map<String, SimpleAttributeSet> expressionsSet;
	
	public CustomStyledDocument() {
		expressionsSet = new LinkedHashMap<String, SimpleAttributeSet>();
		
		// set up attribute sets
		StyleConstants.setForeground(plain, Style.textColor);
		StyleConstants.setItalic(plain, false);
		StyleConstants.setForeground(highlighted, Style.accent);
		StyleConstants.setForeground(brackets, new Color(108, 205, 234));
		StyleConstants.setForeground(value, new Color(195, 202, 89));
		StyleConstants.setForeground(key, new Color(248, 141, 101));
		StyleConstants.setForeground(comment, new Color(185, 233, 136));
		StyleConstants.setItalic(comment, true);
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
		setCharacterAttributes(0, text.length(), plain, false);
		
		for(Entry<String, SimpleAttributeSet> entry : expressionsSet.entrySet()) {
			Pattern pattern = Pattern.compile(entry.getKey(), Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(text);
			while(matcher.find()) {
				setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), entry.getValue(), false);
			}
		}
	}

}
