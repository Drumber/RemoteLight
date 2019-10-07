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
package de.lars.remotelightclient.ui.panels.controlbars.comps;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.UiUtils;

public class SpeedSlider extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6008827176568737547L;
	private String textSpeed = "Speed";
	private Dimension size;
	private JSlider slider;
	private JLabel lblSpeed;

	/**
	 * Create the panel.
	 */
	public SpeedSlider(Color c) {
		setBorder(new EmptyBorder(5, 0, 0, 0));
		setBackground(c);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		size = new Dimension(180, 30);
		
		lblSpeed = new JLabel(textSpeed);
		lblSpeed.setForeground(Style.textColor);
		lblSpeed.setAlignmentX(Component.RIGHT_ALIGNMENT);
		add(lblSpeed);
		
		JPanel bgrSlider = new JPanel();
		bgrSlider.setAlignmentX(Component.RIGHT_ALIGNMENT);
		bgrSlider.setBackground(c);
		bgrSlider.setPreferredSize(size);
		bgrSlider.setMaximumSize(size);
		add(bgrSlider);
		bgrSlider.setLayout(new BoxLayout(bgrSlider, BoxLayout.X_AXIS));
		
		slider = new JSlider();
		slider.setMaximum(200);
		slider.setMinimum(20);
		bgrSlider.add(slider);
		slider.setAlignmentX(Component.RIGHT_ALIGNMENT);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblSpeed.setText(textSpeed + " " + slider.getValue() + "ms");
			}
		});
		slider.setBackground(c);
		slider.setForeground(Style.accent);
		slider.setFocusable(false);
		slider.setMajorTickSpacing(0);
		slider.setPaintTicks(true);
		slider.setPaintTrack(true);
		lblSpeed.setText(textSpeed + " " + slider.getValue() + "ms");
		UiUtils.addSliderMouseWheelListener(slider);
	}
	
	public JSlider getSlider() {
		return slider;
	}
	
	public void setDisplayedValue(String value) {
		lblSpeed.setText(textSpeed + " " + value + "ms");
	}

}
