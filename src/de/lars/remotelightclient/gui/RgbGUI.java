package de.lars.remotelightclient.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Cursor;
import java.awt.Desktop;

import javax.swing.event.ChangeListener;

import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicSyncOld;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;
import de.lars.remotelightclient.screencolor.LEDScreenColor;

import javax.swing.event.ChangeEvent;
import java.awt.FlowLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.border.LineBorder;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

public class RgbGUI extends JFrame {
	
	private final int widthL = 50, heightL = 200;
	private final int widthR = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 50), heightR = 200;
	private int animationSpeed = 50;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5268393542712389384L;
	private MusicSyncOld musicSyncOld;
	private JPanel contentPane;
	private JTextField fieldServerIP;
	private JLabel lblStatus, lblColorRight, lblColorLeft;
	private JButton btnConnect;
	JPanel panelScreenColorLeft, panelScreenColorRight;
	JPanel panelMusicSyncLeft, panelMusicSyncRight;
	

	/**
	 * Create the frame.
	 */
	public RgbGUI() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(RgbGUI.class.getResource("/resourcen/Icon-128x128.png")));
		setResizable(false);
		setTitle("RGB Desk Lamp Client");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 260, 535);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("Menu");
		menuBar.add(menu);
		
		JMenuItem menuItem = new JMenuItem("Settings");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getInstance().openSettingsGui();
			}
		});
		menu.add(menuItem);
		
		JMenuItem menuItem_1 = new JMenuItem("WS281x");
		menuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getInstance().openWS281xGui();
			}
		});
		menu.add(menuItem_1);
		
		JMenuItem menuItem_2 = new JMenuItem("Exit");
		menuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(menuItem_2);
		
		JMenu menu_1 = new JMenu("Help");
		menuBar.add(menu_1);
		
		JMenuItem mntmWebsite = new JMenuItem("Website");
		mntmWebsite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(Main.WEBSITE));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		menu_1.add(mntmWebsite);
		
		JMenuItem mntmGithub = new JMenuItem("GitHub");
		mntmGithub.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(Main.GITHUB));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		menu_1.add(mntmGithub);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblHostnamaIp = new JLabel("Hostnama / IP");
		lblHostnamaIp.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblHostnamaIp.setBounds(10, 21, 97, 14);
		contentPane.add(lblHostnamaIp);
		
		fieldServerIP = new JTextField();
		fieldServerIP.setBounds(10, 35, 125, 20);
		contentPane.add(fieldServerIP);
		fieldServerIP.setColumns(10);
		fieldServerIP.setText((String) DataStorage.getData(DataStorage.IP_STOREKEY));
		
		btnConnect = new JButton("Connect");
		btnConnect.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Connect Button Event
				if(!Client.isConnected()) {
					DataStorage.store(DataStorage.IP_STOREKEY, fieldServerIP.getText());
					Client.connect(fieldServerIP.getText());
				} else {
					Client.disconnect();
				}
			}
		});
		btnConnect.setFocusable(false);
		btnConnect.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		btnConnect.setBounds(145, 34, 89, 23);
		contentPane.add(btnConnect);
		
		lblStatus = new JLabel("");
		lblStatus.setForeground(Color.RED);
		lblStatus.setFont(new Font("Source Sans Pro", Font.PLAIN, 10));
		lblStatus.setBounds(10, 66, 224, 14);
		contentPane.add(lblStatus);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 96, 224, 14);
		contentPane.add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 283, 224, 14);
		contentPane.add(separator_1);
		
		JTabbedPane tabbedPaneColors = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneColors.setFocusable(false);
		tabbedPaneColors.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		tabbedPaneColors.setBounds(10, 102, 224, 170);
		contentPane.add(tabbedPaneColors);
		
		JPanel panelRGBMixer = new JPanel();
		tabbedPaneColors.addTab("RGB Mixer", null, panelRGBMixer, null);
		panelRGBMixer.setLayout(null);
		
		JLabel lblRed = new JLabel("Red");
		lblRed.setBounds(9, 19, 20, 16);
		panelRGBMixer.add(lblRed);
		lblRed.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		
		JSlider sliderRed = new JSlider();
		sliderRed.setBounds(9, 31, 200, 26);
		panelRGBMixer.add(sliderRed);
		sliderRed.setFocusable(false);
		sliderRed.setValue(0);
		
		JLabel lblGreen = new JLabel("Green");
		lblGreen.setBounds(9, 56, 30, 16);
		panelRGBMixer.add(lblGreen);
		lblGreen.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		
		JSlider sliderGreen = new JSlider();
		sliderGreen.setBounds(9, 68, 200, 26);
		panelRGBMixer.add(sliderGreen);
		sliderGreen.setFocusable(false);
		sliderGreen.setValue(0);
		
		JLabel lblBlue = new JLabel("Blue");
		lblBlue.setBounds(9, 93, 23, 16);
		panelRGBMixer.add(lblBlue);
		lblBlue.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		
		JSlider sliderBlue = new JSlider();
		sliderBlue.setBounds(9, 105, 200, 26);
		panelRGBMixer.add(sliderBlue);
		sliderBlue.setFocusable(false);
		sliderBlue.setValue(0);
		
		JPanel panelColors = new JPanel();
		tabbedPaneColors.addTab("Colors", null, panelColors, null);
		
		JPanel c1 = new JPanel();
		c1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c1.setFocusTraversalPolicyProvider(true);
		c1.setFocusCycleRoot(true);
		c1.setBorder(new LineBorder(new Color(0, 0, 0)));
		c1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c1.getBackground());
			}
		});
		c1.setBackground(Color.RED);
		
		JPanel c7 = new JPanel();
		c7.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c7.setFocusTraversalPolicyProvider(true);
		c7.setFocusCycleRoot(true);
		c7.setBorder(new LineBorder(new Color(0, 0, 0)));
		c7.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c7.getBackground());
			}
		});
		c7.setBackground(Color.CYAN);
		
		JPanel c13 = new JPanel();
		c13.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c13.setFocusTraversalPolicyProvider(true);
		c13.setFocusCycleRoot(true);
		c13.setBorder(new LineBorder(new Color(0, 0, 0)));
		c13.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c13.getBackground());
			}
		});
		c13.setBackground(Color.WHITE);
		
		JPanel c2 = new JPanel();
		c2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c2.setFocusTraversalPolicyProvider(true);
		c2.setFocusCycleRoot(true);
		c2.setBorder(new LineBorder(new Color(0, 0, 0)));
		c2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c2.getBackground());
			}
		});
		c2.setBackground(Color.GREEN);
		
		JPanel c8 = new JPanel();
		c8.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c8.setFocusTraversalPolicyProvider(true);
		c8.setFocusCycleRoot(true);
		c8.setBorder(new LineBorder(new Color(0, 0, 0)));
		c8.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c8.getBackground());
			}
		});
		c8.setBackground(new Color(0, 139, 139));
		
		JPanel c14 = new JPanel();
		c14.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c14.setFocusTraversalPolicyProvider(true);
		c14.setFocusCycleRoot(true);
		c14.setBorder(new LineBorder(new Color(0, 0, 0)));
		c14.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c14.getBackground());
			}
		});
		c14.setBackground(new Color(50, 205, 50));
		
		JPanel c3 = new JPanel();
		c3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c3.setFocusTraversalPolicyProvider(true);
		c3.setFocusCycleRoot(true);
		c3.setBorder(new LineBorder(new Color(0, 0, 0)));
		c3.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c3.getBackground());
			}
		});
		c3.setBackground(Color.BLUE);
		
		JPanel c4 = new JPanel();
		c4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c4.setFocusTraversalPolicyProvider(true);
		c4.setFocusCycleRoot(true);
		c4.setBorder(new LineBorder(new Color(0, 0, 0)));
		c4.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c4.getBackground());
			}
		});
		c4.setBackground(Color.YELLOW);
		
		JPanel c10 = new JPanel();
		c10.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c10.setFocusTraversalPolicyProvider(true);
		c10.setFocusCycleRoot(true);
		c10.setBorder(new LineBorder(new Color(0, 0, 0)));
		c10.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c10.getBackground());
			}
		});
		c10.setBackground(new Color(255, 69, 0));
		
		JPanel c9 = new JPanel();
		c9.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c9.setFocusTraversalPolicyProvider(true);
		c9.setFocusCycleRoot(true);
		c9.setBorder(new LineBorder(new Color(0, 0, 0)));
		c9.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c9.getBackground());
			}
		});
		c9.setBackground(new Color(25, 25, 112));
		
		JPanel c15 = new JPanel();
		c15.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c15.setFocusTraversalPolicyProvider(true);
		c15.setFocusCycleRoot(true);
		c15.setBorder(new LineBorder(new Color(0, 0, 0)));
		c15.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c15.getBackground());
			}
		});
		c15.setBackground(new Color(34, 139, 34));
		
		JPanel c16 = new JPanel();
		c16.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c16.setFocusTraversalPolicyProvider(true);
		c16.setFocusCycleRoot(true);
		c16.setBorder(new LineBorder(new Color(0, 0, 0)));
		c16.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c16.getBackground());
			}
		});
		c16.setBackground(new Color(0, 100, 0));
		
		JPanel c5 = new JPanel();
		c5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c5.setFocusTraversalPolicyProvider(true);
		c5.setFocusCycleRoot(true);
		c5.setBorder(new LineBorder(new Color(0, 0, 0)));
		c5.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c5.getBackground());
			}
		});
		c5.setBackground(Color.ORANGE);
		
		JPanel c6 = new JPanel();
		c6.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c6.setFocusTraversalPolicyProvider(true);
		c6.setFocusCycleRoot(true);
		c6.setBorder(new LineBorder(new Color(0, 0, 0)));
		c6.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c6.getBackground());
			}
		});
		c6.setBackground(Color.MAGENTA);
		
		JPanel c12 = new JPanel();
		c12.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c12.setFocusTraversalPolicyProvider(true);
		c12.setFocusCycleRoot(true);
		c12.setBorder(new LineBorder(new Color(0, 0, 0)));
		c12.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c12.getBackground());
			}
		});
		c12.setBackground(new Color(128, 0, 128));
		
		JPanel c11 = new JPanel();
		c11.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c11.setFocusTraversalPolicyProvider(true);
		c11.setFocusCycleRoot(true);
		c11.setBorder(new LineBorder(new Color(0, 0, 0)));
		c11.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c11.getBackground());
			}
		});
		c11.setBackground(new Color(139, 0, 0));
		
		JPanel c17 = new JPanel();
		c17.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c17.setFocusTraversalPolicyProvider(true);
		c17.setFocusCycleRoot(true);
		c17.setBorder(new LineBorder(new Color(0, 0, 0)));
		c17.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c17.getBackground());
			}
		});
		c17.setBackground(new Color(46, 139, 87));
		
		JPanel c18 = new JPanel();
		c18.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c18.setFocusTraversalPolicyProvider(true);
		c18.setFocusCycleRoot(true);
		c18.setBorder(new LineBorder(new Color(0, 0, 0)));
		c18.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendColor(c18.getBackground());
			}
		});
		c18.setBackground(new Color(0, 255, 127));
		GroupLayout gl_panelColors = new GroupLayout(panelColors);
		gl_panelColors.setHorizontalGroup(
			gl_panelColors.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelColors.createSequentialGroup()
					.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
						.addComponent(c1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(c7, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(c13, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
						.addComponent(c2, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(c8, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(c14, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelColors.createSequentialGroup()
							.addComponent(c3, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(c4, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelColors.createSequentialGroup()
							.addComponent(c9, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(c10, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelColors.createSequentialGroup()
							.addComponent(c15, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(c16, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelColors.createSequentialGroup()
							.addComponent(c5, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(c6, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelColors.createSequentialGroup()
							.addComponent(c11, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(c12, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelColors.createSequentialGroup()
							.addComponent(c17, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(c18, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panelColors.setVerticalGroup(
			gl_panelColors.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelColors.createSequentialGroup()
					.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panelColors.createSequentialGroup()
								.addComponent(c2, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addGap(6)
								.addComponent(c8, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addGap(6)
								.addComponent(c14, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_panelColors.createSequentialGroup()
								.addComponent(c1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(c7, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(c13, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panelColors.createSequentialGroup()
							.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
								.addComponent(c3, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c4, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
							.addGap(6)
							.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
								.addComponent(c9, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c10, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
							.addGap(6)
							.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
								.addComponent(c15, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c16, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panelColors.createSequentialGroup()
							.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
								.addComponent(c5, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c6, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
							.addGap(6)
							.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
								.addComponent(c11, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c12, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
							.addGap(6)
							.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
								.addComponent(c17, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c18, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(37, Short.MAX_VALUE))
		);
		panelColors.setLayout(gl_panelColors);
		
		JPanel panelScenes = new JPanel();
		tabbedPaneColors.addTab("Scenes", null, panelScenes, null);
		panelScenes.setLayout(null);
		
		JButton btnNordlichter = new JButton("Northern lights");
		btnNordlichter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.COLOR_LEFT, 0+"", 100+"", 100+""});
				Client.send(new String[] {Identifier.COLOR_RIGHT, 0+"", 100+"", 36+""});
			}
		});
		btnNordlichter.setBounds(10, 11, 89, 23);
		panelScenes.add(btnNordlichter);
		
		JButton btnSunset = new JButton("Sunset");
		btnSunset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.COLOR_LEFT, 100+"", 30+"", 0+""});
				Client.send(new String[] {Identifier.COLOR_RIGHT, 100+"", 100+"", 0+""});
			}
		});
		btnSunset.setBounds(120, 11, 89, 23);
		panelScenes.add(btnSunset);
		
		JButton btnSea = new JButton("Sea");
		btnSea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.COLOR_LEFT, 0+"", 50+"", 100+""});
				Client.send(new String[] {Identifier.COLOR_RIGHT, 0+"", 10+"", 100+""});
			}
		});
		btnSea.setBounds(10, 45, 89, 23);
		panelScenes.add(btnSea);
		
		JButton btnFire = new JButton("Fire");
		btnFire.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.COLOR_LEFT, 100+"", 0+"", 0+""});
				Client.send(new String[] {Identifier.COLOR_RIGHT, 100+"", 30+"", 0+""});
			}
		});
		btnFire.setBounds(120, 45, 89, 23);
		panelScenes.add(btnFire);
		
		JButton btnJungle = new JButton("Jungle");
		btnJungle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.COLOR_LEFT, 30+"", 100+"", 0+""});
				Client.send(new String[] {Identifier.COLOR_RIGHT, 50+"", 100+"", 15+""});
			}
		});
		btnJungle.setBounds(10, 79, 89, 23);
		panelScenes.add(btnJungle);
		
		JButton btnSpace = new JButton("Space");
		btnSpace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.COLOR_LEFT, 40+"", 0+"", 60+""});
				Client.send(new String[] {Identifier.COLOR_RIGHT, 30+"", 20+"", 100+""});
			}
		});
		btnSpace.setBounds(120, 79, 89, 23);
		panelScenes.add(btnSpace);
		
		JTabbedPane tabbedPaneEffects = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneEffects.setFocusable(false);
		tabbedPaneEffects.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		tabbedPaneEffects.setBounds(10, 293, 224, 188);
		contentPane.add(tabbedPaneEffects);
		
		JPanel panelAnimations = new JPanel();
		tabbedPaneEffects.addTab("Animation", null, panelAnimations, null);
		panelAnimations.setLayout(null);
		
		JSlider sliderAnimationSpeed = new JSlider();
		sliderAnimationSpeed.setMinimum(10);
		sliderAnimationSpeed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				animationSpeed = sliderAnimationSpeed.getValue();
				Client.send(new String[] {Identifier.ANI_SET_SPEED, String.valueOf(animationSpeed)});
			}
		});
		sliderAnimationSpeed.setFocusable(false);
		sliderAnimationSpeed.setMaximum(250);
		sliderAnimationSpeed.setMajorTickSpacing(10);
		sliderAnimationSpeed.setBounds(10, 123, 200, 26);
		panelAnimations.add(sliderAnimationSpeed);
		
		JLabel lblAnimationSpeed = new JLabel("Animation Speed");
		lblAnimationSpeed.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblAnimationSpeed.setBounds(10, 112, 199, 14);
		panelAnimations.add(lblAnimationSpeed);
		
		JButton btnFlash = new JButton("Flash");
		btnFlash.setFocusable(false);
		btnFlash.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		btnFlash.setBounds(10, 11, 89, 23);
		panelAnimations.add(btnFlash);
		
		JButton btnJump = new JButton("Jump");
		btnJump.setFocusable(false);
		btnJump.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		btnJump.setBounds(120, 11, 89, 23);
		panelAnimations.add(btnJump);
		
		JButton btnRainbow = new JButton("Rainbow");
		btnRainbow.setFocusable(false);
		btnRainbow.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		btnRainbow.setBounds(10, 45, 89, 23);
		panelAnimations.add(btnRainbow);
		
		JButton btnPulse = new JButton("Pulse");
		btnPulse.setFocusable(false);
		btnPulse.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		btnPulse.setBounds(121, 45, 89, 23);
		panelAnimations.add(btnPulse);
		
		JButton btnBlink = new JButton("Blink");
		btnBlink.setFocusable(false);
		btnBlink.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		btnBlink.setBounds(10, 79, 89, 23);
		panelAnimations.add(btnBlink);
		
		JButton btnOff = new JButton("OFF");
		btnOff.setFocusable(false);
		btnOff.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		btnOff.setBackground(Color.GRAY);
		btnOff.setBounds(120, 79, 89, 23);
		panelAnimations.add(btnOff);
		
		JPanel panelScreenColor = new JPanel();
		tabbedPaneEffects.addTab("ScreenClr.", null, panelScreenColor, null);
		panelScreenColor.setLayout(null);
		
		JButton btnEnableScreenColor = new JButton("Enable ScreenColor");
		btnEnableScreenColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!LEDScreenColor.isActive()) {
					Client.send(Identifier.SC_START);
					LEDScreenColor.start(widthL, heightL, widthR, heightR);
					btnEnableScreenColor.setText("Disable ScreenColor");
				} else {
					LEDScreenColor.stop();
					Client.send(Identifier.SC_STOP);
					btnEnableScreenColor.setText("Enable ScreenColor");
				}
			}
		});
		
		panelScreenColorLeft = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelScreenColorLeft.getLayout();
		flowLayout.setVgap(20);
		flowLayout.setHgap(20);
		panelScreenColorLeft.setForeground(Color.BLACK);
		panelScreenColorLeft.setBackground(Color.MAGENTA);
		panelScreenColorLeft.setBounds(10, 139, 79, -67);
		panelScreenColor.add(panelScreenColorLeft);
		btnEnableScreenColor.setFocusable(false);
		btnEnableScreenColor.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		btnEnableScreenColor.setBounds(10, 11, 199, 23);
		panelScreenColor.add(btnEnableScreenColor);
		
		panelScreenColorRight = new JPanel();
		panelScreenColorRight.setForeground(Color.BLACK);
		panelScreenColorRight.setBackground(Color.WHITE);
		panelScreenColorRight.setBounds(110, 139, 79, -67);
		panelScreenColor.add(panelScreenColorRight);
		
		lblColorLeft = new JLabel("Left");
		lblColorLeft.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblColorLeft.setBounds(10, 99, 46, 14);
		panelScreenColor.add(lblColorLeft);
		
		lblColorRight = new JLabel("Right");
		lblColorRight.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblColorRight.setBounds(110, 99, 46, 14);
		panelScreenColor.add(lblColorRight);
		
		JPanel panelMusicSync = new JPanel();
		tabbedPaneEffects.addTab("MusicSync", null, panelMusicSync, null);
		panelMusicSync.setLayout(null);
		
		JButton btnOpenSettingsGui = new JButton("Settings");
		btnOpenSettingsGui.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(musicSyncOld == null)
					musicSyncOld = new MusicSyncOld();
				//musicSyncOld.openGUI();
			}
		});
		btnOpenSettingsGui.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		btnOpenSettingsGui.setBounds(10, 11, 90, 23);
		panelMusicSync.add(btnOpenSettingsGui);
		
		panelMusicSyncLeft = new JPanel();
		panelMusicSyncLeft.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelMusicSyncLeft.setBackground(Color.WHITE);
		panelMusicSyncLeft.setBounds(10, 99, 50, 50);
		panelMusicSync.add(panelMusicSyncLeft);
		
		panelMusicSyncRight = new JPanel();
		panelMusicSyncRight.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelMusicSyncRight.setBackground(Color.WHITE);
		panelMusicSyncRight.setBounds(159, 99, 50, 50);
		panelMusicSync.add(panelMusicSyncRight);
		
		JLabel lblSensitivity = new JLabel("Sensitivity");
		lblSensitivity.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblSensitivity.setBounds(10, 42, 60, 14);
		panelMusicSync.add(lblSensitivity);
		
		JSlider sliderMusicSensitivity = new JSlider();
		sliderMusicSensitivity.setFocusable(false);
		sliderMusicSensitivity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				//MusicSyncOld.setSensitivity(sliderMusicSensitivity.getValue() / 100.0);
			}
		});
		sliderMusicSensitivity.setValue(100);
		sliderMusicSensitivity.setMaximum(300);
		sliderMusicSensitivity.setMinimum(10);
		sliderMusicSensitivity.setBounds(9, 56, 200, 26);
		panelMusicSync.add(sliderMusicSensitivity);
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String selected = comboBox.getSelectedItem().toString().toUpperCase();
				switch (selected) {
				case "FADE":
					//MusicSyncOld.setAnimation(selected);
					break;
				case "PULSE":
					//MusicSyncOld.setAnimation(selected);
					break;
				case "BASS":
					//MusicSyncOld.setAnimation(selected);
					break;

				default:
					break;
				}
			}
		});
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Fade", "Pulse", "Bass"}));
		comboBox.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		comboBox.setBounds(70, 115, 79, 23);
		panelMusicSync.add(comboBox);
		
		JLabel lblChooseEffect = new JLabel("Choose Effect");
		lblChooseEffect.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblChooseEffect.setBounds(70, 101, 79, 14);
		panelMusicSync.add(lblChooseEffect);
		
		JButton btnMusicSyncEnable = new JButton("Enable");
		btnMusicSyncEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(musicSyncOld == null)
					musicSyncOld = new MusicSyncOld();
				
//				if(MusicSyncOld.isActive()) {
//					musicSyncOld.stop();
//					btnMusicSyncEnable.setText("Enable");
//				} else {
//					musicSyncOld.start();
//					btnMusicSyncEnable.setText("Disable");
//				}
			}
		});
		btnMusicSyncEnable.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		btnMusicSyncEnable.setBounds(119, 11, 90, 23);
		panelMusicSync.add(btnMusicSyncEnable);
		
		sliderRed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Client.send(new String[] {Identifier.COLOR_RED, String.valueOf(sliderRed.getValue())});
				Client.send(new String[] {Identifier.COLOR_GREEN, String.valueOf(sliderGreen.getValue())});
				Client.send(new String[] {Identifier.COLOR_BLUE, String.valueOf(sliderBlue.getValue())});
			}
		});
		sliderGreen.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Client.send(new String[] {Identifier.COLOR_RED, String.valueOf(sliderRed.getValue())});
				Client.send(new String[] {Identifier.COLOR_GREEN, String.valueOf(sliderGreen.getValue())});
				Client.send(new String[] {Identifier.COLOR_BLUE, String.valueOf(sliderBlue.getValue())});
			}
		});
		sliderBlue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Client.send(new String[] {Identifier.COLOR_RED, String.valueOf(sliderRed.getValue())});
				Client.send(new String[] {Identifier.COLOR_GREEN, String.valueOf(sliderGreen.getValue())});
				Client.send(new String[] {Identifier.COLOR_BLUE, String.valueOf(sliderBlue.getValue())});
			}
		});
		
		btnFlash.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.ANI_FLASH, String.valueOf(animationSpeed)});
			}
		});
		btnJump.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.ANI_JUMP, String.valueOf(animationSpeed)});
			}
		});
		btnRainbow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.ANI_RAINBOW, String.valueOf(animationSpeed)});
			}
		});
		btnPulse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.ANI_PULSE, String.valueOf(animationSpeed)});
			}
		});
		btnBlink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.ANI_BLINK, String.valueOf(animationSpeed)});
			}
		});
		btnOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.ANI_SET_STOP, String.valueOf(animationSpeed)});
			}
		});
		
		this.addWindowListener(closeListener);
	}
	
	public void setConnectionStatus(String msg) {
		lblStatus.setText(msg);
	}
	
	public void setConnectedState() {
		btnConnect.setText("Disconnect");
		lblStatus.setText("");
	}
	
	public void setDisconnectedState() {
		btnConnect.setText("Connect");
	}
	
	public void setCurentColorPanel(Color left, Color right) {
		lblColorLeft.setForeground(left);
		lblColorRight.setForeground(right);
	}
	
	public void setMusicSyncColorPanel(Color left, Color right) {
		panelMusicSyncLeft.setBackground(left);
		panelMusicSyncRight.setBackground(right);
	}
	
	private void sendColor(Color color) {
		Client.send(new String[] {Identifier.COLOR_COLOR, color.getRed()+"", color.getGreen()+"", color.getBlue()+""});
	}
	
	WindowListener closeListener = new WindowAdapter() {
		public void windowClosing(WindowEvent windowEvent) {
			if((boolean) DataStorage.getData(DataStorage.SETTINGS_HIDE)) {
				SystemTrayIcon.showTrayIcon();
				RgbGUI.this.dispose();
			} else {
				System.exit(0);
			}
		}
	};
}
