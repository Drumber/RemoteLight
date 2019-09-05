package de.lars.remotelightclient.ui.panels;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.devices.Device;
import de.lars.remotelightclient.devices.DeviceManager;
import de.lars.remotelightclient.devices.arduino.Arduino;
import de.lars.remotelightclient.devices.remotelightserver.RemoteLightServer;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.MainFrame.NotificationType;
import de.lars.remotelightclient.ui.comps.BigImageButton;
import de.lars.remotelightclient.ui.panels.outputComps.ArduinoSettingsPanel;
import de.lars.remotelightclient.ui.panels.outputComps.DeviceSettingsPanel;
import de.lars.remotelightclient.ui.panels.outputComps.RLServerSettingsPanel;
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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;

public class OutputPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8004937110428129961L;
	private MainFrame mainFrame;
	private OutputManager om = Main.getInstance().getOutputManager();
	private DeviceManager dm = Main.getInstance().getDeviceManager();
	private JPanel bgrMenu;
	private DeviceSettingsPanel currentSettingsPanel;
	private JPopupMenu popupMenu;

	/**
	 * Create the panel.
	 */
	public OutputPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		mainFrame.showControlBar(true);
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
		UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(Style.accent));
		panelDevices.add(popupMenu);
		
		BigImageButton add =  new BigImageButton(Style.getUiIcon("add.png"), "Add");
		this.addPopupListener(add, popupMenu);
		panelDevices.add(add);
		
		JMenuItem itemArduino = new JMenuItem("Arduino");
		itemArduino.setIcon(Style.getUiIcon("arduino.png"));
		this.configureAddPopup(itemArduino, "arduino");
		popupMenu.add(itemArduino);
		
		JMenuItem itemRLServer = new JMenuItem("RLServer (Raspberry)");
		itemRLServer.setIcon(Style.getUiIcon("raspberry.png"));
		this.configureAddPopup(itemRLServer, "rlserver");
		popupMenu.add(itemRLServer);
		
		JMenu mnLink = new JMenu("Link");
		this.configureAddPopup(mnLink, "menulink");
		popupMenu.add(mnLink);
		
		JMenuItem itemMultiOutput = new JMenuItem("MultiOutput");
		this.configureAddPopup(itemMultiOutput, "multioutput");
		mnLink.add(itemMultiOutput);
		
		JMenuItem itemChain = new JMenuItem("Chain");
		this.configureAddPopup(itemChain, "chain");
		mnLink.add(itemChain);
		
		JLabel lblDevices = new JLabel("Outputs");
		lblDevices.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDevices.setForeground(Style.accent);
		bgrDevices.add(lblDevices, BorderLayout.NORTH);
		
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
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(btnSize);
		UiUtils.configureButton(btnCancel);
		btnCancel.setName("cancel");
		btnCancel.addActionListener(optionsButtonListener);
		panelOptions.add(btnCancel);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.setPreferredSize(btnSize);
		UiUtils.configureButton(btnRemove);
		btnRemove.setName("remove");
		btnRemove.addActionListener(optionsButtonListener);
		panelOptions.add(btnRemove);
		
		JButton btnSave = new JButton("Save");
		btnSave.setPreferredSize(btnSize);
		UiUtils.configureButton(btnSave);
		btnSave.setName("save");
		btnSave.addActionListener(optionsButtonListener);
		panelOptions.add(btnSave);
		
		JButton btnSelect = new JButton("Select");
		btnSelect.setPreferredSize(btnSize);
		UiUtils.configureButton(btnSelect);
		btnSelect.setName("select");
		btnSelect.addActionListener(optionsButtonListener);
		panelOptions.add(btnSelect);
		
		
		if(setup) {
			btnRemove.setVisible(false);
			btnSelect.setVisible(false);
		}
		
		return bgrDeviceSettings;
	}
	
	public void addDeviceButtons(JPanel panel) {
		for(Device d : dm.getDevices()) {
			String icon = "error.png";
			if(d instanceof Arduino) {
				icon = "arduino.png";
			} else if(d instanceof RemoteLightServer) {
				icon = "raspberry.png";
			}
			BigImageButton btn =  new BigImageButton(Style.getUiIcon(icon), d.getId());
			btn.setName(d.getId());
			btn.addMouseListener(deviceClicked);
			if(om.getActiveOutput() != null && om.getActiveOutput() == d) {
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
					//double click -> select
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
			case "arduino":
				device = new Arduino(null, null);
				break;
			case "rlserver":
				device = new RemoteLightServer(null, null);
				break;
			case "multioutput": //TODO
				
				break;
			case "chain":
				
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
			if(name.equals("save") && currentSettingsPanel != null) {
				if(!currentSettingsPanel.getId().isEmpty()) {
					
					if(!dm.isIdUsed(currentSettingsPanel.getId()) || currentSettingsPanel.getId().equals(currentSettingsPanel.getDevice().getId())) {
						
						currentSettingsPanel.save();
						Device device = currentSettingsPanel.getDevice();
						
						if(currentSettingsPanel.isSetup()) {
							if(dm.addDevice(device)) {
								mainFrame.displayPanel(new OutputPanel(mainFrame));
								mainFrame.printNotification("Added new device.", NotificationType.Success);
							} else {
								mainFrame.printNotification("Name / ID already used!", NotificationType.Error);
							}
						} else {
							mainFrame.displayPanel(new OutputPanel(mainFrame));
							mainFrame.printNotification("Saved changes.", NotificationType.Unimportant);
						}
					} else {
						mainFrame.printNotification("Name / ID already used!", NotificationType.Error);
					}
				} else {
					mainFrame.printNotification("Name / ID field cannot be empty!", NotificationType.Error);
				}
			//REMOVE clicked
			} else if(name.equals("remove") && currentSettingsPanel != null) {
				Device device = currentSettingsPanel.getDevice();
				if(!currentSettingsPanel.isSetup() && dm.isIdUsed(device.getId())) {
					dm.removeDevice(device);
					mainFrame.displayPanel(new OutputPanel(mainFrame));
					mainFrame.printNotification("Removed device.", NotificationType.Info);
				} else {
					mainFrame.printNotification("Could not remove device!", NotificationType.Error);
				}
			//SELECT clicked
			} else if(name.equals("select") && currentSettingsPanel != null) {
				Device device = currentSettingsPanel.getDevice();
				if(!currentSettingsPanel.isSetup() && dm.isIdUsed(device.getId())) {
					om.setActiveOutput(device);
					mainFrame.displayPanel(new OutputPanel(mainFrame));
				}
			//CANCEL clicked
			} else if(name.equals("cancel")) {
				hideSettingsPanel();
				mainFrame.printNotification(null, null);
			}
		}
	};
	
	
}
