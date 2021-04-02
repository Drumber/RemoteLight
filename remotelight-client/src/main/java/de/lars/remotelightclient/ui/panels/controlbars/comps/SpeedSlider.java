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
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.animation.AnimationManager;
import de.lars.remotelightcore.utils.maths.MathHelper;

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
		slider.setMaximum(AnimationManager.MAX_SPEED);
		slider.setMinimum(AnimationManager.MIN_SPEED);
		bgrSlider.add(slider);
		slider.setAlignmentX(Component.RIGHT_ALIGNMENT);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int speedPercent = Math.round(MathHelper.percentageInRange(slider.getValue(), AnimationManager.MIN_SPEED - 1, AnimationManager.MAX_SPEED));
				lblSpeed.setText(textSpeed + " " + speedPercent + "%");
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
