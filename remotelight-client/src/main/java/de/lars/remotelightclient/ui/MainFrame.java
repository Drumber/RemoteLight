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

package de.lars.remotelightclient.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.events.ControlBarEvent;
import de.lars.remotelightclient.events.MenuEvent;
import de.lars.remotelightclient.ui.components.dialogs.ErrorDialog;
import de.lars.remotelightclient.ui.menu.DefaultMenuPanelFactory;
import de.lars.remotelightclient.ui.menu.MenuItem;
import de.lars.remotelightclient.ui.menu.MenuPanelFactory;
import de.lars.remotelightclient.ui.menu.sidemenu.SideMenu;
import de.lars.remotelightclient.ui.menu.sidemenu.SideMenuExtended;
import de.lars.remotelightclient.ui.menu.sidemenu.SideMenuSmall;
import de.lars.remotelightclient.ui.notification.NotificationDisplayHandler;
import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightclient.ui.panels.controlbars.ControlBar;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.notification.listeners.NotificationOptionListener;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingObject;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.utils.ExceptionHandler.ExceptionEvent;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 6397116310182308082L;
	private JPanel contentPane;
	private JPanel bgrSideMenu;
	private JPanel bgrContentPanel;
	private JPanel bgrControlBar;
	private JPanel panelNotification;
	private JLabel lblNotification;
	private JPanel contentArea;
	
	private SideMenu sideMenu;
	private String selectedMenu = "output";
	private MenuPanel displayedPanel;
	private Set<MenuPanelFactory> menuPanelFactories;
	private List<MenuItem> menuItems;
	
	private ControlBar displayedControlBar;
	private SettingsManager sm;
	private NotificationDisplayHandler notificationDisplayHandler;
	private RemoteLightCore core;


	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/resources/Icon-128x128.png")));
		sm = Main.getInstance().getSettingsManager();
		core = Main.getInstance().getCore();
		Main.getInstance().registerFrame(this);
		
		setTitle("RemoteLight" + (RemoteLightCore.DEVBUILD?" [SNAPSHOT]":""));
		setMinimumSize(new Dimension(400, 350));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 850, 440);
		setLocationRelativeTo(null);
		addWindowListener(closeListener);
		
		setSize((Dimension) sm.getSettingObject("mainFrame.size").get());
		
		menuPanelFactories = new HashSet<MenuPanelFactory>();
		menuItems = new ArrayList<MenuItem>();
		// add default menu items
		DefaultMenuPanelFactory.addMenuItems(this);
		// register default menu panel factory
		this.registerMenuPanelFactory(new DefaultMenuPanelFactory(this, sm));
		// init components
		this.setFrameContetPane();
		// show output menu panel as default
		this.showMenuPanel("output");
		
		if(RemoteLightCore.startParameter.tray) {
			SystemTrayIcon.showTrayIcon();
			dispose();
		}
		
		// add notification display handler
		notificationDisplayHandler = new NotificationDisplayHandler(this, core.getNotificationManager());
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
		
		sm.addSetting(new SettingObject("ui.sidemenu.extended", "Extended side menu", false));
		if((boolean) sm.getSettingObject("ui.sidemenu.extended").get())
			sideMenu = new SideMenuExtended(this);
		else
			sideMenu = new SideMenuSmall(this);
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
			if(((SettingBoolean) sm.getSettingFromId("ui.hideintray")).get() && SystemTray.isSupported()) {
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
				
				// start close routine in new thread
				new Thread(new Runnable() {
					@Override
					public void run() {
						// close RemoteLightCore
						core.close(true);
					}
				}, "Shutdown Thread").start();
			}
		}
	};
	
	
	/**
	 * Register a new {@link MenuPanelFactory} that can be queried for {@link MenuPanel}s and
	 * return an instance of the MenuPanel, or {@code null} if that factory does not find
	 * the menu panel for the given name. If {@code null} is returned, the next
	 * MenuPanelFactory (if any) will be asked for the matching MenuPanel. If the
	 * MenuPanel is not found, an error message is output and the old menu panel
	 * remains displayed.
	 * 
	 * @param mpf	the menu panel factory to register
	 */
	public void registerMenuPanelFactory(MenuPanelFactory mpf) {
		menuPanelFactories.add(mpf);
	}
	
	/**
	 * Unregister the MenuPanelFactory from the Set. If the factory is not found in the
	 * set, nothing will happen.
	 * 
	 * @param mpf		the menu panel factory to unregister
	 */
	public void unregisterMenuPanelFactory(MenuPanelFactory mpf) {
		menuPanelFactories.remove(mpf);
	}
	
	/**
	 * Add a {@link MenuItem} that is displayed in the sidebar. It should have valid
	 * information about the targeted MenuPanel.
	 * 
	 * @param item		the menu item to add
	 */
	public void addMenuItem(MenuItem item) {
		menuItems.add(item);
	}
	
	/**
	 * Get all registered MenuItems.
	 * 
	 * @return		a list of menu items
	 */
	public List<MenuItem> getMenuItems() {
		return menuItems;
	}
	
	/**
	 * Remove a {@link MenuItem} from the list. It does not mean that the MenuItem will also disappear
	 * from the sidebar. You have to update the sidebar manually.
	 * 
	 * @param item		the menu item to remove
	 */
	public void removeMenuItem(MenuItem item) {
		menuItems.remove(item);
	}
	
	/**
	 * Show the menu panel corresponding to the name.
	 * 
	 * @param name		the unique name of the menu panel (not case-sensitive)
	 */
	public void showMenuPanel(String name) {
		for(MenuPanelFactory mpf : menuPanelFactories) {
			MenuPanel mp = mpf.getMenuPanel(name.toLowerCase());
			if(mp != null) {
				displayPanel(mp);
				return;
			}
		}
		Logger.error("Could not find menu panel for name the '" + name + "'.");
		Main.getInstance().showNotification(NotificationType.ERROR, "Could not find menu panel for name " + name);
	}
	
	/**
	 * Replace current side menu.
	 * 
	 * @param menu		the new side menu
	 */
	public void replaceSideMenu(SideMenu menu) {
		this.sideMenu = menu;
		bgrSideMenu.removeAll();
		bgrSideMenu.add(menu, BorderLayout.CENTER);
		bgrSideMenu.updateUI();
	}
	
	/**
	 * Get the current side menu.
	 * 
	 * @return		currently displayed side menu
	 */
	public SideMenu getSideMenu() {
		return this.sideMenu;
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
		core.getEventHandler().call(
				new MenuEvent(panel, panel.getName(), displayedPanel == null ? null : displayedPanel.getName()));
		
		this.displayedPanel = panel;
		contentArea.updateUI();
	}
	
	public MenuPanel getDisplayedPanel() {
		return this.displayedPanel;
	}
	
	public NotificationDisplayHandler getNotificationDisplayHandler() {
		return notificationDisplayHandler;
	}
	
	public void showControlBar(boolean enabled) {
		bgrControlBar.setVisible(enabled);
		
		core.getEventHandler().call(new ControlBarEvent.ControlBarVisibleEvent(enabled));
	}
	
	public void setControlBarPanel(ControlBar panel) {
		bgrControlBar.removeAll();
		bgrControlBar.add(panel);
		this.displayedControlBar = panel;
		bgrControlBar.updateUI();
		
		core.getEventHandler().call(new ControlBarEvent.ControlBarChangeEvent(panel));
	}
	
	public ControlBar getDisplayedControlBar() {
		return this.displayedControlBar;
	}
	
	public boolean isControlBarShown() {
		return this.displayedControlBar != null && bgrControlBar.isVisible();
	}
	
	public void updateFrame() {
		this.getContentPane().removeAll();
		this.setFrameContetPane();
		this.revalidate();
		this.repaint();
	}
	
	/** show error dialog on exception */
	public static ExceptionEvent onException = new ExceptionEvent() {
		@Override
		public void onException(Throwable e) {
			MainFrame frame = Main.getInstance().getMainFrame();
			if(frame != null) {
				frame.showErrorNotification(e);
			} else {
				ErrorDialog.showErrorDialog(e);
			}
		}
	};
	
	public void showErrorNotification(Throwable e) {
		Notification notification = new Notification(NotificationType.ERROR,
				"An error has occurred", e.getClass().getCanonicalName() + ": " + e.getMessage());
		notification.setDisplayTime(Notification.LONG);
		notification.setOptions(new String[] {"Details"});
		notification.setOptionListener(new NotificationOptionListener() {
			@Override
			public void onOptionClicked(String option, int index) {
				ErrorDialog.showErrorDialog(e);
			}
		});
		// show notification
		Main.getInstance().getCore().showNotification(notification);
	}

}
