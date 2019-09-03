package de.lars.remotelightclient.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.devices.DeviceManager;
import de.lars.remotelightclient.devices.arduino.Arduino;
import de.lars.remotelightclient.devices.remotelightserver.RemoteLightServer;
import de.lars.remotelightclient.gui.SystemTrayIcon;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.ui.panels.ConnectionPanel;
import de.lars.remotelightclient.ui.panels.SettingsPanel;
import de.lars.remotelightclient.ui.panels.SideMenuSmall;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.FlowLayout;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6397116310182308082L;
	private JPanel contentPane;
	private JPanel bgrSideMenu;
	private JPanel bgrContentPanel;
	
	private String selectedMenu = "connection";
	private JPanel displayedPanel;

	//TEMP
	public static SettingsManager sm;
	public static DeviceManager dm;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		sm = new SettingsManager();
		dm = new DeviceManager();
		dm.addDevice(new Arduino("Arduino1", null));
		dm.addDevice(new RemoteLightServer("RaspberryPi", null));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) { e.printStackTrace(); }
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("RemoteLight");
		setMinimumSize(new Dimension(400, 350));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 836, 391);
		
		this.setFrameContetPane();
	}
	
	private void setFrameContetPane() {
		SettingSelection style = (SettingSelection) sm.getSettingFromId("ui.style");
		Style.setStyle(style.getSelected());
		
		contentPane = new JPanel();
		contentPane.setBackground(Style.panelBackground);
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		bgrSideMenu = new JPanel();
		bgrSideMenu.setLayout(new BorderLayout(0, 0));
		contentPane.add(bgrSideMenu, BorderLayout.WEST);
		
		JPanel sideMenu = new SideMenuSmall(this);
		bgrSideMenu.add(sideMenu, BorderLayout.CENTER);
		
		bgrContentPanel = new JPanel();
		bgrContentPanel.setBackground(Style.panelBackground);
		contentPane.add(bgrContentPanel, BorderLayout.CENTER);
		bgrContentPanel.setLayout(new BorderLayout(0, 0));
		
		panelNotification = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelNotification.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelNotification.setBackground(Style.panelBackground);
		bgrContentPanel.add(panelNotification, BorderLayout.NORTH);
		
		lblNotification = new JLabel("");
		panelNotification.add(lblNotification);
		
		contentArea = new JPanel();
		contentArea.setBackground(Style.panelBackground);
		bgrContentPanel.add(contentArea, BorderLayout.CENTER);
		contentArea.setLayout(new BorderLayout(0, 0));
		
		bgrControlBar = new JPanel();
		bgrControlBar.setBackground(Style.panelDarkBackground);
		contentPane.add(bgrControlBar, BorderLayout.SOUTH);
	}
	
	
	WindowListener closeListener = new WindowAdapter() {
		public void windowClosing(WindowEvent windowEvent) {
			if((boolean) DataStorage.getData(DataStorage.SETTINGS_HIDE)) {
				SystemTrayIcon.showTrayIcon();
				dispose();
			} else {
				System.exit(0);
			}
		}
	};
	private JPanel bgrControlBar;
	private JPanel panelNotification;
	private JLabel lblNotification;
	private JPanel contentArea;
	
	public JPanel getSideMenu() {
		return bgrSideMenu;
	}
	
	public String getSelectedMenu() {
		return selectedMenu;
	}
	
	public void setSelectedMenu(String menu) {
		selectedMenu = menu.toLowerCase();
	}
	
	public void displayPanel(JPanel panel) {
		contentArea.removeAll();
		contentArea.add(panel, BorderLayout.CENTER);
		this.displayedPanel = panel;
		contentArea.updateUI();
	}
	
	public JPanel getDisplayedPanel() {
		return this.displayedPanel;
	}
	
	public void showControlBar(boolean enabled) {
		bgrControlBar.setVisible(enabled);
	}
	
	public void updateFrame() {
		this.getContentPane().removeAll();
		this.setFrameContetPane();
		this.revalidate();
		this.repaint();
	}
	
	public void menuSelected(String menu) {
		printNotification("", NotificationType.Unimportant);
		switch(menu.toLowerCase())
		{
		case "settings":
			this.displayPanel(new SettingsPanel(this, sm));
			break;
		case "connection":
			this.displayPanel(new ConnectionPanel(this));
			break;
		}
	}
	
	public enum NotificationType {
		Error, Info, Unimportant
	}
	public void printNotification(String text, NotificationType type) {
		if(text == null || text.equals("")) {
			panelNotification.setVisible(false);
			return;
		}
		panelNotification.setVisible(true);
		lblNotification.setText(text);
		switch (type) {
		case Error:
			lblNotification.setForeground(Color.RED);
			break;
		case Info:
			lblNotification.setForeground(Style.accent);
			break;
		case Unimportant:
			lblNotification.setForeground(Style.textColor);
			break;
		}
	}

}
