package de.lars.remotelightclient.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.lars.remotelightclient.lang.i18n;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.UiUtils;

import javax.swing.Box;
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
		
		JButton btnExtend = new JButton(""); //$NON-NLS-1$
		btnExtend.setName("extend"); //$NON-NLS-1$
		btnExtend.setIcon(Style.getMenuIcon("menu.png")); //$NON-NLS-1$
		this.configureButton(btnExtend);
		add(btnExtend);
		
		JButton btnConnection = new JButton(i18n.getString("Basic.Connection")); //$NON-NLS-1$
		btnConnection.setName("connection"); //$NON-NLS-1$
		btnConnection.setIcon(Style.getMenuIcon("connection.png")); //$NON-NLS-1$
		this.configureButton(btnConnection);
		add(btnConnection);
		
		JButton btnColors = new JButton(i18n.getString("Basic.Colors")); //$NON-NLS-1$
		btnColors.setName("colors"); //$NON-NLS-1$
		btnColors.setIcon(Style.getMenuIcon("colors.png")); //$NON-NLS-1$
		this.configureButton(btnColors);
		add(btnColors);
		
		JButton btnAnimations = new JButton(i18n.getString("Basic.Animations")); //$NON-NLS-1$
		btnAnimations.setName("animations"); //$NON-NLS-1$
		btnAnimations.setIcon(Style.getMenuIcon("animations.png")); //$NON-NLS-1$
		this.configureButton(btnAnimations);
		add(btnAnimations);
		
		JButton btnScenes = new JButton(i18n.getString("Basic.Scenes")); //$NON-NLS-1$
		btnScenes.setName("scenes"); //$NON-NLS-1$
		btnScenes.setIcon(Style.getMenuIcon("scenes.png")); //$NON-NLS-1$
		this.configureButton(btnScenes);
		add(btnScenes);
		
		JButton btnMusicSync = new JButton(i18n.getString("Basic.MusicSync")); //$NON-NLS-1$
		btnMusicSync.setName("musicsync"); //$NON-NLS-1$
		btnMusicSync.setIcon(Style.getMenuIcon("musicsync.png")); //$NON-NLS-1$
		this.configureButton(btnMusicSync);
		add(btnMusicSync);
		
		JButton btnScreenColor = new JButton(i18n.getString("Basic.ScreenColor")); //$NON-NLS-1$
		btnScreenColor.setName("screencolor"); //$NON-NLS-1$
		btnScreenColor.setIcon(Style.getMenuIcon("screencolor.png")); //$NON-NLS-1$
		this.configureButton(btnScreenColor);
		add(btnScreenColor);
		
		JButton btnSettings = new JButton(i18n.getString("Basic.Settings")); //$NON-NLS-1$
		btnSettings.setIcon(Style.getMenuIcon("settings.png")); //$NON-NLS-1$
		btnSettings.setName("settings"); //$NON-NLS-1$
		this.configureButton(btnSettings);
		add(btnSettings);

		Component glue = Box.createGlue();
		add(glue);
		
		JButton btnAbout = new JButton(i18n.getString("Basic.About")); //$NON-NLS-1$
		btnAbout.setIcon(Style.getMenuIcon("info.png")); //$NON-NLS-1$
		btnAbout.setName("about"); //$NON-NLS-1$
		this.configureButton(btnAbout);
		add(btnAbout);
		
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

			if(!btn.getName().equals("extend")) { //$NON-NLS-1$
				UiUtils.getComponentByName(SideMenuExtended.this, new JButton(), mainFrame.getSelectedMenu()).setBackground(null); //reset background of previous selected button
				btn.setBackground(Style.accent);
				mainFrame.setSelectedMenu(btn.getName());
			}
			
			if(btn.getName().equals("extend")) { //$NON-NLS-1$
				sideMenu.removeAll();
				sideMenu.add(new SideMenuSmall(mainFrame), BorderLayout.CENTER);
				sideMenu.updateUI();
			} else {
				mainFrame.menuSelected(btn.getName());
			}
		}
	};

}
