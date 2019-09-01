package de.lars.remotelightclient.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import javax.swing.BoxLayout;

public class SideMenuExtended extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7722774169681804161L;
	private JPanel sideMenu;
	private MainFrame mainFrame;

	/**
	 * Create the panel.
	 */
	public SideMenuExtended(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.sideMenu = mainFrame.getSideMenu();
		setBackground(Style.panelAccentBackground);
		setPreferredSize(new Dimension(150, 300));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JButton btnExtend = new JButton("");
		btnExtend.setName("extend");
		btnExtend.setIcon(Style.getMenuIcon("menu.png"));
		this.configureButton(btnExtend);
		add(btnExtend);
		
		JButton btnConnection = new JButton("Connection");
		btnConnection.setName("connection");
		btnConnection.setIcon(Style.getMenuIcon("connection.png"));
		this.configureButton(btnConnection);
		add(btnConnection);
		
		JButton btnColors = new JButton("Colors");
		btnColors.setName("colors");
		btnColors.setIcon(Style.getMenuIcon("colors.png"));
		this.configureButton(btnColors);
		add(btnColors);
		
		JButton btnAnimations = new JButton("Animations");
		btnAnimations.setName("animations");
		btnAnimations.setIcon(Style.getMenuIcon("animations.png"));
		this.configureButton(btnAnimations);
		add(btnAnimations);
		
		JButton btnScenes = new JButton("Scenes");
		btnScenes.setName("scenes");
		btnScenes.setIcon(Style.getMenuIcon("scenes.png"));
		this.configureButton(btnScenes);
		add(btnScenes);
		
		JButton btnMusicSync = new JButton("MusicSync");
		btnMusicSync.setName("musicsync");
		btnMusicSync.setIcon(Style.getMenuIcon("musicsync.png"));
		this.configureButton(btnMusicSync);
		add(btnMusicSync);
		
		JButton btnScreenColor = new JButton("ScreenColor");
		btnScreenColor.setName("screencolor");
		btnScreenColor.setIcon(Style.getMenuIcon("screencolor.png"));
		this.configureButton(btnScreenColor);
		add(btnScreenColor);
		
		JButton btnSettings = new JButton("Settings");
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
        btn.setBackground(null);
        btn.setForeground(Style.textColor);
        btn.setMaximumSize(new Dimension(150, 30));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addMouseListener(buttonHoverListener);
        btn.addActionListener(buttonActionListener);
	}
	
	private MouseAdapter buttonHoverListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			btn.setBackground(Style.hoverBackground);
		}
		@Override
		public void mouseExited(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			btn.setBackground(null);
		}
	};
	
	
	private ActionListener buttonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			switch (btn.getName()) {
			case "extend":
				sideMenu.removeAll();
				sideMenu.add(new SideMenuSmall(mainFrame), BorderLayout.CENTER);
				sideMenu.updateUI();
				break;

			default:
				break;
			}
		}
	};

}
