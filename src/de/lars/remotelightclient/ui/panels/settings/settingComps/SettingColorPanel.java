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

import de.lars.colorpicker.ColorPicker;
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
		super(setting);
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
				Color color = ColorPicker.showSimpleDialog("Choose a color", panelColor.getBackground(), true);
				if(color != null) {
					panelColor.setBackground(color);
					SettingColorPanel.this.onChanged(SettingColorPanel.this);
				}
			}
		});
		panelColor.setPreferredSize(new Dimension(15, 15));
		add(panelColor);
		
		if(setting.getDescription() != null && !setting.getDescription().isEmpty()) {
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

	@Override
	public void updateComponents() {
		panelColor.setBackground(setting.getValue());
	}

}
