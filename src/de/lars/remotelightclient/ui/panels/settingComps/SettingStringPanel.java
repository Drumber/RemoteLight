package de.lars.remotelightclient.ui.panels.settingComps;

import de.lars.remotelightclient.settings.types.SettingString;
import de.lars.remotelightclient.ui.Style;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class SettingStringPanel extends SettingPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7949888285481977614L;
	private SettingString setting;
	private JTextField fieldValue;

	/**
	 * Create the panel.
	 */
	public SettingStringPanel(SettingString setting) {
		this.setting = setting;
		setBackground(Style.panelBackground);
		
		JLabel lblName = new JLabel("name");
		lblName.setText(setting.getName());
		lblName.setForeground(Style.textColor);
		add(lblName);
		
		fieldValue = new JTextField();
		fieldValue.setText(setting.getValue());
		add(fieldValue);
		fieldValue.setColumns(20);
		
		if(setting.getDescription() != null) {
			JLabel lblHelp = new JLabel("");
			lblHelp.setIcon(Style.getSettingsIcon("help.png"));
			lblHelp.setToolTipText(setting.getDescription());
			add(lblHelp);
		}
	}
	

	@Override
	public void setValue() {
		setting.setValue(fieldValue.getText());
	}

}
