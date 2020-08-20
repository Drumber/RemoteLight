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

package de.lars.remotelightclient.ui.panels.tools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.ui.panels.tools.entrypanels.ConsoleEntryPanel;
import de.lars.remotelightclient.ui.panels.tools.entrypanels.NotificationEntryPanel;
import de.lars.remotelightclient.ui.panels.tools.entrypanels.PluginsEntryPanel;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.lang.i18n;

public class ToolsPanel extends MenuPanel {
	private static final long serialVersionUID = -560451815834207862L;

	private static List<ToolsPanelEntry> panelEntries = new ArrayList<ToolsPanelEntry>();
	private MainFrame mainFrame;
	
	private JPanel panelContent;
	private JPanel panelNavigation;
	private JPanel panelEntryList;
	
	private ToolsPanelEntry currentToolsPanel;
	private List<ToolsPanelNavItem> navHistory;
	
	static {
		panelEntries.add(new ConsoleEntryPanel());
		panelEntries.add(new PluginsEntryPanel());
		panelEntries.add(new NotificationEntryPanel());
	}
	
	public ToolsPanel() {
		navHistory = new ArrayList<ToolsPanelNavItem>();
		mainFrame = Main.getInstance().getMainFrame();
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout());
		mainFrame.showControlBar(true);
		mainFrame.setControlBarPanel(new DefaultControlBar());
		
		panelContent = new JPanel();
		panelContent.setBackground(Style.panelBackground);
		panelContent.setLayout(new BorderLayout());
		
		JScrollPane scrollContent = new JScrollPane(panelContent);
		scrollContent.setViewportBorder(null);
		scrollContent.setBorder(BorderFactory.createEmptyBorder());
		scrollContent.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollContent.getVerticalScrollBar().setUnitIncrement(8);
		add(scrollContent, BorderLayout.CENTER);
		
		panelNavigation = new JPanel();
		panelNavigation.setLayout(new GridBagLayout());
		panelNavigation.setBackground(getBackground());
		panelNavigation.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		panelNavigation.setVisible(false);
		add(panelNavigation, BorderLayout.NORTH);
		
		panelEntryList = new JPanel();
		panelEntryList.setLayout(new BoxLayout(panelEntryList, BoxLayout.Y_AXIS));
		panelEntryList.setBackground(Style.panelDarkBackground);
		panelEntryList.setBorder(new CompoundBorder(
				BorderFactory.createLineBorder(panelContent.getBackground(), 10),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		initEntryListPanel();
		showToolsOverview();
	}
	
	
	protected void initEntryListPanel() {
		panelEntryList.removeAll();
		
		for(ToolsPanelEntry entry : panelEntries) {
			JPanel panel = getEntryItemPanel(entry);
			panelEntryList.add(panel);
			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					processEntryItemClick(entry);
				}
			});
			panelEntryList.add(Box.createVerticalStrut(5));
		}
	}
	
	protected void processEntryItemClick(ToolsPanelEntry entry) {
		entry.onClick();
		JPanel menuPanel = entry.getMenuPanel(this);
		if(menuPanel != null) {
			// show menu panel
			ToolsPanelNavItem navItem = new ToolsPanelNavItem(entry.getName(), menuPanel);
			navigateUp(navItem);
			currentToolsPanel = entry;
		}
	}
	
	protected JPanel getEntryItemPanel(ToolsPanelEntry entry) {
		JPanel panel = new JPanel();
		panel.setBackground(Style.buttonBackground);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(new LineBorder(Style.textColor));
		UiUtils.addHoverColor(panel, Style.buttonBackground, Style.hoverBackground);
		
		JLabel lblIcon = new JLabel();
		lblIcon.setForeground(Style.textColor);
		lblIcon.setMaximumSize(new Dimension(30, 30));
		if(entry.getIcon() != null)
			lblIcon.setIcon(entry.getIcon());
		panel.add(lblIcon);
		
		JLabel lblName = new JLabel(entry.getName());
		lblName.setForeground(Style.textColor);
		panel.add(lblName);
		
		if(entry.getActionButtons() != null) {
			JPanel panelBtns = new JPanel();
			panelBtns.setBackground(panel.getBackground());
			for(JButton btn : entry.getActionButtons()) {
				UiUtils.configureButton(btn);
				panelBtns.add(btn);
			}
			
			panel.add(Box.createHorizontalGlue());
			panel.add(panelBtns);
		}
		
		Dimension size = new Dimension(Integer.MAX_VALUE, 50);
		panel.setMaximumSize(size);
		panel.setMinimumSize(size);
		return panel;
	}
	
	public void showToolsOverview() {
		panelNavigation.setVisible(false);
		navHistory.clear();
		showContent(panelEntryList);
	}
	
	
	/**
	 * Replace the current shown panel in the content panel.
	 * @param panel		the new panel to show
	 */
	protected void showContent(JPanel panel) {
		panelContent.removeAll();
		panelContent.add(panel, BorderLayout.CENTER);
		panelContent.updateUI();
	}
	
	protected void showTitle(String title, boolean backVisible) {
		panelNavigation.removeAll();
		panelNavigation.setVisible(true);
		JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
		lblTitle.setForeground(Style.textColor);
		JButton btnBack = new JButton("Back");
		UiUtils.configureButton(btnBack);
		btnBack.setPreferredSize(new Dimension(80, 25));
		btnBack.addActionListener(e -> navigateDown());
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 0, 5);
		c.gridy = 0;
		c.gridx = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		if(backVisible)
			panelNavigation.add(btnBack, c);
		
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		panelNavigation.add(lblTitle, c);
		
		c.gridx = 2;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panelNavigation.add(Box.createHorizontalStrut(btnBack.getPreferredSize().width), c);
		panelNavigation.updateUI();
	}
	
	
	protected void showNavPanel(ToolsPanelNavItem navItem) {
		showContent(navItem.getContent());
		if(navItem.getListener() != null)
			navItem.getListener().onShow();
		showTitle(navItem.getTitle(), true);
	}
	
	/**
	 * Navigate up one level.
	 * @param navItem	navigation item to show
	 */
	public void navigateUp(ToolsPanelNavItem navItem) {
		navHistory.add(navItem);
		showNavPanel(navItem);
	}
	
	/**
	 * Navigate down one level.
	 * <p>Will show the tools entry list if there is
	 * no further level down.
	 */
	public void navigateDown() {
		if(navHistory.size() > 0) {
			ToolsPanelNavItem prevItem = navHistory.remove(navHistory.size()-1);
			if(prevItem.getListener() != null)
				prevItem.getListener().onBack();
			if(navHistory.size() == 0) {
				showToolsOverview();
				if(currentToolsPanel != null)
					currentToolsPanel.onEnd();
				currentToolsPanel = null;
			} else {
				showNavPanel(navHistory.get(navHistory.size()-1));
			}
		}
	}
	
	
	/**
	 * Get the list of registered panel entries.
	 * @return		the list of entries
	 */
	public static synchronized List<ToolsPanelEntry> getEntryList() {
		return panelEntries;
	}

	@Override
	public String getName() {
		return i18n.getString("Basic.Tools");
	}
	
	@Override
	public void onEnd(MenuPanel newPanel) {
		if(currentToolsPanel != null)
			currentToolsPanel.onEnd();
	}

}
