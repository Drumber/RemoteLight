package de.lars.remotelightclient.ui.panels.settings.settingComps;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.ui.Style;

public class SettingIntPanel extends SettingPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7949888285481977614L;
	private SettingInt setting;
	private JSpinner spinner;

	/**
	 * Create the panel.
	 */
	public SettingIntPanel(SettingInt setting) {
		this.setting = setting;
		setBackground(Style.panelBackground);
		
		JLabel lblName = new JLabel("name");
		lblName.setText(setting.getName());
		lblName.setForeground(Style.textColor);
		add(lblName);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(setting.getValue(), setting.getMin(), setting.getMax(), setting.getStepsize()));
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				SettingIntPanel.this.onChanged(setting);
			}
		});
		add(spinner);
		
		if(setting.getDescription() != null && !setting.getDescription().isEmpty()) {
			JLabel lblHelp = new JLabel("");
			lblHelp.setIcon(Style.getSettingsIcon("help.png"));
			lblHelp.setToolTipText(setting.getDescription());
			add(lblHelp);
		}
	}

	@Override
	public void setValue() {
		setting.setValue((int) spinner.getValue());
	}

}
