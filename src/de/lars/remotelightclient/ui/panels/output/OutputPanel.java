package de.lars.remotelightclient.ui.panels.output;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.devices.ConnectionState;
import de.lars.remotelightclient.devices.Device;
import de.lars.remotelightclient.devices.DeviceManager;
import de.lars.remotelightclient.devices.arduino.Arduino;
import de.lars.remotelightclient.devices.remotelightserver.RemoteLightServer;
import de.lars.remotelightclient.emulator.EmulatorFrame;
import de.lars.remotelightclient.lang.i18n;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.MainFrame.NotificationType;
import de.lars.remotelightclient.ui.comps.BigImageButton;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.ui.panels.output.outputComps.ArduinoSettingsPanel;
import de.lars.remotelightclient.ui.panels.output.outputComps.DeviceSettingsPanel;
import de.lars.remotelightclient.ui.panels.output.outputComps.RLServerSettingsPanel;
import de.lars.remotelightclient.utils.UiUtils;
import de.lars.remotelightclient.utils.WrapLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.Box;
import java.awt.Cursor;

public class OutputPanel extends MenuPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8004937110428129961L;
	private MainFrame mainFrame;
	private EmulatorFrame emulator;
	private OutputManager om = Main.getInstance().getOutputManager();
	private DeviceManager dm = Main.getInstance().getDeviceManager();
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
		setBackground(Style.panelBackground);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel bgrDevices = new JPanel();
		int space = 10;
		bgrDevices.setBorder(new EmptyBorder(space, space, 0, space));
		bgrDevices.setBackground(Style.panelBackground);
		add(bgrDevices);
		bgrDevices.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
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
		panelDevices.setBackground(Style.panelDarkBackground);
		panelDevices.setSize(new Dimension(200, 1));
		addDeviceButtons(panelDevices);
		scrollPane.setViewportView(panelDevices);
		
		popupMenu = new JPopupMenu();
		UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(Style.accent)); //$NON-NLS-1$
		panelDevices.add(popupMenu);
		
		BigImageButton add =  new BigImageButton(Style.getUiIcon("add.png"), i18n.getString("Basic.Add")); //$NON-NLS-1$ //$NON-NLS-2$
		this.addPopupListener(add, popupMenu);
		panelDevices.add(add);
		
		JMenuItem itemArduino = new JMenuItem("Arduino"); //$NON-NLS-1$
		itemArduino.setIcon(Style.getUiIcon("arduino.png")); //$NON-NLS-1$
		this.configureAddPopup(itemArduino, "arduino"); //$NON-NLS-1$
		popupMenu.add(itemArduino);
		
		JMenuItem itemRLServer = new JMenuItem("RLServer (Raspberry)"); //$NON-NLS-1$
		itemRLServer.setIcon(Style.getUiIcon("raspberry.png")); //$NON-NLS-1$
		this.configureAddPopup(itemRLServer, "rlserver"); //$NON-NLS-1$
		popupMenu.add(itemRLServer);
		
		JMenu mnLink = new JMenu(i18n.getString("OutputPanel.Link")); //$NON-NLS-1$
		this.configureAddPopup(mnLink, "menulink"); //$NON-NLS-1$
		popupMenu.add(mnLink);
		
		JMenuItem itemMultiOutput = new JMenuItem(i18n.getString("OutputPanel.MultiOutput")); //$NON-NLS-1$
		this.configureAddPopup(itemMultiOutput, "multioutput"); //$NON-NLS-1$
		mnLink.add(itemMultiOutput);
		
		JMenuItem itemChain = new JMenuItem(i18n.getString("OutputPanel.Chain")); //$NON-NLS-1$
		this.configureAddPopup(itemChain, "chain"); //$NON-NLS-1$
		mnLink.add(itemChain);
		
		JPanel panelTitle = new JPanel();
		bgrDevices.add(panelTitle, BorderLayout.NORTH);
		panelTitle.setBackground(Style.panelBackground);
		panelTitle.setLayout(new BoxLayout(panelTitle, BoxLayout.X_AXIS));
		
		JLabel lblDevices = new JLabel(i18n.getString("OutputPanel.Outputs")); //$NON-NLS-1$
		panelTitle.add(lblDevices);
		lblDevices.setFont(Style.getFontBold(12));
		lblDevices.setForeground(Style.accent);
		
		Component glue = Box.createGlue();
		panelTitle.add(glue);
		
		JLabel lblEmulator = new JLabel(i18n.getString("OutputPanel.OpenEmulator")); //$NON-NLS-1$
		lblEmulator.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(emulator == null || !emulator.isDisplayable()) {
					emulator = new EmulatorFrame();
				} else {
					emulator.setVisible(true);
					emulator.toFront();
				}
			}
		});
		lblEmulator.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblEmulator.setForeground(Style.accent);
		panelTitle.add(lblEmulator);
		
		bgrMenu = new JPanel();
		bgrMenu.setBackground(Style.panelBackground);
		bgrMenu.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
		bgrMenu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
		add(bgrMenu);
		bgrMenu.setLayout(new BorderLayout(0, 0));

	}
	
	private JPanel getDeviceSettingsBgr(boolean setup) {
		Dimension btnSize = new Dimension(100, 25);
		
		JPanel bgrDeviceSettings = new JPanel();
		bgrDeviceSettings.setBackground(Style.panelBackground);
		bgrDeviceSettings.setLayout(new BorderLayout(0, 0));
		bgrDeviceSettings.setPreferredSize(bgrMenu.getSize());
		
		JPanel panelOptions = new JPanel();
		WrapLayout wlayout = new WrapLayout();
		wlayout.setAlignment(FlowLayout.LEFT);
		panelOptions.setLayout(wlayout);
		bgrDeviceSettings.add(panelOptions, BorderLayout.SOUTH);
		panelOptions.setBackground(Style.panelBackground);
		
		JButton btnCancel = new JButton(i18n.getString("Basic.Cancel")); //$NON-NLS-1$
		btnCancel.setPreferredSize(btnSize);
		UiUtils.configureButton(btnCancel);
		btnCancel.setName("cancel"); //$NON-NLS-1$
		btnCancel.addActionListener(optionsButtonListener);
		panelOptions.add(btnCancel);
		
		JButton btnRemove = new JButton(i18n.getString("Basic.Remove")); //$NON-NLS-1$
		btnRemove.setPreferredSize(btnSize);
		UiUtils.configureButton(btnRemove);
		btnRemove.setName("remove"); //$NON-NLS-1$
		btnRemove.addActionListener(optionsButtonListener);
		panelOptions.add(btnRemove);
		
		JButton btnSave = new JButton(i18n.getString("Basic.Save")); //$NON-NLS-1$
		btnSave.setPreferredSize(btnSize);
		UiUtils.configureButton(btnSave);
		btnSave.setName("save"); //$NON-NLS-1$
		btnSave.addActionListener(optionsButtonListener);
		panelOptions.add(btnSave);
		
		btnActivate = new JButton(i18n.getString("Baisc.Activate")); //$NON-NLS-1$
		btnActivate.setPreferredSize(btnSize);
		UiUtils.configureButton(btnActivate);
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
			String icon = "error.png"; //$NON-NLS-1$
			if(d instanceof Arduino) {
				icon = "arduino.png"; //$NON-NLS-1$
			} else if(d instanceof RemoteLightServer) {
				icon = "raspberry.png"; //$NON-NLS-1$
			}
			BigImageButton btn =  new BigImageButton(Style.getUiIcon(icon), d.getId());
			btn.setName(d.getId());
			btn.addMouseListener(deviceClicked);
			if(om.getActiveOutput() != null && om.getActiveOutput() == d && d.getConnectionState() == ConnectionState.CONNECTED) {
				btn.setBorder(BorderFactory.createLineBorder(Style.accent));
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
						if((!currentSettingsPanel.isSetup() || currentSettingsPanel == null) && dm.isIdUsed(d.getId())) {
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
		JPanel bgr = getDeviceSettingsBgr(setup);
		DeviceSettingsPanel panel = null;
		
		if(d instanceof Arduino) {
			panel = new ArduinoSettingsPanel((Arduino) d, setup);
		} else if(d instanceof RemoteLightServer) {
			panel = new RLServerSettingsPanel((RemoteLightServer) d, setup);
		}
		
		if(d.getConnectionState() == ConnectionState.CONNECTED) {
			btnActivate.setText(i18n.getString("Basic.Deactivate")); //$NON-NLS-1$
		}
		
		if(panel != null) {
			currentSettingsPanel = panel;
			bgr.add(panel, BorderLayout.CENTER);
			bgrMenu.removeAll();
			bgrMenu.add(bgr, BorderLayout.CENTER);
			bgrMenu.updateUI();
		}
	}
	
	public void hideSettingsPanel() {
		currentSettingsPanel = null;
		bgrMenu.removeAll();
		bgrMenu.updateUI();
	}
	
	private void configureAddPopup(JMenuItem item, String name) {
		item.setBackground(Style.panelAccentBackground);
		item.setContentAreaFilled(false);
		item.setOpaque(true);
		item.setForeground(Style.textColor);
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
			case "multioutput": //TODO //$NON-NLS-1$
				
				break;
			case "chain": //$NON-NLS-1$
				
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
			
			//SAVE clicked
			if(name.equals("save") && currentSettingsPanel != null) { //$NON-NLS-1$
				if(!currentSettingsPanel.getId().isEmpty()) {
					
					if(!dm.isIdUsed(currentSettingsPanel.getId()) || currentSettingsPanel.getId().equals(currentSettingsPanel.getDevice().getId())) {
						
						currentSettingsPanel.save();
						Device device = currentSettingsPanel.getDevice();
						
						if(currentSettingsPanel.isSetup()) {
							if(dm.addDevice(device)) {
								mainFrame.displayPanel(new OutputPanel(mainFrame));
								mainFrame.printNotification(i18n.getString("OutputPanel.AddedDevice"), NotificationType.Success); //$NON-NLS-1$
							} else {
								mainFrame.printNotification(i18n.getString("OutputPanel.NameAlreadyUsed"), NotificationType.Error); //$NON-NLS-1$
							}
						} else {
							mainFrame.displayPanel(new OutputPanel(mainFrame));
							mainFrame.printNotification(i18n.getString("OutputPanel.SavedChanges"), NotificationType.Unimportant); //$NON-NLS-1$
						}
					} else {
						mainFrame.printNotification(i18n.getString("OutputPanel.NameAlreadyUsed"), NotificationType.Error); //$NON-NLS-1$
					}
				} else {
					mainFrame.printNotification(i18n.getString("OutputPanel.NameCouldNotEmpty"), NotificationType.Error); //$NON-NLS-1$
				}
			//REMOVE clicked
			} else if(name.equals("remove") && currentSettingsPanel != null) { //$NON-NLS-1$
				Device device = currentSettingsPanel.getDevice();
				if(!currentSettingsPanel.isSetup() && dm.isIdUsed(device.getId())) {
					dm.removeDevice(device);
					mainFrame.displayPanel(new OutputPanel(mainFrame));
					mainFrame.printNotification(i18n.getString("OutputPanel.RemovedDevice"), NotificationType.Info); //$NON-NLS-1$
				} else {
					mainFrame.printNotification(i18n.getString("OutputPanel.CouldNotRemoveDevice"), NotificationType.Error); //$NON-NLS-1$
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
						mainFrame.printNotification(i18n.getString("OutputPanel.CouldNotConnect"), NotificationType.Error); //$NON-NLS-1$
					} else {
						mainFrame.printNotification(null, null);
					}
				}
			//CANCEL clicked
			} else if(name.equals("cancel")) { //$NON-NLS-1$
				hideSettingsPanel();
				mainFrame.printNotification(null, null);
			}
		}
	};
	
	
}
