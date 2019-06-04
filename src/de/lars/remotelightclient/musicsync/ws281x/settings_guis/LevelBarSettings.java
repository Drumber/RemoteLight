package de.lars.remotelightclient.musicsync.ws281x.settings_guis;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.border.LineBorder;

import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.musicsync.ws281x.LevelBar;

import java.awt.Color;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class LevelBarSettings extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1789010807810035069L;
	private JPanel contentPane;


	/**
	 * Create the frame.
	 */
	public LevelBarSettings() {
		setAlwaysOnTop(true);
		setType(Type.UTILITY);
		setVisible(true);
		setResizable(false);
		setTitle("LevelBar Effect Settings");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 150, 320);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblColor1 = new JLabel("Color 1:");
		lblColor1.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblColor1.setBounds(10, 11, 46, 14);
		contentPane.add(lblColor1);
		
		JLabel lblColor2 = new JLabel("Color 2:");
		lblColor2.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblColor2.setBounds(10, 66, 46, 14);
		contentPane.add(lblColor2);
		
		JLabel lblColor3 = new JLabel("Color 3:");
		lblColor3.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblColor3.setBounds(10, 121, 46, 14);
		contentPane.add(lblColor3);
		
		JPanel panelColor1 = new JPanel();
		panelColor1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panelColor1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				panelColor1.setBackground(openColorChooser(panelColor1.getBackground()));
				LevelBar.color1 = panelColor1.getBackground();
				System.out.println("1: " + panelColor1.getBackground());
			}
		});
		panelColor1.setBackground((Color) DataStorage.getData(DataStorage.LEVELBAR_COLOR1));
		panelColor1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelColor1.setBounds(10, 25, 30, 30);
		contentPane.add(panelColor1);
		
		JPanel panelColor2 = new JPanel();
		panelColor2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				panelColor2.setBackground(openColorChooser(panelColor2.getBackground()));
				LevelBar.color2 = panelColor2.getBackground();
				System.out.println("2: " + panelColor2.getBackground());
			}
		});
		panelColor2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panelColor2.setBackground((Color) DataStorage.getData(DataStorage.LEVELBAR_COLOR2));
		panelColor2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelColor2.setBounds(10, 80, 30, 30);
		contentPane.add(panelColor2);
		
		JPanel panelColor3 = new JPanel();
		panelColor3.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				panelColor3.setBackground(openColorChooser(panelColor3.getBackground()));
				LevelBar.color3 = panelColor3.getBackground();
				System.out.println("3: " + panelColor3.getBackground());
			}
		});
		panelColor3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panelColor3.setBackground((Color) DataStorage.getData(DataStorage.LEVELBAR_COLOR3));
		panelColor3.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelColor3.setBounds(10, 134, 30, 30);
		contentPane.add(panelColor3);
		
		JCheckBox chckbxSmooth = new JCheckBox("Smooth");
		chckbxSmooth.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				DataStorage.store(DataStorage.LEVELBAR_SMOOTH, chckbxSmooth.isSelected());
				LevelBar.smooth = chckbxSmooth.isSelected();
			}
		});
		if(DataStorage.isStored(DataStorage.LEVELBAR_SMOOTH))
			chckbxSmooth.setSelected((boolean) DataStorage.getData(DataStorage.LEVELBAR_SMOOTH));
		chckbxSmooth.setFocusable(false);
		chckbxSmooth.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		chckbxSmooth.setBounds(6, 197, 97, 23);
		contentPane.add(chckbxSmooth);
		
		JButton btnOk = new JButton("OK");
		btnOk.setFocusable(false);
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DataStorage.store(DataStorage.LEVELBAR_COLOR1, panelColor1.getBackground());
				DataStorage.store(DataStorage.LEVELBAR_COLOR2, panelColor2.getBackground());
				DataStorage.store(DataStorage.LEVELBAR_COLOR3, panelColor3.getBackground());
				dispose();
			}
		});
		btnOk.setBounds(10, 257, 46, 23);
		contentPane.add(btnOk);
		
		JCheckBox chckbxAutoChangeColor = new JCheckBox("Auto change Color");
		chckbxAutoChangeColor.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				DataStorage.store(DataStorage.LEVELBAR_AUTOCHANGE, chckbxAutoChangeColor.isSelected());
				LevelBar.autoChange = chckbxAutoChangeColor.isSelected();
			}
		});
		if(DataStorage.isStored(DataStorage.LEVELBAR_AUTOCHANGE))
			chckbxAutoChangeColor.setSelected((boolean) DataStorage.getData(DataStorage.LEVELBAR_AUTOCHANGE));
		chckbxAutoChangeColor.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		chckbxAutoChangeColor.setFocusable(false);
		chckbxAutoChangeColor.setBounds(6, 171, 132, 23);
		contentPane.add(chckbxAutoChangeColor);
	}
	
	private static Color openColorChooser(Color c) {
		JColorChooser cc = new JColorChooser();
		return JColorChooser.showDialog(cc, "Choose a color", c);
	}
}
