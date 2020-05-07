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
package de.lars.remotelightclient.ui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.ui.panels.about.AboutPanel;
import de.lars.remotelightclient.ui.panels.animations.AnimationsPanel;
import de.lars.remotelightclient.ui.panels.colors.ColorsPanel;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.ui.panels.musicsync.MusicSyncPanel;
import de.lars.remotelightclient.ui.panels.output.OutputPanel;
import de.lars.remotelightclient.ui.panels.scenes.ScenesPanel;
import de.lars.remotelightclient.ui.panels.screencolor.ScreenColorPanel;
import de.lars.remotelightclient.ui.panels.scripts.ScriptsPanel;
import de.lars.remotelightclient.ui.panels.settings.SettingsPanel;
import de.lars.remotelightclient.ui.panels.sidemenu.SideMenuSmall;
import de.lars.remotelightclient.utils.ExceptionHandler;
import de.lars.remotelightclient.utils.ExceptionHandler.ExceptionEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.tinylog.Logger;

public class MainFrame extends JFrame {

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
		
		setSize((Dimension) sm.getSettingObject("mainFrame.size").getValue());
		
		this.setFrameContetPane();
		this.displayPanel(new OutputPanel(this));
		
		if(Main.startParameter.tray) {
			SystemTrayIcon.showTrayIcon();
			dispose();
		}
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
			if(((SettingBoolean) sm.getSettingFromId("ui.hideintray")).getValue() && SystemTray.isSupported()) {
				SystemTrayIcon.showTrayIcon();
				dispose();
			} else {
				try {
					if(displayedPanel != null) {
						displayedPanel.onEnd(null);
					}
					if(getExtendedState() == NORMAL) {
						sm.getSettingObject("mainFrame.size").setValue(getSize());
					}
				} catch(Exception e) {
					Logger.error(e, "Error while closing frame");
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
		case "scripts":
			this.displayPanel(new ScriptsPanel());
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
	
	/** show error dialog on exception */
	public static ExceptionEvent onException = new ExceptionEvent() {
		@Override
		public void onException(Throwable e) {
			JPanel root = new JPanel();
			root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
			
			JLabel header = new JLabel(String.format("A %s Error occured", e.getClass().getCanonicalName()));
			header.setHorizontalAlignment(JLabel.LEFT);
			header.setAlignmentX(Component.LEFT_ALIGNMENT);
			header.setFont(Style.getFontRegualar(12));
			root.add(header);
			
			root.add(Box.createRigidArea(new Dimension(0, 20)));
			
			JTextArea text = new JTextArea(ExceptionHandler.getStackTrace(e));
			text.setLineWrap(true);
			text.setCaretPosition(0);
			text.setEditable(false);
			root.add(new JScrollPane(text));
			
			JOptionPane.showMessageDialog(null, root, "Exception", JOptionPane.ERROR_MESSAGE);
		}
	};

}
