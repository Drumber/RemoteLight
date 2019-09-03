package de.lars.remotelightclient.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.UiUtils;

import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;

public class SideMenuSmall extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1604913473609403672L;
	private JPanel sideMenu;
	private MainFrame mainFrame;

	/**
	 * Create the panel.
	 */
	public SideMenuSmall(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.sideMenu = mainFrame.getSideMenu();
		setBackground(Style.panelAccentBackground);
		setPreferredSize(new Dimension(40, 300));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JButton btnExtend = new JButton("");
		btnExtend.setName("extend");
		btnExtend.setIcon(Style.getMenuIcon("menu.png"));
		this.configureButton(btnExtend);
		add(btnExtend);
		
		JButton btnConnection = new JButton("");
		btnConnection.setName("connection");
		btnConnection.setIcon(Style.getMenuIcon("connection.png"));
		this.configureButton(btnConnection);
		add(btnConnection);
		
		JButton btnColors = new JButton("");
		btnColors.setName("colors");
		btnColors.setIcon(Style.getMenuIcon("colors.png"));
		this.configureButton(btnColors);
		add(btnColors);
		
		JButton btnAnimations = new JButton("");
		btnAnimations.setName("animations");
		btnAnimations.setIcon(Style.getMenuIcon("animations.png"));
		this.configureButton(btnAnimations);
		add(btnAnimations);
		
		JButton btnScenes = new JButton("");
		btnScenes.setName("scenes");
		btnScenes.setIcon(Style.getMenuIcon("scenes.png"));
		this.configureButton(btnScenes);
		add(btnScenes);
		
		JButton btnMusicSync = new JButton("");
		btnMusicSync.setName("musicsync");
		btnMusicSync.setIcon(Style.getMenuIcon("musicsync.png"));
		this.configureButton(btnMusicSync);
		add(btnMusicSync);
		
		JButton btnScreenColor = new JButton("");
		btnScreenColor.setName("screencolor");
		btnScreenColor.setIcon(Style.getMenuIcon("screencolor.png"));
		this.configureButton(btnScreenColor);
		add(btnScreenColor);
		
		JButton btnSettings = new JButton("");
		btnSettings.setIcon(Style.getMenuIcon("settings.png"));
		btnSettings.setName("settings");
		this.configureButton(btnSettings);
		add(btnSettings);

	}
	
	
	private void configureButton(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setBackground(null);
        btn.addMouseListener(buttonHoverListener);
        btn.addActionListener(buttonActionListener);
        if(mainFrame.getSelectedMenu().equals(btn.getName())) {
        	btn.setBackground(Style.accent);
        }
	}
	
	private MouseAdapter buttonHoverListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			if(!mainFrame.getSelectedMenu().equals(btn.getName())) {
				btn.setBackground(Style.hoverBackground);
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			if(!mainFrame.getSelectedMenu().equals(btn.getName())) {
				btn.setBackground(null);
			}
		}
	};
	
	
	private ActionListener buttonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			
			if(!btn.getName().equals("extend")) {
				UiUtils.getComponentByName(SideMenuSmall.this, new JButton(), mainFrame.getSelectedMenu()).setBackground(null); //reset background of previous selected button
				btn.setBackground(Style.accent);
				mainFrame.setSelectedMenu(btn.getName());
			}
			
			if(btn.getName().equals("extend")) {
				sideMenu.removeAll();
				sideMenu.add(new SideMenuExtended(mainFrame), BorderLayout.CENTER);
				sideMenu.updateUI();
			} else {
				mainFrame.menuSelected(btn.getName());
			}
		}
	};
	

}
