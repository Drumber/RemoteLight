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

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.lars.remotelightclient.ui.Style;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

public class BigImageButton extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7302225939932211684L;
	private JPanel bgrText;
	private JPanel bgrImage;
	private JLabel lblText;
	private JLabel lblImage;
	
	private Color bg = Style.buttonBackground;

	/**
	 * Create the panel.
	 */
	public BigImageButton(ImageIcon image, String text) {
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(100, 100));
		
		bgrText = new JPanel();
		bgrText.setBackground(Style.buttonBackground);
		add(bgrText, BorderLayout.SOUTH);
		
		lblText = new JLabel(text);
		lblText.setForeground(Style.textColor);
		bgrText.add(lblText);
		
		bgrImage = new JPanel();
		bgrImage.setBackground(Style.buttonBackground);
		add(bgrImage, BorderLayout.CENTER);
		bgrImage.setLayout(new BorderLayout(0, 0));
		
		lblImage = new JLabel("", SwingConstants.CENTER);
		lblImage.setIcon(image);
		bgrImage.add(lblImage);
		
		addMouseListener(buttonHoverListener);
	}
	
	private MouseAdapter buttonHoverListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			bgrText.setBackground(Style.hoverBackground);
			bgrImage.setBackground(Style.hoverBackground);
		}
		@Override
		public void mouseExited(MouseEvent e) {
			bgrText.setBackground(bg);
			bgrImage.setBackground(bg);
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
	
	public void setImage(ImageIcon i) {
		lblImage.setIcon(i);
	}
	

}
