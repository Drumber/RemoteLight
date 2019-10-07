/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient.ui.comps;

import java.awt.Image;

import javax.swing.JPanel;

import de.lars.remotelightclient.ui.Style;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;

public class ImagePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5453343680456337041L;
	private Image img;
	private JLabel lbl;
	private boolean blur;
	private boolean line;
	private int x1, x2, y1, y2; //cordinates for the line
	
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
		lbl.setForeground(Style.accent);
		add(lbl, BorderLayout.CENTER);
		
	}
	
	public JLabel getLabel() {
		return lbl;
	}
	
	public void setImage(Image img) {
		this.img = img;
	}
	
	public void setBlur(boolean active) {
		blur = active;
	}
	
	public boolean isBlurred() {
		return blur;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(this.resize(img, getWidth(), getHeight()), 0, 0, null);
		
		if(blur) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		if(line) {
			g.setColor(new Color(255, 0, 0, 180));
			g.fillRect(x1, y1, x2, y2);
			//System.out.println(x1 + "/" + y1 + " ; " + x2 + "/" + y2);
		}
	}
	
	private Image resize(Image im, int width, int height) {
		return im.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}
	
	public void enableLine(int x1, int y1, int x2, int y2) {
		line = true;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public void disableLine() {
		line = false;
		repaint();
	}

}
