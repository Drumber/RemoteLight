package de.lars.remotelightclient.ui.panels.settings.settingComps;

import javax.swing.JColorChooser;
import javax.swing.JLabel;

import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.ui.Style;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingColorPanel extends SettingPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7949888285481977614L;
	private SettingColor setting;
	private JPanel panelColor;

	/**
	 * Create the panel.
	 */
	public SettingColorPanel(SettingColor setting) {
		this.setting = setting;
		setBackground(Style.panelBackground);
		
		JLabel lblName = new JLabel("name");
		lblName.setText(setting.getName());
		lblName.setForeground(Style.textColor);
		add(lblName);
		
		panelColor = new JPanel();
		panelColor.setBackground(setting.getValue());
		panelColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Color color = JColorChooser.showDialog(null, "Choose a color", panelColor.getBackground());
				if(color != null) {
					panelColor.setBackground(color);
				}
			}
		});
		panelColor.setPreferredSize(new Dimension(15, 15));
		add(panelColor);
		
		if(setting.getDescription() != null) {
			JLabel lblHelp = new JLabel("");
			lblHelp.setIcon(Style.getSettingsIcon("help.png"));
			lblHelp.setToolTipText(setting.getDescription());
			add(lblHelp);
		}
	}

	@Override
	public void setValue() {
		setting.setValue(panelColor.getBackground());
	}

}
