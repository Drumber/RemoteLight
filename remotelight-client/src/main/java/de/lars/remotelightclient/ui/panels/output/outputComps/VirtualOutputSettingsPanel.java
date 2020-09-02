package de.lars.remotelightclient.ui.panels.output.outputComps;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.devices.arduino.RgbOrder;
import de.lars.remotelightcore.devices.virtual.VirtualOutput;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.out.OutputManager;

public class VirtualOutputSettingsPanel extends DeviceSettingsPanel {
	private static final long serialVersionUID = -3968905553069494626L;
	
	private VirtualOutput virtual;
	private JTextField fieldId;
	private JSpinner spinnerPixels;
	private JComboBox<RgbOrder> comboOrder;
	private JSpinner spinnerShift;
	private JSpinner spinnerClone;
	private JCheckBox checkboxCloneMirrored;
	private Dimension size;

	/**
	 * Create the panel.
	 */
	public VirtualOutputSettingsPanel(VirtualOutput virtual, boolean setup) {
		super(virtual, setup);
		this.virtual = virtual;
		size = new Dimension(800, 40);
		setBackground(Style.panelBackground);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel panelId = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelId.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelId.setPreferredSize(size);
		panelId.setMaximumSize(size);
		panelId.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelId.setBackground(Style.panelBackground);
		add(panelId);
		
		JLabel lblNameId = new JLabel(i18n.getString("OutputPanel.NameID")); //$NON-NLS-1$
		lblNameId.setForeground(Style.textColor);
		panelId.add(lblNameId);
		
		fieldId = new JTextField();
		panelId.add(fieldId);
		fieldId.setColumns(10);
		
		JPanel panelPixels = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panelPixels.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		panelPixels.setPreferredSize(size);
		panelPixels.setMaximumSize(size);
		panelPixels.setBackground(Style.panelBackground);
		panelPixels.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panelPixels);
		
		JLabel lblPixels = new JLabel(i18n.getString("OutputPanel.Pixels")); //$NON-NLS-1$
		lblPixels.setForeground(Style.textColor);
		panelPixels.add(lblPixels);
		
		spinnerPixels = new JSpinner();
		spinnerPixels.setModel(new SpinnerNumberModel(new Integer(OutputManager.MIN_PIXELS), new Integer(OutputManager.MIN_PIXELS), null, new Integer(1)));
		UiUtils.configureSpinner(spinnerPixels);
		panelPixels.add(spinnerPixels);
		
		JPanel panelOrder = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panelOrder.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		panelOrder.setPreferredSize(size);
		panelOrder.setMaximumSize(size);
		panelOrder.setBackground(Style.panelBackground);
		panelOrder.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panelOrder);
		
		JLabel lblRgbOrder = new JLabel(i18n.getString("OutputPanel.RgbOrder"));
		lblRgbOrder.setForeground(Style.textColor);
		panelOrder.add(lblRgbOrder);
		
		comboOrder = new JComboBox<RgbOrder>();
		comboOrder.setModel(new DefaultComboBoxModel<>(RgbOrder.values()));
		panelOrder.add(comboOrder);
		
		JLabel lblOutputPatch = new JLabel(i18n.getString("OutputPanel.OutputPatch"), SwingConstants.LEFT);
		lblOutputPatch.setFont(Style.getFontBold(11));
		lblOutputPatch.setForeground(Style.textColor);
		lblOutputPatch.setBorder(new EmptyBorder(5, 5, 0, 0));
		add(lblOutputPatch);
		
		JPanel panelShift = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) panelShift.getLayout();
		flowLayout_4.setAlignment(FlowLayout.LEFT);
		panelShift.setPreferredSize(new Dimension(800, 40));
		panelShift.setMaximumSize(new Dimension(800, 40));
		panelShift.setBackground(Style.panelBackground);
		panelShift.setAlignmentX(0.0f);
		add(panelShift);
		
		JLabel lblShift = new JLabel(i18n.getString("OutputPanel.ShiftPixels"));
		lblShift.setForeground(Style.textColor);
		panelShift.add(lblShift);
		
		spinnerShift = new JSpinner();
		spinnerShift.setModel(new SpinnerNumberModel(virtual.getOutputPatch().getShift(), -virtual.getPixels(), virtual.getPixels(), 1));
		UiUtils.configureSpinner(spinnerShift);
		spinnerShift.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int max = (int) spinnerPixels.getValue() - 1;
				spinnerShift.setModel(new SpinnerNumberModel((int) spinnerShift.getValue(), -max, max, 1));
			}
		});
		panelShift.add(spinnerShift);
		
		JLabel lblClone = new JLabel(i18n.getString("OutputPanel.Clone"));
		lblClone.setForeground(Style.textColor);
		panelShift.add(lblClone);
		
		spinnerClone = new JSpinner();
		spinnerClone.setModel(new SpinnerNumberModel(virtual.getOutputPatch().getClone(), 0, virtual.getPixels() / 2, 1));
		UiUtils.configureSpinner(spinnerClone);
		spinnerClone.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				spinnerClone.setModel(new SpinnerNumberModel((Number) spinnerClone.getValue(), 0, virtual.getPixels() / 2, 1));
			}
		});
		panelShift.add(spinnerClone);
		
		checkboxCloneMirrored = new JCheckBox(i18n.getString("OutputPanel.Mirror"));
		checkboxCloneMirrored.setBackground(Style.panelBackground);
		checkboxCloneMirrored.setForeground(Style.textColor);
		checkboxCloneMirrored.setSelected(virtual.getOutputPatch().isCloneMirrored());
		panelShift.add(checkboxCloneMirrored);
		
		setValues();
	}
	
	private void setValues() {
		if(virtual.getId() != null) {
			fieldId.setText(virtual.getId());
		}
		spinnerPixels.setValue(virtual.getPixels());
		
		if(virtual.getRgbOrder() == null) {
			virtual.setRgbOrder(RgbOrder.GRB);
		}
		comboOrder.setSelectedItem(virtual.getRgbOrder());
	}

	@Override
	public boolean save() {
		if(fieldId.getText().isEmpty()) {
			return false;
		}
		virtual.setId(fieldId.getText());
		virtual.setPixels((int) spinnerPixels.getValue());
		virtual.setRgbOrder((RgbOrder) comboOrder.getSelectedItem());
		virtual.getOutputPatch().setShift((int) spinnerShift.getValue());
		virtual.getOutputPatch().setClone((int) spinnerClone.getValue());
		virtual.getOutputPatch().setCloneMirrored(checkboxCloneMirrored.isSelected());
		return true;
	}

	@Override
	public String getId() {
		return fieldId.getText();
	}

}
