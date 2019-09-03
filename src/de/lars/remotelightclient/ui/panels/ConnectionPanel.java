package de.lars.remotelightclient.ui.panels;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import de.lars.remotelightclient.devices.Device;
import de.lars.remotelightclient.devices.arduino.Arduino;
import de.lars.remotelightclient.devices.remotelightserver.RemoteLightServer;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.comps.BigImageButton;
import de.lars.remotelightclient.ui.panels.connectionComps.ArduinoSettingsPanel;
import de.lars.remotelightclient.ui.panels.connectionComps.DeviceSettingsPanel;
import de.lars.remotelightclient.ui.panels.connectionComps.RLServerSettingsPanel;
import de.lars.remotelightclient.utils.UiUtils;
import de.lars.remotelightclient.utils.WrapLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

public class ConnectionPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8004937110428129961L;
	private MainFrame mainFrame;
	private JPanel bgrMenu;
	private DeviceSettingsPanel currentSettingsPanel;
	private JPopupMenu popup;

	/**
	 * Create the panel.
	 */
	public ConnectionPanel(MainFrame mainFrame) {
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
		
		JLabel lblDevices = new JLabel("Devices");
		lblDevices.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDevices.setForeground(Style.accent);
		bgrDevices.add(lblDevices, BorderLayout.NORTH);
		
		bgrMenu = new JPanel();
		bgrMenu.setBackground(Style.panelBackground);
		Dimension size = new Dimension(Integer.MAX_VALUE, 200);
		bgrMenu.setPreferredSize(size);
		bgrMenu.setMaximumSize(size);
		add(bgrMenu);
		bgrMenu.setLayout(new BorderLayout(0, 0));

		createPopup();
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
		panelOptions.add(btnCancel);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.setPreferredSize(btnSize);
		UiUtils.configureButton(btnRemove);
		panelOptions.add(btnRemove);
		
		JButton btnSave = new JButton("Save");
		btnSave.setPreferredSize(btnSize);
		UiUtils.configureButton(btnSave);
		panelOptions.add(btnSave);
		
		JButton btnSelect = new JButton("Select");
		btnSelect.setPreferredSize(btnSize);
		UiUtils.configureButton(btnSelect);
		panelOptions.add(btnSelect);
		
		
		if(setup) {
			btnRemove.setVisible(false);
			btnSelect.setVisible(false);
		}
		
		return bgrDeviceSettings;
	}
	
	public void addDeviceButtons(JPanel panel) {
		for(Device d : MainFrame.dm.getDevices()) {
			String icon = "error.png";
			if(d instanceof Arduino) {
				icon = "arduino.png";
			} else if(d instanceof RemoteLightServer) {
				icon = "raspberry.png";
			}
			BigImageButton btn =  new BigImageButton(Style.getUiIcon(icon), d.getId());
			btn.setName(d.getId());
			btn.addMouseListener(deviceClicked);
			panel.add(btn);
		}
		BigImageButton add =  new BigImageButton(Style.getUiIcon("add.png"), "Add");
		add.addMouseListener(addBtnClicked);
		panel.add(add);
	}
	
	private MouseAdapter deviceClicked = new MouseAdapter() {
		@Override
		public void mouseClicked(java.awt.event.MouseEvent e) {
			BigImageButton btn = (BigImageButton) e.getSource();
			for(Device d : MainFrame.dm.getDevices()) {
				if(d.getId().equals(btn.getName())) {
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
			panel = new ArduinoSettingsPanel((Arduino) d);
		} else if(d instanceof RemoteLightServer) {
			panel = new RLServerSettingsPanel((RemoteLightServer) d);
		}
		
		if(panel != null) {
			bgr.add(panel, BorderLayout.CENTER);
			bgrMenu.removeAll();
			bgrMenu.add(bgr, BorderLayout.CENTER);
			bgrMenu.updateUI();
		}
	}
	
	
	private MouseAdapter addBtnClicked = new MouseAdapter() {
		@Override
		public void mouseClicked(java.awt.event.MouseEvent e) {
			showAddPopupMenu();
		}
	};
	
	private void createPopup() {
		popup = new JPopupMenu();
		JMenuItem arduino = new JMenuItem("Arduino", Style.getUiIcon("arduino.png"));
		arduino.setName("arduino");
		arduino.addActionListener(popupMenuListener);
		JMenuItem raspberry = new JMenuItem("RLServer", Style.getUiIcon("raspberry.png"));
		raspberry.setName("raspberry");
		raspberry.addActionListener(popupMenuListener);
		raspberry.setBackground(Style.buttonBackground);
		popup.add(arduino);
		popup.add(raspberry);
		popup.setBackground(Style.buttonBackground);
		popup.setForeground(Style.textColor);
		popup.setVisible(false);
		popup.addPopupMenuListener(popupListener);
	}
	
	private PopupMenuListener popupListener = new PopupMenuListener() {
		
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			System.out.println(1);
		}
		
		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			System.out.println(2);
		}
		
		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			System.out.println(3);
		}
	};
	
	private void showAddPopupMenu() {
		popup.setLocation(MouseInfo.getPointerInfo().getLocation());
		popup.setVisible(true);
		this.updateUI();
	}
	
	private void hideAddPopupMenu() {
		popup.setVisible(false);
	}
	
	private ActionListener popupMenuListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(1);
		}
	};

}
