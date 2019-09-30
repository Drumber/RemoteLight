package de.lars.remotelightclient.emulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.lars.remotelightclient.emulator.RLServerEmulator.ConnectionStateChangeListener;
import de.lars.remotelightclient.emulator.RLServerEmulator.InputReiceiveListener;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.UiUtils;
import de.lars.remotelightclient.utils.WrapLayout;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

public class EmulatorFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2752076462014410311L;
	private RLServerEmulator emulator;
	private Dimension pixelPanelSize = new Dimension(20, 20);
	private JPanel contentPane;
	private JPanel panelPixel;
	private JLabel lblStatus;

	/**
	 * Create the frame.
	 */
	public EmulatorFrame() {
		emulator = new RLServerEmulator();
		emulator.addReceiveListener(inputListener);
		emulator.addStateChangeListener(stateListener);
		setFrameTitle("Disconnected");
		addWindowListener(closeListener);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 640, 150);
		setMinimumSize(new Dimension(200, 100));
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setBackground(Style.panelBackground);
		setContentPane(contentPane);
		
		JPanel panelToolbar = new JPanel();
		panelToolbar.setBackground(Style.panelDarkBackground);
		contentPane.add(panelToolbar, BorderLayout.NORTH);
		panelToolbar.setLayout(new BoxLayout(panelToolbar, BoxLayout.X_AXIS));
		
		JPanel panelControl = new JPanel();
		WrapLayout wl_panelControl = new WrapLayout(FlowLayout.LEFT);
		panelControl.setLayout(wl_panelControl);
		panelControl.setBackground(Style.panelDarkBackground);
		panelToolbar.add(panelControl);
		
		JButton btnEnable = new JButton("Enable");
		btnEnable.setName("enable");
		btnEnable.addActionListener(btnListener);
		UiUtils.configureButton(btnEnable);
		panelControl.add(btnEnable);
		
		lblStatus = new JLabel();
		lblStatus.setForeground(Style.textColor);
		panelControl.add(lblStatus);
		
		JPanel panelScale = new JPanel();
		panelScale.setBackground(Style.panelDarkBackground);
		panelToolbar.add(panelScale);
		panelScale.setLayout(new BoxLayout(panelScale, BoxLayout.X_AXIS));
		
		JButton btnMinus = new JButton("-");
		btnMinus.setName("minus");
		btnMinus.addActionListener(btnListener);
		UiUtils.configureButton(btnMinus);
		panelScale.add(btnMinus);
		
		JButton btnPlus = new JButton("+");
		btnPlus.setName("plus");
		btnPlus.addActionListener(btnListener);
		UiUtils.configureButton(btnPlus);
		panelScale.add(btnPlus);
		
		JPanel bgrPixel = new JPanel();
		contentPane.add(bgrPixel, BorderLayout.CENTER);
		bgrPixel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		bgrPixel.add(scrollPane, BorderLayout.CENTER);
		
		panelPixel = new JPanel();
		WrapLayout wlayout = new WrapLayout(FlowLayout.LEFT);
		panelPixel.setLayout(wlayout);
		panelPixel.setBackground(Style.panelBackground);
		scrollPane.setViewportView(panelPixel);
	}
	
	WindowListener closeListener = new WindowAdapter() {
		public void windowClosing(WindowEvent windowEvent) {
			emulator.stop();
			dispose();
		}
	};
	
	public void setFrameTitle(String text) {
		setTitle("[Emulator] " + text);
	}
	
	private ActionListener btnListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			switch (btn.getName()) {
			case "enable":
				btn.setText(!emulator.isRunning() ? "Disable" : "Enable");
				toggleEmulator(!emulator.isRunning());
				break;
				
			}
		}
	};
	
	
	public void toggleEmulator(boolean enable) {
		if(enable) {
			emulator.start();
		} else {
			emulator.stop();
		}
	}
	
	private InputReiceiveListener inputListener = new InputReiceiveListener() {
		@Override
		public void onInputReceived(Color[] pixel) {
			addPixelPanels(pixel);
			lblStatus.setText("Number of pixels: " + pixel.length);
		}
	};
	
	private ConnectionStateChangeListener stateListener = new ConnectionStateChangeListener() {
		@Override
		public void onConnectionStateChanged(String status) {
			setFrameTitle(status);
		}
	};
	
	
	public void addPixelPanels(Color[] pixels) {
		panelPixel.removeAll();
		
		for(Color c : pixels) {
			JPanel panel = new JPanel();
			panel.setBackground(c);
			
			panel.setPreferredSize(pixelPanelSize);
			
			panelPixel.add(panel);
		}
		
	}

}
