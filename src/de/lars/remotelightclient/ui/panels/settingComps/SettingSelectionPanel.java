package de.lars.remotelightclient.ui.panels.settingComps;

import javax.swing.JLabel;
import javax.swing.JRadioButton;

import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.settings.types.SettingSelection.Model;
import de.lars.remotelightclient.ui.Style;
import javax.swing.JComboBox;

import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;

public class SettingSelectionPanel extends SettingPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7949888285481977614L;
	private SettingSelection setting;
	private JComboBox<String> boxValues;
	private ButtonGroup group;

	/**
	 * Create the panel.
	 */
	public SettingSelectionPanel(SettingSelection setting) {
		this.setting = setting;
		setBackground(Style.panelBackground);
		
		JLabel lblName = new JLabel("name");
		lblName.setText(setting.getName());
		lblName.setForeground(Style.textColor);
		add(lblName);
		
		if(setting.getModel() == Model.ComboBox) {
			boxValues = new JComboBox<String>();
			boxValues.setModel(new DefaultComboBoxModel<String>(setting.getValues()));
			if(setting.getSelected() != null && Arrays.asList(setting.getValues()).contains(setting.getSelected())) {
				boxValues.setSelectedItem(setting.getSelected());
			}
			add(boxValues);
			
		} else if(setting.getModel() == Model.RadioButton) {
			group = new ButtonGroup();
			for(String s : setting.getValues()) {
				
				JRadioButton btn = new JRadioButton(s);
				btn.setForeground(Style.textColor);
				btn.setActionCommand(s);
				if(setting.getSelected() != null && s.equals(setting.getSelected())) {
					btn.setSelected(true);
				}
				group.add(btn);
				add(btn);
			}
		}
		
		if(setting.getDescription() != null) {
			JLabel lblHelp = new JLabel("");
			lblHelp.setIcon(Style.getSettingsIcon("help.png"));
			lblHelp.setToolTipText(setting.getDescription());
			add(lblHelp);
		}
	}

	@Override
	public void setValue() {
		if(setting.getModel() == Model.ComboBox) {
			setting.setSelected((String) boxValues.getSelectedItem());
		} else if(setting.getModel() == Model.RadioButton) {
			group.getSelection().getActionCommand();
		}
	}

}
