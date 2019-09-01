package de.lars.remotelightclient.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.gui.SystemTrayIcon;
import de.lars.remotelightclient.ui.panels.SideMenuSmall;
import java.awt.Dimension;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6397116310182308082L;
	private JPanel contentPane;
	private JPanel bgrSideMenu;
	private JPanel bgrContentPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
	
	public JPanel getSideMenu() {
		return bgrSideMenu;
	}
	
	public void setContentPanel(JPanel panel) {
		bgrContentPanel.removeAll();
		bgrContentPanel.add(panel, BorderLayout.CENTER);
		bgrContentPanel.updateUI();
	}

}
