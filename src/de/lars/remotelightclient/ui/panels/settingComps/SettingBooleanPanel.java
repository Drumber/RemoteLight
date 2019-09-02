package de.lars.remotelightclient.ui.panels.settingComps;

import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.ui.Style;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

public class SettingBooleanPanel extends SettingPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7949888285481977614L;
	private SettingBoolean setting;
	private JCheckBox checkBox;

	/**
	 * Create the panel.
	 */
	public SettingBooleanPanel(SettingBoolean setting) {
		this.setting = setting;
		setBackground(Style.panelBackground);
		
		JLabel lblName = new JLabel("name");
		lblName.setText(setting.getName());
		lblName.setForeground(Style.textColor);
		add(lblName);
		
		checkBox = new JCheckBox("");
		checkBox.setOpaque(false);
		checkBox.setSelected(setting.getValue());
		add(checkBox);
		
		if(setting.getDescription() != null) {
			JLabel lblHelp = new JLabel("");
			lblHelp.setIcon(Style.getSettingsIcon("help.png"));
			lblHelp.setToolTipText(setting.getDescription());
			add(lblHelp);
		}
	}

	@Override
	public void setValue() {
		setting.setValue(checkBox.isSelected());
	}

}
