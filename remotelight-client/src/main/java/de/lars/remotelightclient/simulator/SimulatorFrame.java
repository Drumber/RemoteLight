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

package de.lars.remotelightclient.simulator;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.lang.i18n;
import de.lars.remotelightclient.simulator.RLServerSimulator.ConnectionStateChangeListener;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;

public class SimulatorFrame extends JFrame {
	private static final long serialVersionUID = 2752076462014410311L;
	
	private RLServerSimulator emulator;
	private SettingsManager sm;
	private boolean loopActive = false;
	private Dimension pixelPanelSize = new Dimension(20, 20);
	private List<JPanel> pixelPanels;
	private JPanel contentPane;
	private JPanel panelPixel;
	private JLabel lblStatus;

	/**
	 * Create the frame.
	 */
	public SimulatorFrame() {
		pixelPanels = new ArrayList<>();
		sm = Main.getInstance().getSettingsManager();
		emulator = new RLServerSimulator();
		emulator.addStateChangeListener(stateListener);
		setFrameTitle(i18n.getString("EmulatorFrame.Disconnected")); //$NON-NLS-1$
		addWindowListener(closeListener);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(300, 100));
		setSize((Dimension) sm.getSettingObject("simulatorFrame.size").getValue());
		
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setBackground(Style.panelBackground);
		setContentPane(contentPane);
		
		JPanel panelToolbar = new JPanel();
		panelToolbar.setBackground(Style.panelDarkBackground);
		contentPane.add(panelToolbar, BorderLayout.NORTH);
		panelToolbar.setLayout(new BoxLayout(panelToolbar, BoxLayout.X_AXIS));
		
		JPanel panelControl = new JPanel();
		WrapLayout wl_panelControl = new WrapLayout(FlowLayout.LEFT);
		panelControl.setLayout(wl_panelControl);
		panelControl.setBackground(Style.panelDarkBackground);
		panelToolbar.add(panelControl);
		
		JButton btnEnable = new JButton(i18n.getString("EmulatorFrame.Enable")); //$NON-NLS-1$
		btnEnable.setName("enable"); //$NON-NLS-1$
		btnEnable.addActionListener(btnListener);
		UiUtils.configureButton(btnEnable);
		panelControl.add(btnEnable);
		
		lblStatus = new JLabel();
		lblStatus.setForeground(Style.textColor);
		panelControl.add(lblStatus);
		
		JPanel panelScale = new JPanel();
		panelScale.setBackground(Style.panelDarkBackground);
		panelToolbar.add(panelScale);
		panelScale.setLayout(new BoxLayout(panelScale, BoxLayout.X_AXIS));
		
		JCheckBox checkBoxAlwaysTop = new JCheckBox("always on top");
		sm.addSetting(new SettingBoolean("simulatorFrame.alwaysontop", "always on top", SettingCategory.Intern, null, false));
		checkBoxAlwaysTop.setSelected(sm.getSetting(SettingBoolean.class, "simulatorFrame.alwaysontop").getValue());
		setAlwaysOnTop(checkBoxAlwaysTop.isSelected());
		checkBoxAlwaysTop.setBackground(Style.panelDarkBackground);
		checkBoxAlwaysTop.setForeground(Style.textColor);
		checkBoxAlwaysTop.setFocusable(false);
		checkBoxAlwaysTop.addActionListener(checkBoxAlwaysTopListener);
		panelScale.add(checkBoxAlwaysTop);
		
		JButton btnMinus = new JButton("-"); //$NON-NLS-1$
		btnMinus.setName("minus"); //$NON-NLS-1$
		btnMinus.addActionListener(btnListener);
		UiUtils.configureButton(btnMinus);
		panelScale.add(btnMinus);
		
		JButton btnPlus = new JButton("+"); //$NON-NLS-1$
		btnPlus.setName("plus"); //$NON-NLS-1$
		btnPlus.addActionListener(btnListener);
		UiUtils.configureButton(btnPlus);
		panelScale.add(btnPlus);
		
		JPanel bgrPixel = new JPanel();
		contentPane.add(bgrPixel, BorderLayout.CENTER);
		bgrPixel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		bgrPixel.add(scrollPane, BorderLayout.CENTER);
		
		panelPixel = new JPanel();
		WrapLayout wlayout = new WrapLayout(FlowLayout.LEFT);
		panelPixel.setLayout(wlayout);
		panelPixel.setBackground(Style.panelBackground);
		scrollPane.setViewportView(panelPixel);
	}
	
	WindowListener closeListener = new WindowAdapter() {
		public void windowClosing(WindowEvent windowEvent) {
			if(getExtendedState() == NORMAL) {
				// save window size
				sm.getSettingObject("simulatorFrame.size").setValue(getSize());
			}
			emulator.stop();
			dispose();
		}
	};
	
	public void setFrameTitle(String text) {
		setTitle(i18n.getString("EmulatorFrame.Title") + text); //$NON-NLS-1$
	}
	
	private ActionListener btnListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			switch (btn.getName()) {
			case "enable": //$NON-NLS-1$
				pixelPanels.clear();
				btn.setText(!emulator.isRunning() ? i18n.getString("EmulatorFrame.Disable") : i18n.getString("EmulatorFrame.Enable")); //$NON-NLS-1$ //$NON-NLS-2$
				toggleEmulator(!emulator.isRunning());
				break;
			case "minus": //$NON-NLS-1$
				changePanelSize(-2);
				break;
			case "plus": //$NON-NLS-1$
				changePanelSize(2);
				break;
			}
		}
	};
	
	
	private ActionListener checkBoxAlwaysTopListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox cb = (JCheckBox) e.getSource();
			sm.getSetting(SettingBoolean.class, "simulatorFrame.alwaysontop").setValue(cb.isSelected());
			setAlwaysOnTop(cb.isSelected());
		}
	};
	
	
	public void toggleEmulator(boolean enable) {
		if(enable) {
			emulator.start();
			startLoop();
		} else {
			emulator.stop();
			stopLoop();
		}
	}
	
	private void startLoop() {
		if(!loopActive) {
			loopActive = true;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Logger.info("Started Simulator loop"); //$NON-NLS-1$
					
					while(loopActive) {
						if(emulator.getPixels() != null) {
							Color[] pixel = emulator.getPixels();
							
							if(pixelPanels.size() != pixel.length) {
								addPixelPanels(pixel);
								lblStatus.setText(i18n.getString("EmulatorFrame.PixelNumber") + pixel.length); //$NON-NLS-1$
							} else {
								setPanelColors(pixel);
							}
						}
						try {
							Thread.sleep(1000 / 30);	// running at 30 fps
						} catch (InterruptedException e) {
						}
					}
					Logger.info("Stopped Simulator loop"); //$NON-NLS-1$
				}
				
			}, "Simulator loop").start(); //$NON-NLS-1$
		}
	}
	
	private void stopLoop() {
		loopActive = false;
	}
	
	private ConnectionStateChangeListener stateListener = new ConnectionStateChangeListener() {
		@Override
		public void onConnectionStateChanged(String status) {
			setFrameTitle(status);
		}
	};
	
	
	public void addPixelPanels(Color[] pixels) {
		panelPixel.removeAll();
		pixelPanels.clear();
		
		for(Color c : pixels) {
			JPanel panel = new JPanel();
			panel.setBackground(c);
			panel.setPreferredSize(pixelPanelSize);
			
			panelPixel.add(panel);
			pixelPanels.add(panel);
		}
		panelPixel.updateUI();
	}
	
	public void setPanelColors(Color[] pixels) {
		if(pixels.length >= pixelPanels.size()) {
			for(int i = 0; i < pixelPanels.size(); i++) {
				pixelPanels.get(i).setBackground(pixels[i]);
			}
		}
	}
	
	public void changePanelSize(int value) {
		pixelPanelSize.setSize(pixelPanelSize.width + value, pixelPanelSize.height + value);
		if(pixelPanelSize.width <= 0) {
			pixelPanelSize.setSize(2, 2);
		} else if(pixelPanelSize.width > 200) {
			pixelPanelSize.setSize(200, 200);
		}
		
		for(JPanel panel : pixelPanels) {
			panel.setPreferredSize(pixelPanelSize);
		}
		panelPixel.updateUI();
	}

}
