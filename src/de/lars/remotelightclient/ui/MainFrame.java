package de.lars.remotelightclient.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.ui.panels.about.AboutPanel;
import de.lars.remotelightclient.ui.panels.animations.AnimationsPanel;
import de.lars.remotelightclient.ui.panels.colors.ColorsPanel;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.ui.panels.musicsync.MusicSyncPanel;
import de.lars.remotelightclient.ui.panels.output.OutputPanel;
import de.lars.remotelightclient.ui.panels.scenes.ScenesPanel;
import de.lars.remotelightclient.ui.panels.screencolor.ScreenColorPanel;
import de.lars.remotelightclient.ui.panels.settings.SettingsPanel;
import de.lars.remotelightclient.ui.panels.sidemenu.SideMenuSmall;

import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.border.EmptyBorder;

import org.tinylog.Logger;
import java.awt.Toolkit;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6397116310182308082L;
	private JPanel contentPane;
	private JPanel bgrSideMenu;
	private JPanel bgrContentPanel;
	private JPanel bgrControlBar;
	private JPanel panelNotification;
	private JLabel lblNotification;
	private JPanel contentArea;
	
	private String selectedMenu = "output";
	private MenuPanel displayedPanel;
	private JPanel displayedControlBar;
	private SettingsManager sm;


	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/resourcen/Icon-128x128.png")));
		sm = Main.getInstance().getSettingsManager();
		setTitle("RemoteLight");
		setMinimumSize(new Dimension(400, 350));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 850, 440);
		setLocationRelativeTo(null);
		addWindowListener(closeListener);
		
		this.setFrameContetPane();
		this.displayPanel(new OutputPanel(this));
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
		bgrControlBar.setBorder(new EmptyBorder(2, 0, 0, 0));
		bgrContentPanel.add(bgrControlBar, BorderLayout.SOUTH);
		bgrControlBar.setBackground(Style.accent);
		bgrControlBar.setLayout(new BorderLayout(0, 0));
		this.setControlBarPanel(new DefaultControlBar());
	}
	
	
	WindowListener closeListener = new WindowAdapter() {
		public void windowClosing(WindowEvent windowEvent) {
			if(DataStorage.isStored(DataStorage.SETTINGS_HIDE) && (boolean) DataStorage.getData(DataStorage.SETTINGS_HIDE)) {
				SystemTrayIcon.showTrayIcon();
				dispose();
			} else {
				if(displayedPanel != null) {
					displayedPanel.onEnd(null);
				}
				Main.getInstance().close(true);
			}
		}
	};
	
	public JPanel getSideMenu() {
		return bgrSideMenu;
	}
	
	public String getSelectedMenu() {
		return selectedMenu;
	}
	
	public void setSelectedMenu(String menu) {
		selectedMenu = menu.toLowerCase();
	}
	
	public void displayPanel(MenuPanel panel) {
		if(this.displayedPanel != null) {
			this.displayedPanel.onEnd(panel);
		}
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
	
	public void setControlBarPanel(JPanel panel) {
		bgrControlBar.removeAll();
		bgrControlBar.add(panel);
		this.displayedControlBar = panel;
		bgrControlBar.updateUI();
	}
	
	public JPanel getDisplayedControlBar() {
		return this.displayedControlBar;
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
		case "output":
			this.displayPanel(new OutputPanel(this));
			break;
		case "colors":
			this.displayPanel(new ColorsPanel());
			break;
		case "animations":
			this.displayPanel(new AnimationsPanel());
			break;
		case "scenes":
			this.displayPanel(new ScenesPanel());
			break;
		case "musicsync":
			this.displayPanel(new MusicSyncPanel());
			break;
		case "screencolor":
			this.displayPanel(new ScreenColorPanel());
			break;
		case "about":
			this.displayPanel(new AboutPanel());
			break;
			
		default:
			Logger.info("MenuPanel '" + menu + "' not found!");
		}
	}
	
	public enum NotificationType {
		Error, Info, Unimportant, Success
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
		case Success:
			lblNotification.setForeground(Color.GREEN);
			break;
		}
	}

}
