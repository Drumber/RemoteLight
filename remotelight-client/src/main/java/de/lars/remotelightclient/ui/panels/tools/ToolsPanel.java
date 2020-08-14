package de.lars.remotelightclient.ui.panels.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightclient.ui.panels.tools.entrypanels.SettingsEntryPanel;
import de.lars.remotelightclient.utils.ui.UiUtils;

public class ToolsPanel extends MenuPanel {
	private static final long serialVersionUID = -560451815834207862L;

	private MainFrame mainFrame;
	private static List<ToolsPanelEntry> panelEntries = new ArrayList<ToolsPanelEntry>();
	
	private JPanel panelContent;
	private JPanel panelNavigation;
	private JPanel panelEntryList;
	
	private List<ToolsPanelNavItem> navHistory;
	
	static {
		panelEntries.add(new SettingsEntryPanel());
	}
	
	public ToolsPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		navHistory = new ArrayList<ToolsPanelNavItem>();
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout());
		
		panelContent = new JPanel();
		panelContent.setBackground(Style.panelDarkBackground);
		panelContent.setLayout(new BorderLayout());
		
		JScrollPane scrollContent = new JScrollPane(panelContent);
		add(scrollContent, BorderLayout.CENTER);
		
		panelNavigation = new JPanel();
		panelNavigation.setLayout(new GridBagLayout());
		panelNavigation.setBackground(getBackground());
		panelNavigation.setVisible(false);
		add(panelNavigation, BorderLayout.NORTH);
		
		panelEntryList = new JPanel();
		panelEntryList.setLayout(new BoxLayout(panelEntryList, BoxLayout.Y_AXIS));
		panelEntryList.setBackground(panelContent.getBackground());
		
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
		}
	}
	
	protected void processEntryItemClick(ToolsPanelEntry entry) {
		entry.onClick();
		if(entry.getMenuPanel() != null) {
			// show menu panel
			ToolsPanelNavItem navItem = new ToolsPanelNavItem(entry.getName(), entry.getMenuPanel());
			navigateUp(navItem);
		}
	}
	
	protected JPanel getEntryItemPanel(ToolsPanelEntry entry) {
		JPanel panel = new JPanel();
		panel.setBackground(Style.buttonBackground);
		panel.addMouseListener(entryItemMouseListener);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel lblIcon = new JLabel();
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
		
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		return panel;
	}
	
	MouseAdapter entryItemMouseListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			((JComponent) e.getSource()).setBackground(Style.hoverBackground);
		}
		@Override
		public void mouseExited(MouseEvent e) {
			((JComponent) e.getSource()).setBackground(Style.buttonBackground);
		}
	};
	
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
		JButton btnBack = new JButton("Back");
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
	}
	
	
	protected void showNavPanel(ToolsPanelNavItem navItem) {
		showContent(navItem.getContent());
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
			navHistory.remove(navHistory.size()-1);
			if(navHistory.size() == 0) {
				showToolsOverview();
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
		return "Tools";
	}

}
