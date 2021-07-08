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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.UiUtils;

public class ImagePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5453343680456337041L;
	private Image img;
	private JLabel lbl;
	private boolean gray;
	private boolean line;
	private int x, w, y, h; // rectangle bounds
	
	public ImagePanel(Image img, Dimension size) {
		this(img, "", size);
	}

	public ImagePanel(Image img, String text, Dimension size) {
		this.img = img;
		setPreferredSize(size);
		setSize(size);
		setMinimumSize(size);
		setBackground(Color.BLACK);
		setLayout(new BorderLayout(0, 0));
		
		lbl = new JLabel(text);
		lbl.setHorizontalAlignment(JLabel.CENTER);
		lbl.setFont(Style.getFontBold(14));
		UiUtils.bindForeground(lbl, Style.accent());
		add(lbl, BorderLayout.CENTER);
		
	}
	
	public JLabel getLabel() {
		return lbl;
	}
	
	public void setImage(Image img) {
		this.img = img;
	}
	
	/**
	 * Enable or disable semi transparent overlay
	 * @param active
	 */
	public void setBlur(boolean active) {
		gray = active;
	}
	
	public boolean isBlurred() {
		return gray;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(this.resize(img, getWidth(), getHeight()), 0, 0, null);
		
		if(gray) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		if(line) {
			g.setColor(new Color(255, 0, 0, 180));
			g.fillRect(x, y, w, h);
			//System.out.println(x1 + "/" + y1 + " ; " + x2 + "/" + y2);
		}
	}
	
	private Image resize(Image im, int width, int height) {
		return im.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}
	
	public void enableLine(int x, int y, int width, int height) {
		line = true;
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
	}
	
	public void disableLine() {
		line = false;
		repaint();
	}

}
