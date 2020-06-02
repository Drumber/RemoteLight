/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Drumber
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

package de.lars.remotelightclient.ui.comps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.lars.remotelightclient.ui.Style;

public class BigTextButton extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7302225939932211684L;
	private JPanel bgrText;
	private JPanel bgrTitle;
	private JLabel lblText;
	private JLabel lblTitle;
	
	private Color bg = Style.buttonBackground;

	/**
	 * Create the panel.
	 */
	public BigTextButton(String title, String text) {
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(100, 100));
		
		bgrText = new JPanel();
		bgrText.setBackground(Style.buttonBackground);
		add(bgrText, BorderLayout.SOUTH);
		
		lblText = new JLabel(text);
		lblText.setForeground(Style.textColorDarker);
		bgrText.add(lblText);
		
		bgrTitle = new JPanel();
		bgrTitle.setBackground(Style.buttonBackground);
		add(bgrTitle, BorderLayout.CENTER);
		bgrTitle.setLayout(new BorderLayout(0, 0));
		
		lblTitle = new JLabel(title, SwingConstants.CENTER);
		lblTitle.setForeground(Style.textColor);
		lblTitle.setFont(Style.getFontBold(12));
		bgrTitle.add(lblTitle);
		
		addMouseListener(buttonHoverListener);
	}
	
	private MouseAdapter buttonHoverListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			bgrText.setBackground(Style.hoverBackground);
			bgrTitle.setBackground(Style.hoverBackground);
		}
		@Override
		public void mouseExited(MouseEvent e) {
			bgrText.setBackground(bg);
			bgrTitle.setBackground(bg);
		}
	};
	
	
	public void setText(String s) {
		lblText.setText(s);
	}
	
	public String getText() {
		return lblText.getText();
	}
	
	public void setTextColor(Color c) {
		lblText.setForeground(c);
	}
	
	public Color getTextColor() {
		return lblText.getForeground();
	}
	
	public void setTitle(String s) {
		lblTitle.setText(s);
	}
	
	public String getTitle() {
		return lblTitle.getText();
	}
	
	public void setTitleColor(Color c) {
		lblTitle.setForeground(c);
	}
	
	public Color getTitleColor() {
		return lblTitle.getForeground();
	}
	
}
