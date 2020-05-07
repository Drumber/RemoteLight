package de.lars.remotelightclient.ui.panels.musicsync.nativesound;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.xtaudio.xt.*;

import de.lars.colorpicker.utils.ColorPickerStyle;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.sound.nativesound.NativeSound;
import de.lars.remotelightclient.musicsync.sound.nativesound.NativeSoundFormat;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.DisabledGlassPane;
import de.lars.remotelightclient.utils.UiUtils;

public class NativeSoundConfigPanel extends JPanel {
	private static final long serialVersionUID = -3746048418035257056L;
	
	private SettingsManager sm;
	private NativeSound nsound;
	
	private JDialog dialog;
	private DisabledGlassPane glassPane;
	/** map index of device in combobox to service device index */
	private List<Integer> currentDeviceIndexes;
	
	private JComboBox<String> comboService;
	private JComboBox<Integer> comboSampleRate;
	private JComboBox<Integer> comboBitRate;
	private JComboBox<Integer> comboChannels;
	private JComboBox<String> comboDevice;
	private JCheckBox chckbxShowSupportedOnly;

	public NativeSoundConfigPanel(JDialog dialog) {
		this.dialog = dialog;
		currentDeviceIndexes = new ArrayList<>();
		setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] { 1, 1, 0, 0 };
		gridBagLayout.columnWidths = new int[] { 1, 1 };
		gridBagLayout.columnWeights = new double[] { 1.0, 3.0 };
		setLayout(gridBagLayout);
		setBackground(Style.panelBackground);
		sm = Main.getInstance().getSettingsManager();
		nsound = Main.getInstance().getMusicSyncManager().getNativeSound();
		
		sm.addSetting(new SettingObject("nativesound.panel.showonlysupported", "Show only supported devices", false));

		initLayout();
		setValues();
	}
	
	public void setValues() {
		int serviceIndex = (int) sm.getSettingObject("nativesound.serviceindex").getValue();
		if(serviceIndex == -1)
			serviceIndex = 0;
		
		try (XtAudio audio = new XtAudio(null, null, null, null)) {
			List<String> listServiceNames = new ArrayList<>();
			nsound.getServices().forEach(service -> listServiceNames.add(service.getName()));
			
			Integer[] sampleRates = Arrays.stream(NativeSound.SAMPLERATES).boxed().toArray(Integer[]::new);
			Integer[] bitRates = {8, 16, 24, 32};
			Integer[] channels = {1, 2, 3, 4};
			
			comboService.setModel(new DefaultComboBoxModel<>(listServiceNames.toArray(new String[0])));
			comboSampleRate.setModel(new DefaultComboBoxModel<>(sampleRates));
			comboBitRate.setModel(new DefaultComboBoxModel<>(bitRates));
			comboChannels.setModel(new DefaultComboBoxModel<>(channels));
		}
		
		try {
			comboService.setSelectedIndex(serviceIndex);
			int samplerate = (int) sm.getSettingObject("nativesound.samplerate").getValue();
			comboSampleRate.setSelectedItem(samplerate);
			int bitrate = (int) sm.getSettingObject("nativesound.bitrate").getValue();
			comboBitRate.setSelectedItem(bitrate);
			int channels = (int) sm.getSettingObject("nativesound.channels").getValue();
			comboChannels.setSelectedItem(channels);
			chckbxShowSupportedOnly.setSelected((boolean) sm.getSettingObject("nativesound.panel.showonlysupported").getValue());
			
			updateDeviceList(serviceIndex);
			int selectedDeviceIndex = (int) sm.getSettingObject("nativesound.deviceindex").getValue();
			comboDevice.setSelectedIndex(currentDeviceIndexes.indexOf(selectedDeviceIndex));
		} catch(IllegalArgumentException e) {
		}
	}
	
	public void updateDeviceList(int serviceIndex) {
		glassPane.activate("Loading...");
		currentDeviceIndexes.clear();
		try (XtAudio audio = new XtAudio(null, null, null, null)) {
			XtService service = XtAudio.getServiceByIndex(serviceIndex);
			List<String> listDevices = new ArrayList<>();
			
			if(chckbxShowSupportedOnly.isSelected()) {
				
				XtFormat format = new XtFormat(new XtMix((int) comboSampleRate.getSelectedItem(),
						NativeSoundFormat.bitrateToSample((int) comboBitRate.getSelectedItem())),
						(int) comboChannels.getSelectedItem(), 0, 0, 0);
				
				for(int dIndex : nsound.getSupportedDevicesIndex(service, format)) {
					try(XtDevice device = service.openDevice(dIndex)) {
						listDevices.add(device.getName());
						currentDeviceIndexes.add(dIndex);
					}
				}
			} else {
				nsound.getDeviceNames(service).forEach(device -> { 
					listDevices.add(device);
					currentDeviceIndexes.add(currentDeviceIndexes.size());
				});
				comboDevice.setModel(new DefaultComboBoxModel<>(listDevices.toArray(new String[0])));
			}
			comboDevice.setModel(new DefaultComboBoxModel<>(listDevices.toArray(new String[0])));
		}
		glassPane.deactivate();
	}
	
	private ItemListener serviceChangedListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED) {
				updateDeviceList(comboService.getSelectedIndex());
			}
		}
	};
	
	private ActionListener showSupportedOnlyChanged = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			updateDeviceList(comboService.getSelectedIndex());
		}
	};
	
	private ItemListener formatChangedListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED) {
				if(chckbxShowSupportedOnly.isSelected()) {
					updateDeviceList(comboService.getSelectedIndex());
				}
			}
		}
	};
	
	public void saveConfig() {
		sm.getSettingObject("nativesound.serviceindex").setValue(comboService.getSelectedIndex());
		int deviceIndex = currentDeviceIndexes.get(comboDevice.getSelectedIndex());
		sm.getSettingObject("nativesound.deviceindex").setValue(deviceIndex);
		sm.getSettingObject("nativesound.samplerate").setValue(comboSampleRate.getSelectedItem());
		sm.getSettingObject("nativesound.bitrate").setValue(comboBitRate.getSelectedItem());
		sm.getSettingObject("nativesound.channels").setValue(comboChannels.getSelectedItem());
		sm.getSettingObject("nativesound.panel.showonlysupported").setValue(chckbxShowSupportedOnly.isSelected());
	}
	
	public boolean checkSupport() {
		try (XtAudio audio = new XtAudio(null, null, null, null)) {
			XtService service = XtAudio.getServiceByIndex(comboService.getSelectedIndex());
			XtFormat format = new XtFormat(new XtMix((int) comboSampleRate.getSelectedItem(),
					NativeSoundFormat.bitrateToSample((int) comboBitRate.getSelectedItem())),
					(int) comboChannels.getSelectedItem(), 0, 0, 0);
			int deviceIndex = currentDeviceIndexes.get(comboDevice.getSelectedIndex());
			return nsound.isDeviceSupported(service, deviceIndex, format);
		}
	}
	
	
	/**
	 * Initialize components and add them to the panel
	 */
	private void initLayout() {
		glassPane = new DisabledGlassPane();
		if(dialog != null) {
			SwingUtilities.getRootPane(dialog).setGlassPane(glassPane);
		}
		
		JLabel lblService = new JLabel("Service");
		lblService.setForeground(Style.textColor);
		GridBagConstraints gbc_lblService = new GridBagConstraints();
		gbc_lblService.anchor = GridBagConstraints.LINE_START;
		gbc_lblService.insets = new Insets(0, 0, 5, 5);
		gbc_lblService.gridx = 0;
		gbc_lblService.gridy = 0;
		add(lblService, gbc_lblService);

		comboService = new JComboBox<String>();
		comboService.addItemListener(serviceChangedListener);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.weightx = 2.0;
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 0;
		add(comboService, gbc_comboBox);

		JLabel lblFormat = new JLabel("Format");
		lblFormat.setForeground(Style.textColor);
		GridBagConstraints gbc_lblFormat = new GridBagConstraints();
		gbc_lblFormat.anchor = GridBagConstraints.LINE_START;
		gbc_lblFormat.weightx = 1.0;
		gbc_lblFormat.insets = new Insets(0, 0, 5, 5);
		gbc_lblFormat.gridx = 0;
		gbc_lblFormat.gridy = 1;
		add(lblFormat, gbc_lblFormat);

		JPanel panelFormat = new JPanel();
		panelFormat.setBackground(Style.panelBackground);
		GridBagConstraints gbc_panelFormat = new GridBagConstraints();
		gbc_panelFormat.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelFormat.insets = new Insets(0, 0, 5, 0);
		gbc_panelFormat.weightx = 2.0;
		gbc_panelFormat.gridx = 1;
		gbc_panelFormat.gridy = 1;
		add(panelFormat, gbc_panelFormat);
		GridBagLayout gbl_panelFormat = new GridBagLayout();
		gbl_panelFormat.columnWidths = new int[] { 1, 1, 1 };
		gbl_panelFormat.rowHeights = new int[] { 0, 0 };
		gbl_panelFormat.columnWeights = new double[] { 1.0 };
		gbl_panelFormat.rowWeights = new double[] { Double.MIN_VALUE, 0.0 };
		panelFormat.setLayout(gbl_panelFormat);

		{
			JLabel lblSamplerate = new JLabel("Samplerate");
			lblSamplerate.setForeground(Style.textColor);
			GridBagConstraints gbc_lblSamplerate = new GridBagConstraints();
			gbc_lblSamplerate.anchor = GridBagConstraints.LINE_START;
			gbc_lblSamplerate.weightx = 1.0;
			gbc_lblSamplerate.insets = new Insets(0, 0, 5, 5);
			gbc_lblSamplerate.gridx = 0;
			gbc_lblSamplerate.gridy = 0;
			panelFormat.add(lblSamplerate, gbc_lblSamplerate);

			JLabel lblBitrate = new JLabel("Bitrate");
			lblBitrate.setForeground(Style.textColor);
			GridBagConstraints gbc_lblBitrate = new GridBagConstraints();
			gbc_lblBitrate.anchor = GridBagConstraints.LINE_START;
			gbc_lblBitrate.weightx = 1.0;
			gbc_lblBitrate.insets = new Insets(0, 0, 5, 5);
			gbc_lblBitrate.gridx = 1;
			gbc_lblBitrate.gridy = 0;
			panelFormat.add(lblBitrate, gbc_lblBitrate);

			JLabel lblChannels = new JLabel("Channels");
			lblChannels.setForeground(Style.textColor);
			GridBagConstraints gbc_lblChannels = new GridBagConstraints();
			gbc_lblChannels.anchor = GridBagConstraints.LINE_START;
			gbc_lblChannels.insets = new Insets(0, 0, 5, 0);
			gbc_lblChannels.weightx = 1.0;
			gbc_lblChannels.gridx = 2;
			gbc_lblChannels.gridy = 0;
			panelFormat.add(lblChannels, gbc_lblChannels);

			comboSampleRate = new JComboBox<Integer>();
			comboSampleRate.addItemListener(formatChangedListener);
			GridBagConstraints gbc_comboBoxSampleRate = new GridBagConstraints();
			gbc_comboBoxSampleRate.weightx = 1.0;
			gbc_comboBoxSampleRate.insets = new Insets(0, 0, 0, 5);
			gbc_comboBoxSampleRate.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBoxSampleRate.gridx = 0;
			gbc_comboBoxSampleRate.gridy = 1;
			panelFormat.add(comboSampleRate, gbc_comboBoxSampleRate);

			comboBitRate = new JComboBox<Integer>();
			comboBitRate.addItemListener(formatChangedListener);
			GridBagConstraints gbc_comboBoxBitRate = new GridBagConstraints();
			gbc_comboBoxBitRate.weightx = 1.0;
			gbc_comboBoxBitRate.insets = new Insets(0, 0, 0, 5);
			gbc_comboBoxBitRate.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBoxBitRate.gridx = 1;
			gbc_comboBoxBitRate.gridy = 1;
			panelFormat.add(comboBitRate, gbc_comboBoxBitRate);

			comboChannels = new JComboBox<Integer>();
			comboChannels.addItemListener(formatChangedListener);
			GridBagConstraints gbc_comboChannels = new GridBagConstraints();
			gbc_comboChannels.weightx = 1.0;
			gbc_comboChannels.insets = new Insets(0, 0, 0, 5);
			gbc_comboChannels.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboChannels.gridx = 2;
			gbc_comboChannels.gridy = 1;
			panelFormat.add(comboChannels, gbc_comboChannels);
		}

		JLabel lblDevice = new JLabel("Device");
		lblDevice.setForeground(Style.textColor);
		GridBagConstraints gbc_lblDevice = new GridBagConstraints();
		gbc_lblDevice.anchor = GridBagConstraints.LINE_START;
		gbc_lblDevice.insets = new Insets(0, 0, 5, 5);
		gbc_lblDevice.gridx = 0;
		gbc_lblDevice.gridy = 2;
		add(lblDevice, gbc_lblDevice);

		comboDevice = new JComboBox<String>();
		GridBagConstraints gbc_comboBoxDevice = new GridBagConstraints();
		gbc_comboBoxDevice.insets = new Insets(0, 0, 5, 0);
		gbc_comboBoxDevice.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxDevice.gridx = 1;
		gbc_comboBoxDevice.gridy = 2;
		add(comboDevice, gbc_comboBoxDevice);

		chckbxShowSupportedOnly = new JCheckBox("Show only supported devices");
		chckbxShowSupportedOnly.addActionListener(showSupportedOnlyChanged);
		chckbxShowSupportedOnly.setBackground(Style.panelBackground);
		chckbxShowSupportedOnly.setForeground(Style.textColor);
		GridBagConstraints gbc_chckbxShowSupportedOnly = new GridBagConstraints();
		gbc_chckbxShowSupportedOnly.anchor = GridBagConstraints.LINE_START;
		gbc_chckbxShowSupportedOnly.gridx = 1;
		gbc_chckbxShowSupportedOnly.gridy = 3;
		add(chckbxShowSupportedOnly, gbc_chckbxShowSupportedOnly);
	}
	
	
	public static NativeSoundConfigPanel showDialog() {
		Frame window = JOptionPane.getRootFrame();
		JDialog dialog = new JDialog(window, "Choose a sound output or input", true);
		
		NativeSoundConfigPanel configPanel = new NativeSoundConfigPanel(dialog);
		
		JPanel panelButtons = new JPanel();
		panelButtons.setBackground(Style.panelBackground);
		
		JButton btnOk = new JButton("Ok");
		UiUtils.configureButton(btnOk);
		panelButtons.add(btnOk);
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!configPanel.checkSupport()) {
					JOptionPane.showMessageDialog(dialog, "Device not supported!\nTry to change the format or selected another one.",
							"Unsupported device", JOptionPane.ERROR_MESSAGE);
					return;
				}
				configPanel.saveConfig();
				dialog.setVisible(false);
			}
		});
		
		JButton btnCancel = new JButton("Cancel");
		UiUtils.configureButton(btnCancel);
		panelButtons.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		
		JPanel rootPanel = new JPanel(new BorderLayout());
		rootPanel.setBackground(ColorPickerStyle.colorBackground);
		
		rootPanel.add(configPanel, BorderLayout.CENTER);
		rootPanel.add(panelButtons, BorderLayout.SOUTH);
		
		dialog.setContentPane(rootPanel);
		dialog.pack();
		dialog.setMinimumSize(new Dimension(350, 250));
		dialog.setLocationRelativeTo(null);
		
		dialog.setVisible(true);
		dialog.dispose();
		return configPanel;
	}

}
