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

package de.lars.remotelightclient.ui.panels.output;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.BigImageButton;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.ui.panels.output.outputComps.ArduinoSettingsPanel;
import de.lars.remotelightclient.ui.panels.output.outputComps.ArtnetSettingsPanel;
import de.lars.remotelightclient.ui.panels.output.outputComps.ChainSettingsPanel;
import de.lars.remotelightclient.ui.panels.output.outputComps.DeviceSettingsPanel;
import de.lars.remotelightclient.ui.panels.output.outputComps.E131SettingsPanel;
import de.lars.remotelightclient.ui.panels.output.outputComps.MultiOutputSettingsPanel;
import de.lars.remotelightclient.ui.panels.output.outputComps.RLServerSettingsPanel;
import de.lars.remotelightclient.ui.panels.output.outputComps.VirtualOutputSettingsPanel;
import de.lars.remotelightclient.ui.simulator.SimulatorFrame;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.devices.DeviceManager;
import de.lars.remotelightcore.devices.arduino.Arduino;
import de.lars.remotelightcore.devices.artnet.Artnet;
import de.lars.remotelightcore.devices.e131.E131;
import de.lars.remotelightcore.devices.link.chain.Chain;
import de.lars.remotelightcore.devices.link.multi.MultiOutput;
import de.lars.remotelightcore.devices.remotelightserver.RemoteLightServer;
import de.lars.remotelightcore.devices.virtual.VirtualOutput;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.out.Output;
import de.lars.remotelightcore.out.OutputActionListener;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.OutputUtil;
import jiconfont.IconCode;

public class OutputPanel extends MenuPanel {
	private static final long serialVersionUID = 8004937110428129961L;
	
	private MainFrame mainFrame;
	private SimulatorFrame emulator;
	private OutputManager om = RemoteLightCore.getInstance().getOutputManager();
	private DeviceManager dm = RemoteLightCore.getInstance().getDeviceManager();
	private JPanel bgrMenu;
	private DeviceSettingsPanel currentSettingsPanel;
	private JPopupMenu popupMenu;
	private JButton btnActivate;

	/**
	 * Create the panel.
	 */
	public OutputPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		mainFrame.showControlBar(true);
		mainFrame.setControlBarPanel(new DefaultControlBar());
		om.addOutputActionListener(outputActionListener);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel bgrDevices = new JPanel();
		int space = 10;
		bgrDevices.setBorder(new EmptyBorder(space, space, 0, space));
		add(bgrDevices);
		bgrDevices.setLayout(new BorderLayout(0, 0));
		
		TScrollPane scrollPane = new TScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		bgrDevices.add(scrollPane);
		
		JPanel panelDevices = new JPanel();
		panelDevices.setLayout(new WrapLayout());
		WrapLayout fl_panelDevices = (WrapLayout) panelDevices.getLayout();
		fl_panelDevices.setAlignment(FlowLayout.LEFT);
		UiUtils.bindBackground(panelDevices, Style.panelDarkBackground());
		panelDevices.setSize(new Dimension(200, 1));
		addDeviceButtons(panelDevices);
		scrollPane.setViewportView(panelDevices);
		
		popupMenu = new JPopupMenu();
		panelDevices.add(popupMenu);
		
		BigImageButton add =  new BigImageButton(Style.getFontIcon(MenuIcon.ADD, 23), i18n.getString("Basic.Add")); //$NON-NLS-1$ //$NON-NLS-2$
		this.addPopupListener(add, popupMenu);
		panelDevices.add(add);
		
		JMenuItem itemArduino = new JMenuItem("Arduino"); //$NON-NLS-1$
		itemArduino.setIcon(Style.getFontIcon(MenuIcon.ARDUINO)); //$NON-NLS-1$
		this.configureAddPopup(itemArduino, "arduino"); //$NON-NLS-1$
		popupMenu.add(itemArduino);
		
		JMenuItem itemRLServer = new JMenuItem("RLServer (Raspberry)"); //$NON-NLS-1$
		itemRLServer.setIcon(Style.getFontIcon(MenuIcon.RASPBERRYPI)); //$NON-NLS-1$
		this.configureAddPopup(itemRLServer, "rlserver"); //$NON-NLS-1$
		popupMenu.add(itemRLServer);
		
		JMenuItem itemArtnet = new JMenuItem("Art-Net");
		itemArtnet.setIcon(Style.getFontIcon(MenuIcon.ARTNET)); //$NON-NLS-1$
		this.configureAddPopup(itemArtnet, "artnet");
		popupMenu.add(itemArtnet);
		
		JMenuItem itemE131 = new JMenuItem("E1.31");
		itemE131.setIcon(Style.getFontIcon(MenuIcon.ARTNET)); //$NON-NLS-1$
		this.configureAddPopup(itemE131, "e131");
		popupMenu.add(itemE131);
		
		JMenuItem itemVirtual = new JMenuItem("Virtual");
		itemVirtual.setIcon(Style.getFontIcon(MenuIcon.VIRTUAL)); //$NON-NLS-1$
		this.configureAddPopup(itemVirtual, "virtual");
		popupMenu.add(itemVirtual);
		
		JMenu mnLink = new JMenu(i18n.getString("OutputPanel.Link")); //$NON-NLS-1$
		mnLink.setIcon(Style.getFontIcon(MenuIcon.LINK_STRIPS)); //$NON-NLS-1$
		this.configureAddPopup(mnLink, "menulink"); //$NON-NLS-1$
		popupMenu.add(mnLink);
		
		JMenuItem itemMultiOutput = new JMenuItem(i18n.getString("OutputPanel.MultiOutput")); //$NON-NLS-1$
		itemMultiOutput.setIcon(Style.getFontIcon(MenuIcon.LINK_STRIPS));
		this.configureAddPopup(itemMultiOutput, "multioutput"); //$NON-NLS-1$
		mnLink.add(itemMultiOutput);
		
		JMenuItem itemChain = new JMenuItem(i18n.getString("OutputPanel.Chain")); //$NON-NLS-1$
		itemChain.setIcon(Style.getFontIcon(MenuIcon.CHAIN)); //$NON-NLS-1$
		this.configureAddPopup(itemChain, "chain"); //$NON-NLS-1$
		mnLink.add(itemChain);
		
		JPanel panelTitle = new JPanel();
		bgrDevices.add(panelTitle, BorderLayout.NORTH);
		panelTitle.setLayout(new BoxLayout(panelTitle, BoxLayout.X_AXIS));
		
		JLabel lblDevices = new JLabel(i18n.getString("OutputPanel.Outputs")); //$NON-NLS-1$
		panelTitle.add(lblDevices);
		lblDevices.setFont(Style.getFontBold(12));
		UiUtils.bindForeground(lblDevices, Style.accent());
		
		Component glue = Box.createGlue();
		panelTitle.add(glue);
		
		JLabel lblEmulator = new JLabel(i18n.getString("OutputPanel.OpenEmulator")); //$NON-NLS-1$
		lblEmulator.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(emulator == null || !emulator.isDisplayable()) {
					emulator = new SimulatorFrame();
				} else {
					emulator.setVisible(true);
					emulator.toFront();
				}
			}
		});
		lblEmulator.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		UiUtils.bindForeground(lblEmulator, Style.accent());
		panelTitle.add(lblEmulator);
		
		bgrMenu = new JPanel();
		bgrMenu.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200));
		bgrMenu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
		bgrMenu.setLayout(new BorderLayout(0, 0));
		add(bgrMenu);
	}
	
	private JPanel getDeviceSettingsBgr(boolean setup, JPanel deviceSettingsPanel) {
		Dimension btnSize = new Dimension(100, 25);
		
		JPanel bgrDeviceSettings = new JPanel();
		bgrDeviceSettings.setLayout(new BorderLayout(0, 0));
		bgrDeviceSettings.setPreferredSize(bgrMenu.getSize());
		
		TScrollPane scrollPane = new TScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(8);
		bgrDeviceSettings.add(scrollPane, BorderLayout.CENTER);
		
		JPanel panelDeviceSettings = new JPanel();
		panelDeviceSettings.setLayout(new BorderLayout(0, 0));
		scrollPane.setViewportView(panelDeviceSettings);
		
		panelDeviceSettings.add(deviceSettingsPanel, BorderLayout.CENTER);
		
		JPanel panelOptions = new JPanel();
		WrapLayout wlayout = new WrapLayout();
		wlayout.setAlignment(FlowLayout.LEFT);
		panelOptions.setLayout(wlayout);
		bgrDeviceSettings.add(panelOptions, BorderLayout.SOUTH);
		
		JButton btnCancel = new JButton(i18n.getString("Basic.Cancel")); //$NON-NLS-1$
		btnCancel.setPreferredSize(btnSize);
		btnCancel.setName("cancel"); //$NON-NLS-1$
		btnCancel.addActionListener(optionsButtonListener);
		panelOptions.add(btnCancel);
		
		JButton btnRemove = new JButton(i18n.getString("Basic.Remove")); //$NON-NLS-1$
		btnRemove.setPreferredSize(btnSize);
		btnRemove.setName("remove"); //$NON-NLS-1$
		btnRemove.addActionListener(optionsButtonListener);
		panelOptions.add(btnRemove);
		
		JButton btnSave = new JButton(i18n.getString("Basic.Save")); //$NON-NLS-1$
		btnSave.setPreferredSize(btnSize);
		btnSave.setName("save"); //$NON-NLS-1$
		btnSave.addActionListener(optionsButtonListener);
		panelOptions.add(btnSave);
		
		btnActivate = new JButton(i18n.getString("Baisc.Activate")); //$NON-NLS-1$
		btnActivate.setPreferredSize(btnSize);
		btnActivate.setName("activate"); //$NON-NLS-1$
		btnActivate.addActionListener(optionsButtonListener);
		panelOptions.add(btnActivate);
		
		
		if(setup) {
			btnRemove.setVisible(false);
			btnActivate.setVisible(false);
		}
		return bgrDeviceSettings;
	}
	
	public void addDeviceButtons(JPanel panel) {
		for(Device d : dm.getDevices()) {
			IconCode icon = null;
			if(d instanceof Arduino) {
				icon = MenuIcon.ARDUINO;
			} else if(d instanceof RemoteLightServer) {
				icon = MenuIcon.RASPBERRYPI;
			} else if(d instanceof Artnet) {
				icon = MenuIcon.ARTNET;
			} else if(d instanceof E131) {
				icon = MenuIcon.ARTNET; // TODO: add E1.31 icon
			} else if(d instanceof VirtualOutput) {
				icon = MenuIcon.VIRTUAL;
			} else if(d instanceof Chain) {
				icon = MenuIcon.CHAIN;
			} else if(d instanceof MultiOutput) {
				icon = MenuIcon.LINK_STRIPS;
			} else {
				icon = MenuIcon.ERROR;
			}
			BigImageButton btn =  new BigImageButton(Style.getFontIcon(icon, 35), d.getId());
			btn.setName(d.getId());
			btn.addMouseListener(deviceClicked);
			if(om.getActiveOutput() != null && om.getActiveOutput() == d && d.getConnectionState() == ConnectionState.CONNECTED) {
				btn.setBorder(BorderFactory.createLineBorder(Style.accent().get()));
			}
			panel.add(btn);
		}
	}
	
	private MouseAdapter deviceClicked = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			BigImageButton btn = (BigImageButton) e.getSource();
			for(Device d : dm.getDevices()) {
				if(d.getId().equals(btn.getName())) {
					//double click -> activate
					if(e.getClickCount() == 2) {
						if((currentSettingsPanel == null || !currentSettingsPanel.isSetup()) && dm.isIdUsed(d.getId())) {
							om.setActiveOutput(d);
							mainFrame.displayPanel(new OutputPanel(mainFrame));
							break;
						}
					}
					showSettingsPanel(d, false);
					break;
				}
			}
		}
	};
	
	public void showSettingsPanel(Device d, boolean setup) {
		DeviceSettingsPanel panel = null;
		
		if(d instanceof Arduino) {
			panel = new ArduinoSettingsPanel((Arduino) d, setup);
		} else if(d instanceof RemoteLightServer) {
			panel = new RLServerSettingsPanel((RemoteLightServer) d, setup);
		} else if(d instanceof Artnet) {
			panel = new ArtnetSettingsPanel((Artnet) d, setup);
		} else if(d instanceof E131) {
			panel = new E131SettingsPanel((E131) d, setup);
		} else if(d instanceof VirtualOutput) {
			panel = new VirtualOutputSettingsPanel((VirtualOutput) d, setup);
		} else if(d instanceof Chain) {
			panel = new ChainSettingsPanel((Chain) d, setup);
		} else if(d instanceof MultiOutput) {
			panel = new MultiOutputSettingsPanel((MultiOutput) d, setup);
		} else {
			// no panel for the device type available
			Main.getInstance().showNotification(NotificationType.WARN, "UI Error", "There is no configuaration panel for device type '" + d.getClass().getSimpleName() + "'.");
		}
		
		if(panel != null) {
			currentSettingsPanel = panel;
			JPanel bgr = getDeviceSettingsBgr(setup, panel);
			
			if(d.getConnectionState() == ConnectionState.CONNECTED) {
				btnActivate.setText(i18n.getString("Basic.Deactivate")); //$NON-NLS-1$
			}
			
			bgrMenu.removeAll();
			bgrMenu.add(bgr, BorderLayout.CENTER);
			
			String outputType = OutputUtil.getOutputTypeAsString(d);
			String titleText = setup ? "Setup " + outputType : String.format("Configure %s (%s)", d.getId(), outputType);
			JLabel lblTitle = new JLabel(titleText);
			lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
			bgrMenu.add(lblTitle, BorderLayout.NORTH);
			
			bgrMenu.updateUI();
		}
	}
	
	public void hideSettingsPanel() {
		currentSettingsPanel = null;
		bgrMenu.removeAll();
		bgrMenu.updateUI();
	}
	
	private void configureAddPopup(JMenuItem item, String name) {
		item.setName(name);
		item.addActionListener(menuItemListener);
	}

	private void addPopupListener(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				hideSettingsPanel();
				showMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				showMenu(e);
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	
	private ActionListener menuItemListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem) e.getSource();
			Device device = null;
			
			switch (item.getName()) {
			case "arduino": //$NON-NLS-1$
				device = new Arduino(null, null);
				break;
			case "rlserver": //$NON-NLS-1$
				device = new RemoteLightServer(null, null);
				break;
			case "artnet":
				device = new Artnet(null);
				break;
			case "e131":
				device = new E131(null);
				break;
			case "virtual":
				device = new VirtualOutput(null, OutputManager.MIN_PIXELS);
				break;
			case "multioutput": //$NON-NLS-1$
				device = new MultiOutput(null);
				break;
			case "chain": //$NON-NLS-1$
				device = new Chain(null);
				break;
			}
			if(device != null) {
				showSettingsPanel(device, true);
			}
		}
	};
	
	private ActionListener optionsButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			String name = btn.getName();
			Main main = Main.getInstance();
			String notiTitle = "Outputs"; // notification title
			
			//SAVE clicked
			if(name.equals("save") && currentSettingsPanel != null) { //$NON-NLS-1$
				if(!currentSettingsPanel.getId().isEmpty()) {
					
					if(!dm.isIdUsed(currentSettingsPanel.getId()) || currentSettingsPanel.getId().equals(currentSettingsPanel.getDevice().getId())) {
						
						currentSettingsPanel.save();
						Device device = currentSettingsPanel.getDevice();
						
						if(currentSettingsPanel.isSetup()) {
							if(dm.addDevice(device)) {
								mainFrame.displayPanel(new OutputPanel(mainFrame));
								main.showNotification(NotificationType.SUCCESS, notiTitle, i18n.getString("OutputPanel.AddedDevice"));
							} else {
								main.showNotification(NotificationType.ERROR, i18n.getString("OutputPanel.NameAlreadyUsed"));
							}
						} else {
							mainFrame.displayPanel(new OutputPanel(mainFrame));
							main.showNotification(new Notification(NotificationType.INFO, notiTitle, i18n.getString("OutputPanel.SavedChanges"), Notification.SHORT));
						}
					} else {
						main.showNotification(NotificationType.ERROR, i18n.getString("OutputPanel.NameAlreadyUsed"));
					}
				} else {
					main.showNotification(NotificationType.ERROR, i18n.getString("OutputPanel.NameCouldNotEmpty"));
				}
			//REMOVE clicked
			} else if(name.equals("remove") && currentSettingsPanel != null) { //$NON-NLS-1$
				Device device = currentSettingsPanel.getDevice();
				if(!currentSettingsPanel.isSetup() && dm.isIdUsed(device.getId())) {
					dm.removeDevice(device);
					mainFrame.displayPanel(new OutputPanel(mainFrame));
					main.showNotification(NotificationType.INFO, notiTitle, i18n.getString("OutputPanel.RemovedDevice"));
				} else {
					main.showNotification(NotificationType.ERROR, i18n.getString("OutputPanel.CouldNotRemoveDevice"));
				}
			//ACTIVATE clicked
			} else if(name.equals("activate") && currentSettingsPanel != null) { //$NON-NLS-1$
				Device device = currentSettingsPanel.getDevice();
				if(!currentSettingsPanel.isSetup() && dm.isIdUsed(device.getId())) {
					
					if(device.getConnectionState() == ConnectionState.CONNECTED) {
						om.deactivate(device);
					} else {
						om.setActiveOutput(device);
					}
					mainFrame.displayPanel(new OutputPanel(mainFrame));
					if(device.getConnectionState() == ConnectionState.FAILED) {
						main.showNotification(NotificationType.ERROR, i18n.getString("OutputPanel.CouldNotConnect"));
					}
				}
			//CANCEL clicked
			} else if(name.equals("cancel")) { //$NON-NLS-1$
				hideSettingsPanel();
			}
		}
	};
	
	
	private OutputActionListener outputActionListener = new OutputActionListener() {
		@Override
		public void onOutputAction(Output output, OutputActionType type) {
			if(currentSettingsPanel == null && mainFrame.getDisplayedPanel() instanceof OutputPanel) {
				mainFrame.displayPanel(new OutputPanel(mainFrame));
			}
		}
	};
	
	@Override
	public String getName() {
		return i18n.getString("Basic.Output");
	}
	
}
