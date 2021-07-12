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

package de.lars.remotelightclient.ui.simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.frames.BasicFrame;
import de.lars.remotelightclient.ui.simulator.RLServerSimulator.ConnectionStateChangeListener;
import de.lars.remotelightclient.ui.simulator.RLServerSimulator.PixelReceiver;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.lang.i18n;

public class SimulatorFrame extends BasicFrame implements PixelReceiver {
	private static final long serialVersionUID = 2752076462014410311L;
	
	/*
	 * TODO:
	 * - option to set frame rate (FPS)
	 * - option for spacing (small, medium, large spacing)
	 */
	
	private RLServerSimulator emulator;
	private SimulatorPanel pixelPanel;
	private Dimension pixelPanelSize = new Dimension(20, 20);
	private JPanel contentPane;
	private JLabel lblStatus;

	/**
	 * Create the frame.
	 */
	public SimulatorFrame() {
		super("simulator", Main.getInstance().getSettingsManager());
		emulator = new RLServerSimulator(this);
		emulator.addStateChangeListener(stateListener);
		
		pixelPanel = new SimulatorPanel(1000 / 60, pixelPanelSize.width, 10);
		
		// add window close action
		setCloseAction(() -> emulator.stop());
		setFrameTitle(i18n.getString("EmulatorFrame.Disconnected")); //$NON-NLS-1$
		setMinimumSize(new Dimension(300, 100));
		
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panelToolbar = new JPanel();
		UiUtils.bindBackground(panelToolbar, Style.panelDarkBackground());
		contentPane.add(panelToolbar, BorderLayout.NORTH);
		panelToolbar.setLayout(new BoxLayout(panelToolbar, BoxLayout.X_AXIS));
		
		JPanel panelControl = new JPanel();
		WrapLayout wl_panelControl = new WrapLayout(FlowLayout.LEFT);
		panelControl.setLayout(wl_panelControl);
		UiUtils.bindBackground(panelControl, Style.panelDarkBackground());
		panelToolbar.add(panelControl);
		
		JButton btnEnable = new JButton(i18n.getString("EmulatorFrame.Enable")); //$NON-NLS-1$
		btnEnable.setName("enable"); //$NON-NLS-1$
		btnEnable.addActionListener(btnListener);
		panelControl.add(btnEnable);
		
		lblStatus = new JLabel();
		panelControl.add(lblStatus);
		
		JPanel panelScale = new JPanel();
		UiUtils.bindBackground(panelScale, Style.panelDarkBackground());
		panelToolbar.add(panelScale);
		panelScale.setLayout(new BoxLayout(panelScale, BoxLayout.X_AXIS));
		
		JCheckBox checkBoxAlwaysTop = new JCheckBox("always on top");
		checkBoxAlwaysTop.setSelected(isAlwayTop());
		UiUtils.bindBackground(checkBoxAlwaysTop, Style.panelDarkBackground());
		checkBoxAlwaysTop.setFocusable(false);
		checkBoxAlwaysTop.addActionListener(checkBoxAlwaysTopListener);
		panelScale.add(checkBoxAlwaysTop);
		
		JButton btnMinus = new JButton("-"); //$NON-NLS-1$
		btnMinus.setName("minus"); //$NON-NLS-1$
		btnMinus.addActionListener(btnListener);
		panelScale.add(btnMinus);
		
		JButton btnPlus = new JButton("+"); //$NON-NLS-1$
		btnPlus.setName("plus"); //$NON-NLS-1$
		btnPlus.addActionListener(btnListener);
		panelScale.add(btnPlus);
		
		JPanel bgrPixel = new JPanel();
		contentPane.add(bgrPixel, BorderLayout.CENTER);
		bgrPixel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane(pixelPanel);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		bgrPixel.add(scrollPane, BorderLayout.CENTER);
	}
	
	public void setFrameTitle(String text) {
		setTitle(i18n.getString("EmulatorFrame.Title") + text); //$NON-NLS-1$
	}
	
	private ActionListener btnListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			switch (btn.getName()) {
			case "enable":
				btn.setText(!emulator.isRunning() ? i18n.getString("EmulatorFrame.Disable") : i18n.getString("EmulatorFrame.Enable")); //$NON-NLS-1$ //$NON-NLS-2$
				toggleEmulator(!emulator.isRunning());
				break;
			case "minus":
				changePanelSize(-2);
				break;
			case "plus":
				changePanelSize(2);
				break;
			}
		}
	};
	
	
	private ActionListener checkBoxAlwaysTopListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox cb = (JCheckBox) e.getSource();
			setAlwaysTop(cb.isSelected());
		}
	};
	
	
	public void toggleEmulator(boolean enable) {
		if(enable) {
			emulator.start();
		} else {
			emulator.stop();
		}
	}
	
	private ConnectionStateChangeListener stateListener = new ConnectionStateChangeListener() {
		@Override
		public void onConnectionStateChanged(String status) {
			setFrameTitle(status);
		}
	};
	
	@Override
	public void onPixelReceived(Color[] pixels) {
		lblStatus.setText(i18n.getString("EmulatorFrame.PixelNumber") + pixels.length);
		pixelPanel.pushData(pixels);
	}
	
	public void changePanelSize(int value) {
		pixelPanelSize.setSize(pixelPanelSize.width + value, pixelPanelSize.height + value);
		if(pixelPanelSize.width <= 0) {
			pixelPanelSize.setSize(2, 2);
		} else if(pixelPanelSize.width > 200) {
			pixelPanelSize.setSize(200, 200);
		}
		
		pixelPanel.setLedWidth(pixelPanelSize.width);
	}

}
