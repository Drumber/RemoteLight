package de.lars.remotelightclient.musicsync.ws281x.settings_guis;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import de.lars.remotelightclient.DataStorage;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class RainbowSettings extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1789010807810035069L;
	private JPanel contentPane;
	private JSpinner spinnerSteps;
	
	private JCheckBox chckbxSmoothFall, chckbxSmoothRise;


	/**
	 * Create the frame.
	 */
	public RainbowSettings() {
		setAlwaysOnTop(true);
		setType(Type.UTILITY);
		setVisible(true);
		setResizable(false);
		setTitle("Rainbow Effect Settings");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 150, 210);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		chckbxSmoothRise = new JCheckBox("Smooth Rise");
		chckbxSmoothRise.setSelected(true);
		chckbxSmoothRise.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				//Rainbow.smothRise = chckbxSmoothRise.isSelected();
			}
		});
		if(DataStorage.isStored(DataStorage.RAINBOW_SMOOTH_RISE))
			chckbxSmoothRise.setSelected((boolean) DataStorage.getData(DataStorage.RAINBOW_SMOOTH_RISE));
		chckbxSmoothRise.setFocusable(false);
		chckbxSmoothRise.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		chckbxSmoothRise.setBounds(6, 7, 97, 23);
		contentPane.add(chckbxSmoothRise);
		
		JButton btnOk = new JButton("OK");
		btnOk.setFocusable(false);
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DataStorage.store(DataStorage.RAINBOW_SMOOTH_RISE, chckbxSmoothRise.isSelected());
				DataStorage.store(DataStorage.RAINBOW_SMOOTH_FALL, chckbxSmoothFall.isSelected());
				DataStorage.store(DataStorage.RAINBOW_STEPS, spinnerSteps.getValue());
				dispose();
			}
		});
		btnOk.setBounds(6, 147, 46, 23);
		contentPane.add(btnOk);
		
		chckbxSmoothFall = new JCheckBox("Smooth Fall");
		chckbxSmoothFall.setSelected(true);
		chckbxSmoothFall.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				//Rainbow.smothFall = chckbxSmoothFall.isSelected();
			}
		});
		if(DataStorage.isStored(DataStorage.RAINBOW_SMOOTH_FALL))
			chckbxSmoothFall.setSelected((boolean) DataStorage.getData(DataStorage.RAINBOW_SMOOTH_FALL));
		chckbxSmoothFall.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		chckbxSmoothFall.setFocusable(false);
		chckbxSmoothFall.setBounds(6, 33, 97, 23);
		contentPane.add(chckbxSmoothFall);
		
		JLabel lblRainbowSteps = new JLabel("Rainbow steps");
		lblRainbowSteps.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		lblRainbowSteps.setBounds(6, 63, 128, 14);
		contentPane.add(lblRainbowSteps);
		
		JLabel lblhigherMore = new JLabel("(higher = more colors)");
		lblhigherMore.setFont(new Font("Source Sans Pro", Font.PLAIN, 10));
		lblhigherMore.setBounds(10, 78, 124, 14);
		contentPane.add(lblhigherMore);
		
		spinnerSteps = new JSpinner();
		spinnerSteps.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				//Rainbow.steps = (int) spinnerSteps.getValue();
			}
		});
		spinnerSteps.setModel(new SpinnerNumberModel(5, 1, 90, 1));
		spinnerSteps.setBounds(10, 96, 46, 20);
		if(DataStorage.isStored(DataStorage.RAINBOW_STEPS))
			spinnerSteps.setValue(DataStorage.getData(DataStorage.RAINBOW_STEPS));
		contentPane.add(spinnerSteps);
		
	}
	
}
