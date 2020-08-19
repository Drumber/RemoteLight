/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightclient.ui.components;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;

public class CustomTextPane extends JTextPane {
	private static final long serialVersionUID = 6624135880403860383L;
	
	private boolean autoScroll = true;
	
	public void setAutoScroll(boolean autoScroll) {
		this.autoScroll = autoScroll;
	}
	
	public boolean isAutoScroll() {
		return autoScroll;
	}
	
	@Override
	public boolean getScrollableTracksViewportHeight() {
		if(!isAutoScroll())
			return false;
		return super.getScrollableTracksViewportHeight();
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth() {
		if(!isAutoScroll())
			return false;
		return super.getScrollableTracksViewportWidth();
	}
	
	/**
	 * Insert content without checking if the text pane if editable.
	 * <p> see {@link #replaceSelection(String)}
	 * 
	 * @param content	the content to replace
	 */
	public void replaceSelectionUnchecked(String content) {
        Document doc = getStyledDocument();
        if (doc != null) {
            try {
                Caret caret = getCaret();
                boolean composedTextSaved = saveComposedText(caret.getDot());
                int p0 = Math.min(caret.getDot(), caret.getMark());
                int p1 = Math.max(caret.getDot(), caret.getMark());
                AttributeSet attr = getInputAttributes().copyAttributes();
                if (doc instanceof AbstractDocument) {
                    ((AbstractDocument)doc).replace(p0, p1 - p0, content,attr);
                }
                else {
                    if (p0 != p1) {
                        doc.remove(p0, p1 - p0);
                    }
                    if (content != null && content.length() > 0) {
                        doc.insertString(p0, content, attr);
                    }
                }
                if (composedTextSaved) {
                    restoreComposedText();
                }
            } catch (BadLocationException e) {
                UIManager.getLookAndFeel().provideErrorFeedback(this);
            }
        }
	}

}
