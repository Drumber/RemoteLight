package de.lars.remotelightclient.gui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;

import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicSync;
import de.lars.remotelightclient.musicsync.sound.SoundProcessing;
import de.lars.remotelightclient.musicsync.ws281x.settings_guis.LevelBarSettings;
import de.lars.remotelightclient.musicsync.ws281x.settings_guis.RainbowSettings;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;
import de.lars.remotelightclient.scenes.SceneHandler;
import de.lars.remotelightclient.screencolor.WS281xScreenColorHandler;

import javax.swing.event.ChangeEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;

import javax.swing.JTabbedPane;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.Toolkit;
import javax.swing.JCheckBox;

public class WS281xGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7017941162622210272L;
	private MusicSync musicSync;
	private JPanel contentPane;
	private int pixels = 60;
	private JButton btnConnect;
	private JLabel lblStatus, lblEffectSettingsStatus;
	private JSpinner spinnerScInterval, spinnerScYpos;
	private JCheckBox chckbxInvertScreenColor;
	private int brightness;
	


	/**
	 * Create the frame.
	 */
	public WS281xGUI() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(WS281xGUI.class.getResource("/resourcen/Icon-128x128.png")));
		setTitle("WS281x Control");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 350, 600);
		
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
		
		JMenuItem mntmRgb = new JMenuItem("RGB");
		mntmRgb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getInstance().openRgbGui();
			}
		});
		menu.add(mntmRgb);
		
		JMenuItem menuItem_2 = new JMenuItem("Exit");
		menuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(menuItem_2);
		
		JMenu mnRaspberrypi = new JMenu("RaspberryPi");
		menuBar.add(mnRaspberrypi);
		
		JMenuItem mntmShutdown = new JMenuItem("Shutdown");
		mntmShutdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.send(new String[] {Identifier.SYS_SHUTDOWN_NOW});
			}
		});
		mnRaspberrypi.add(mntmShutdown);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
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
		mnHelp.add(mntmWebsite);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnOpenRgbGui = new JButton("Open RGB GUI with WS281x support");
		btnOpenRgbGui.setFocusable(false);
		btnOpenRgbGui.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getInstance().openRgbGui();
				Client.send(new String[] {Identifier.STNG_MODE_WS28x, pixels+""});
			}
		});
		btnOpenRgbGui.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		btnOpenRgbGui.setBounds(10, 506, 314, 23);
		contentPane.add(btnOpenRgbGui);
		
		fieldServerIP = new JTextField();
		fieldServerIP.setText((String) null);
		fieldServerIP.setColumns(10);
		fieldServerIP.setBounds(10, 25, 125, 20);
		fieldServerIP.setText((String) DataStorage.getData(DataStorage.IP_STOREKEY));
		contentPane.add(fieldServerIP);
		
		JLabel label = new JLabel("Hostnama / IP");
		label.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		label.setBounds(10, 11, 97, 14);
		contentPane.add(label);
		
		btnConnect = new JButton("Connect");
		btnConnect.setFocusable(false);
		btnConnect.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		btnConnect.setBounds(145, 24, 89, 23);
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Connect Button Event
				if(!Client.isConnected()) {
					DataStorage.store(DataStorage.IP_STOREKEY, fieldServerIP.getText());
					Client.connect(fieldServerIP.getText());
					
					performConnectActions();
				} else {
					Client.disconnect();
				}
			}
		});
		contentPane.add(btnConnect);
		
		lblStatus = new JLabel("");
		lblStatus.setForeground(Color.RED);
		lblStatus.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
		lblStatus.setBounds(10, 47, 232, 14);
		contentPane.add(lblStatus);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFocusable(false);
		tabbedPane.setBounds(10, 268, 314, 185);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Animations", null, panel, null);
		panel.setLayout(null);
		
		JButton btnRainbow = new JButton("Rainbow");
		btnRainbow.setFocusable(false);
		btnRainbow.setBounds(10, 11, 89, 23);
		panel.add(btnRainbow);
		
		JButton btnRunninglight = new JButton("RunningLight");
		btnRunninglight.setFocusable(false);
		btnRunninglight.setToolTipText("RunningLight");
		btnRunninglight.setBounds(109, 11, 89, 23);
		panel.add(btnRunninglight);
		
		JButton btnWipe = new JButton("Wipe");
		btnWipe.setFocusable(false);
		btnWipe.setBounds(10, 45, 89, 23);
		panel.add(btnWipe);
		
		JButton btnScan = new JButton("Scanner");
		btnScan.setFocusable(false);
		btnScan.setBounds(208, 11, 89, 23);
		panel.add(btnScan);
		
		JSlider slider = new JSlider();
		slider.setFocusable(false);
		slider.setBounds(61, 120, 236, 26);
		panel.add(slider);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Client.send(new String[] {Identifier.WS_ANI_SPEED, slider.getValue()+""});
			}
		});
		slider.setMaximum(300);
		slider.setMinimum(10);
		
		JLabel lblSpeed = new JLabel("Speed:");
		lblSpeed.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblSpeed.setBounds(10, 127, 46, 14);
		panel.add(lblSpeed);
		
		JButton btnStop = new JButton("Stop");
		btnStop.setFocusable(false);
		btnStop.setBounds(10, 86, 89, 23);
		panel.add(btnStop);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Client.send(new String[] {Identifier.WS_ANI_STOP});
			}
		});
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("ScreenColor", null, panel_1, null);
		panel_1.setLayout(null);
		
		JButton btnEnableScreenColor = new JButton("Enable ScreenColor");
		btnEnableScreenColor.setFocusable(false);
		btnEnableScreenColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!WS281xScreenColorHandler.isActive()) {
					Client.send(Identifier.SC_START);
					WS281xScreenColorHandler.start((int) spinnerScYpos.getValue(), (int) spinnerScInterval.getValue(), chckbxInvertScreenColor.isSelected());
					btnEnableScreenColor.setText("Disable ScreenColor");
				} else {
					WS281xScreenColorHandler.stop();
					btnEnableScreenColor.setText("Enable ScreenColor");
					try {
						Thread.sleep(750);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Client.send(Identifier.SC_STOP);
				}
			}
		});
		btnEnableScreenColor.setBounds(10, 11, 289, 23);
		panel_1.add(btnEnableScreenColor);
		
		JLabel lblIntervalInMs = new JLabel("Interval in ms:");
		lblIntervalInMs.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblIntervalInMs.setBounds(10, 45, 80, 14);
		panel_1.add(lblIntervalInMs);
		
		spinnerScInterval = new JSpinner();
		spinnerScInterval.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				DataStorage.store(DataStorage.SETTINGS_SCREENCOLOR_INTERVAL, spinnerScInterval.getValue());
			}
		});
		spinnerScInterval.setModel(new SpinnerNumberModel(150, 25, 2000, 1));
		if(DataStorage.isStored(DataStorage.SETTINGS_SCREENCOLOR_INTERVAL))
			spinnerScInterval.setValue(DataStorage.getData(DataStorage.SETTINGS_SCREENCOLOR_INTERVAL));
		spinnerScInterval.setBounds(100, 42, 59, 20);
		panel_1.add(spinnerScInterval);
		
		JLabel lblYaxe = new JLabel("Y-Axe:");
		lblYaxe.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblYaxe.setBounds(10, 70, 46, 14);
		panel_1.add(lblYaxe);
		
		spinnerScYpos = new JSpinner();
		spinnerScYpos.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				DataStorage.store(DataStorage.SETTINGS_SCREENCOLOR_YPOS, spinnerScInterval.getValue());
			}
		});
		spinnerScYpos.setModel(new SpinnerNumberModel(new Integer(1000), new Integer(0), null, new Integer(1)));
		if(DataStorage.isStored(DataStorage.SETTINGS_SCREENCOLOR_YPOS))
			spinnerScInterval.setValue(DataStorage.getData(DataStorage.SETTINGS_SCREENCOLOR_YPOS));
		spinnerScYpos.setBounds(100, 67, 59, 20);
		panel_1.add(spinnerScYpos);
		
		JLabel lblPixelHeightOf = new JLabel("Pixel height of your monitor.");
		lblPixelHeightOf.setFont(new Font("Source Sans Pro", Font.ITALIC, 10));
		lblPixelHeightOf.setBounds(10, 87, 149, 14);
		panel_1.add(lblPixelHeightOf);
		
		JLabel lblIsAt = new JLabel("0 is at the top");
		lblIsAt.setFont(new Font("Source Sans Pro", Font.ITALIC, 10));
		lblIsAt.setBounds(10, 100, 113, 14);
		panel_1.add(lblIsAt);
		
		chckbxInvertScreenColor = new JCheckBox("Invert");
		chckbxInvertScreenColor.setFocusable(false);
		chckbxInvertScreenColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DataStorage.store(DataStorage.SETTINGS_SCREENCOLOR_INVERT, chckbxInvertScreenColor.isSelected());
			}
		});
		chckbxInvertScreenColor.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		chckbxInvertScreenColor.setToolTipText("Invert left and right");
		if(DataStorage.isStored(DataStorage.SETTINGS_SCREENCOLOR_INVERT))
			chckbxInvertScreenColor.setSelected((boolean) DataStorage.getData(DataStorage.SETTINGS_SCREENCOLOR_INVERT));
		chckbxInvertScreenColor.setBounds(10, 127, 97, 23);
		panel_1.add(chckbxInvertScreenColor);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("MusicSync", null, panel_2, null);
		panel_2.setLayout(null);
		
		JButton btnMusicSyncEnable = new JButton("Enable");
		btnMusicSyncEnable.setFocusable(false);
		btnMusicSyncEnable.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		btnMusicSyncEnable.setBounds(10, 42, 89, 23);
		panel_2.add(btnMusicSyncEnable);
		
		JLabel lblEffect = new JLabel("Effect:");
		lblEffect.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblEffect.setBounds(10, 11, 46, 14);
		panel_2.add(lblEffect);
		
		JComboBox<String> comboBoxMusicSync = new JComboBox<String>();
		comboBoxMusicSync.setFocusable(false);
		comboBoxMusicSync.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				String selected = comboBoxMusicSync.getSelectedItem().toString().toUpperCase();
				MusicSync.setAnimation(selected);
			}
		});
		comboBoxMusicSync.setModel(new DefaultComboBoxModel<String>(new String[] {"RunningLight", "LevelBar", "Rainbow", "Bump", "EQ", "Fade", "Pulse"}));
		comboBoxMusicSync.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		comboBoxMusicSync.setBounds(49, 8, 89, 23);
		panel_2.add(comboBoxMusicSync);
		
		JButton btnOpenSettingsGui = new JButton("Input");
		btnOpenSettingsGui.setFocusable(false);
		btnOpenSettingsGui.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		btnOpenSettingsGui.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(musicSync == null)
					musicSync = new MusicSync();
				musicSync.openGUI();
			}
		});
		btnOpenSettingsGui.setBounds(210, 7, 89, 23);
		panel_2.add(btnOpenSettingsGui);
		
		JLabel lblSensitivity = new JLabel("Sensitivity:");
		lblSensitivity.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblSensitivity.setBounds(10, 78, 59, 14);
		panel_2.add(lblSensitivity);
		
		JSlider sliderMusicSensitivity = new JSlider();
		sliderMusicSensitivity.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				MusicSync.setSensitivity(sliderMusicSensitivity.getValue() / 100.0);
				DataStorage.store(DataStorage.SETTINGS_MUSICSYNC_SENSITIVITY, sliderMusicSensitivity.getValue());
			}
		});
		sliderMusicSensitivity.setFocusable(false);
		sliderMusicSensitivity.setMinimum(10);
		sliderMusicSensitivity.setMaximum(300);
		if(DataStorage.isStored(DataStorage.SETTINGS_MUSICSYNC_SENSITIVITY))
			sliderMusicSensitivity.setValue((int) DataStorage.getData(DataStorage.SETTINGS_MUSICSYNC_SENSITIVITY));
		else
			sliderMusicSensitivity.setValue(100);
		MusicSync.setSensitivity(sliderMusicSensitivity.getValue() / 100.0);
		sliderMusicSensitivity.setBounds(10, 92, 200, 26);
		panel_2.add(sliderMusicSensitivity);
		
		JButton btnEffectSettings = new JButton("Effect Settings");
		btnEffectSettings.setFocusable(false);
		btnEffectSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switch ((String) comboBoxMusicSync.getSelectedItem()) {
				case "LevelBar":
					new LevelBarSettings();
					lblEffectSettingsStatus.setText("");
					break;
				case "Rainbow":
					new RainbowSettings();
					lblEffectSettingsStatus.setText("");
					break;

				default:
					lblEffectSettingsStatus.setText("There are no settings for this effect.");
					break;
				}
			}
		});
		btnEffectSettings.setBounds(10, 123, 200, 23);
		panel_2.add(btnEffectSettings);
		
		lblEffectSettingsStatus = new JLabel("");
		lblEffectSettingsStatus.setForeground(Color.RED);
		lblEffectSettingsStatus.setFont(new Font("Source Sans Pro", Font.PLAIN, 10));
		lblEffectSettingsStatus.setBounds(10, 143, 200, 14);
		panel_2.add(lblEffectSettingsStatus);
		
		JLabel lblInputStatus = new JLabel("");
		lblInputStatus.setForeground(Color.RED);
		lblInputStatus.setFont(new Font("Source Sans Pro", Font.PLAIN, 10));
		lblInputStatus.setBounds(210, 33, 89, 14);
		panel_2.add(lblInputStatus);
		btnMusicSyncEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(musicSync == null) {
					musicSync = new MusicSync();
				}
				if(!SoundProcessing.isMixerSet()) {
					lblInputStatus.setText("No Input set!");
					return;
				}
				lblInputStatus.setText("");
				
				if(MusicSync.isActive()) {
					musicSync.stop();
					musicSync = null;
					Client.send(new String[] {Identifier.WS_COLOR_OFF});
					btnMusicSyncEnable.setText("Enable");
				} else {
					MusicSync.setAnimation(comboBoxMusicSync.getSelectedItem().toString().toUpperCase());
					musicSync.start();
					btnMusicSyncEnable.setText("Disable");
				}
			}
		});
		
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_1.setFocusable(false);
		tabbedPane_1.setBounds(10, 89, 314, 148);
		contentPane.add(tabbedPane_1);
		
		JPanel panelColors = new JPanel();
		tabbedPane_1.addTab("Colors", null, panelColors, null);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 76, 314, 9);
		contentPane.add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 248, 314, 9);
		contentPane.add(separator_1);
		btnScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Client.send(new String[] {Identifier.WS_ANI_SCAN});
			}
		});
		btnWipe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Client.send(new String[] {Identifier.WS_ANI_WIPE});
			}
		});
		btnRunninglight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Client.send(new String[] {Identifier.WS_ANI_RUNNING});
			}
		});
		btnRainbow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Client.send(new String[] {Identifier.WS_ANI_RAINBOW});
			}
		});
		this.addWindowListener(closeListener);
		
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
					.addContainerGap()
					.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
						.addComponent(c1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(c7, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
						.addComponent(c2, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(c8, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelColors.createSequentialGroup()
							.addComponent(c3, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(c4, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(c5, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(c6, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(c12, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(c13, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelColors.createSequentialGroup()
							.addComponent(c9, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(c10, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(c14, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(c16, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(c17, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(c18, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panelColors.setVerticalGroup(
			gl_panelColors.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelColors.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelColors.createSequentialGroup()
							.addComponent(c2, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(c8, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelColors.createSequentialGroup()
							.addComponent(c1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(c7, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelColors.createSequentialGroup()
							.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
								.addComponent(c3, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c4, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c5, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c6, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c12, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c13, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelColors.createParallelGroup(Alignment.LEADING)
								.addComponent(c18, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c17, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c14, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c16, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c9, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(c10, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))))
					.addGap(41))
		);
		panelColors.setLayout(gl_panelColors);
		
		JPanel scenes = new JPanel();
		tabbedPane_1.addTab("Scenes", null, scenes, null);
		scenes.setLayout(null);
		
		JButton btnSunset = new JButton("Sunset");
		btnSunset.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		btnSunset.setFocusable(false);
		btnSunset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SceneHandler.start(SceneHandler.SUNSET);
			}
		});
		btnSunset.setBounds(10, 11, 89, 23);
		scenes.add(btnSunset);
		
		JButton btnFire = new JButton("Fire");
		btnFire.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SceneHandler.start(SceneHandler.FIRE);
			}
		});
		btnFire.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		btnFire.setFocusable(false);
		btnFire.setBounds(109, 11, 89, 23);
		scenes.add(btnFire);
		
		JButton btnNorthernlights = new JButton("NorthernLights");
		btnNorthernlights.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SceneHandler.start(SceneHandler.NORTHERNLIGHTS);
			}
		});
		btnNorthernlights.setToolTipText("NorthernLights");
		btnNorthernlights.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		btnNorthernlights.setFocusable(false);
		btnNorthernlights.setBounds(208, 11, 89, 23);
		scenes.add(btnNorthernlights);
		
		JButton btnJungle = new JButton("Jungle");
		btnJungle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SceneHandler.start(SceneHandler.JUNGLE);
			}
		});
		btnJungle.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		btnJungle.setFocusable(false);
		btnJungle.setBounds(10, 45, 89, 23);
		scenes.add(btnJungle);
		
		JButton btnOcean = new JButton("Ocean");
		btnOcean.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SceneHandler.start(SceneHandler.OCEAN);
			}
		});
		btnOcean.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		btnOcean.setFocusable(false);
		btnOcean.setBounds(109, 45, 89, 23);
		scenes.add(btnOcean);
		
		JButton btnSpace = new JButton("Space");
		btnSpace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SceneHandler.start(SceneHandler.SPACE);
			}
		});
		btnSpace.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		btnSpace.setFocusable(false);
		btnSpace.setBounds(208, 45, 89, 23);
		scenes.add(btnSpace);
		
		JButton btnStop_1 = new JButton("Stop");
		btnStop_1.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		btnStop_1.setFocusable(false);
		btnStop_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SceneHandler.stop();
			}
		});
		btnStop_1.setBounds(10, 86, 89, 23);
		scenes.add(btnStop_1);
		
		JLabel lblDim = new JLabel("Brightness:");
		lblDim.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblDim.setBounds(10, 464, 74, 14);
		contentPane.add(lblDim);
		
		JSlider sliderBrightness = new JSlider();
		sliderBrightness.setFocusable(false);
		sliderBrightness.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				brightness = 7 - sliderBrightness.getValue();
				DataStorage.store(DataStorage.SETTINGS_BRIGHTNESS, brightness);
				Client.send(new String[] {Identifier.WS_COLOR_DIM, brightness+""});
			}
		});
		sliderBrightness.setValue(6);
		sliderBrightness.setMaximum(6);
		sliderBrightness.setMinimum(1);
		if(DataStorage.isStored(DataStorage.SETTINGS_BRIGHTNESS))
			sliderBrightness.setValue(7 - (int) DataStorage.getData(DataStorage.SETTINGS_BRIGHTNESS));
		else
			sliderBrightness.setValue(6);
		sliderBrightness.setBounds(10, 475, 200, 26);
		contentPane.add(sliderBrightness);
		
		JButton btnOff = new JButton("Turn off");
		btnOff.setFocusable(false);
		btnOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(musicSync != null)
					musicSync.stop();
				WS281xScreenColorHandler.stop();
				SceneHandler.stop();
				Client.send(new String[] {Identifier.WS_ANI_STOP});
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Client.send(new String[] {Identifier.WS_COLOR_OFF});
			}
		});
		btnOff.setBounds(253, 472, 71, 23);
		contentPane.add(btnOff);
		
	}
	
	/*
	 * Connect Actions
	 */
	public void performConnectActions() {
		Client.send(new String[] {Identifier.STNG_MODE_WS28x, DataStorage.getData(DataStorage.SETTINGS_LED_NUM)+""});
		
		if(!DataStorage.isStored(DataStorage.SETTINGS_BRIGHTNESS))
			brightness = 1;
		else
			brightness = (int) DataStorage.getData(DataStorage.SETTINGS_BRIGHTNESS);
		Client.send(new String[] {Identifier.WS_COLOR_DIM, brightness+""});
		
		if(DataStorage.isStored(DataStorage.SETTINGS_BOOT_ANI))
			Client.send(new String[] {Identifier.STNG_BOOT_ANI, String.valueOf(DataStorage.getData(DataStorage.SETTINGS_BOOT_ANI))});
		
//		if((boolean) DataStorage.getData(DataStorage.SETTINGS_AUTOSHUTDOWN))
//			Client.send(new String[] {Identifier.SYS_SHUTDOWN_IFNOT_REACHABLE});
	}
	
	
	
	
	
	public void setConnectionStatus(String msg) {
		lblStatus.setText(msg);
	}
	
	public void setConnectedState() {
		btnConnect.setText("Disconnect");
		lblStatus.setText("");
		this.setTitle("WS281x | RaspberryPi >> " + fieldServerIP.getText());
	}
	
	public void setDisconnectedState() {
		btnConnect.setText("Connect");
		this.setTitle("WS281x Control");
	}
	
	private void sendColor(Color color) {
		int r = color.getRed(), g = color.getGreen(), b = color.getBlue();
		if(r < 0) r = 0;
		if(g < 0) g = 0;
		if(b < 0) b = 0;
		Client.send(new String[] {Identifier.COLOR_COLOR, r+"", g+"", b+""});
	}
	
	
	WindowListener closeListener = new WindowAdapter() {
		public void windowClosing(WindowEvent windowEvent) {
			if((boolean) DataStorage.getData(DataStorage.SETTINGS_HIDE)) {
				SystemTrayIcon.showTrayIcon();
				WS281xGUI.this.dispose();
			} else {
				System.exit(0);
			}
		}
	};
	private JTextField fieldServerIP;
}
