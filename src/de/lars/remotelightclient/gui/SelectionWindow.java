package de.lars.remotelightclient.gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.lars.remotelightclient.Main;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

public class SelectionWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7188929321382875622L;
	private JPanel contentPane;


	/**
	 * Create the frame.
	 */
	public SelectionWindow() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(SelectionWindow.class.getResource("/resourcen/Icon-128x128.png")));
		setTitle("RGB DeskLamp | Select Device");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 250);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel lblSelectTheDevice = new JLabel("Select the device that you want to control");
		lblSelectTheDevice.setFont(new Font("Source Sans Pro", Font.BOLD, 14));
		contentPane.add(lblSelectTheDevice, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		
		JLabel lblRgbLed = new JLabel("RGB LED (only RPi)");
		lblRgbLed.setFont(new Font("Source Sans Pro", Font.PLAIN, 14));
		
		JLabel lblWsx = new JLabel("WS281x Strip");
		lblWsx.setFont(new Font("Source Sans Pro", Font.PLAIN, 14));
		
		JTextPane txtpnControlThreeSeperate = new JTextPane();
		txtpnControlThreeSeperate.setFocusable(false);
		txtpnControlThreeSeperate.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		txtpnControlThreeSeperate.setEditable(false);
		txtpnControlThreeSeperate.setText("Control three separate LEDs (red, green, blue) or one RGB-LED per side (left / right).\r\nRGB strips should also work.");
		
		JTextPane txtpnControlOneWswswsb = new JTextPane();
		txtpnControlOneWswswsb.setFocusable(false);
		txtpnControlOneWswswsb.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		txtpnControlOneWswswsb.setEditable(false);
		txtpnControlOneWswswsb.setText("Control one WS2811/WS2812/WS2812b LED strip.\r\nOnly GPIO-Pin 18 and GRND are needed. (RPi)\r\nSupports Arduino!\r\n  (-> Settings)");
		
		JButton btnSelectRGB = new JButton("Select");
		btnSelectRGB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				Main.getInstance().setRGBMode();
			}
		});
		btnSelectRGB.setFocusable(false);
		btnSelectRGB.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		
		JButton btnSelectWS281x = new JButton("Select");
		btnSelectWS281x.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				Main.getInstance().setWS281xMode();
				Main.getInstance().openSettingsGui();
			}
		});
		btnSelectWS281x.setFocusable(false);
		btnSelectWS281x.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(txtpnControlThreeSeperate, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
								.addComponent(lblRgbLed))
							.addGap(64)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblWsx)
								.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
									.addComponent(btnSelectWS281x, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
									.addComponent(txtpnControlOneWswswsb, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)))
							.addContainerGap())
						.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
							.addComponent(btnSelectRGB, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
							.addGap(219))))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblRgbLed)
						.addComponent(lblWsx))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(txtpnControlOneWswswsb)
						.addComponent(txtpnControlThreeSeperate))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE, false)
						.addComponent(btnSelectRGB)
						.addComponent(btnSelectWS281x))
					.addGap(65))
		);
		panel.setLayout(gl_panel);
	}
}
