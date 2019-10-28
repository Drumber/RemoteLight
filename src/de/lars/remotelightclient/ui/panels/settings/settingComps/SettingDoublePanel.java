/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient.ui.panels.settings.settingComps;

import javax.swing.JLabel;

import de.lars.remotelightclient.settings.types.SettingDouble;
import de.lars.remotelightclient.ui.Style;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SettingDoublePanel extends SettingPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7949888285481977614L;
	private SettingDouble setting;
	private JSpinner spinner;

	/**
	 * Create the panel.
	 */
	public SettingDoublePanel(SettingDouble setting) {
		super(setting);
		setBackground(Style.panelBackground);
		this.setting = setting;
		setBackground(Style.panelBackground);
		
		JLabel lblName = new JLabel("name");
		lblName.setText(setting.getName());
		lblName.setForeground(Style.textColor);
		add(lblName);
		
		if(setting.getValue() > setting.getMax()) {
			setting.setValue(setting.getMax());
		}
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(setting.getValue(), setting.getMin(), setting.getMax(), setting.getStepsize()));
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				SettingDoublePanel.this.onChanged(SettingDoublePanel.this);
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
		setting.setValue((double) spinner.getValue());
	}

	@Override
	public void updateComponents() {
		spinner.setModel(new SpinnerNumberModel(setting.getValue(), setting.getMin(), setting.getMax(), setting.getStepsize()));
	}

}
