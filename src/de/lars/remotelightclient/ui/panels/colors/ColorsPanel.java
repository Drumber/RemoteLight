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
package de.lars.remotelightclient.ui.panels.colors;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.lars.colorpicker.ColorPicker;
import de.lars.colorpicker.listener.ColorListener;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.utils.color.PixelColorUtils;

import java.awt.BorderLayout;

public class ColorsPanel extends MenuPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2572544853394733969L;
	private Color[] defaultColors = {Color.ORANGE, Color.RED, Color.MAGENTA, Color.GREEN, Color.BLUE, Color.CYAN, Color.WHITE, Color.BLACK};
	private List<Color> colors;
	private SettingsManager sm = Main.getInstance().getSettingsManager();

	/**
	 * Create the panel.
	 */
	public ColorsPanel() {
		Main.getInstance().getMainFrame().showControlBar(true);
		Main.getInstance().getMainFrame().setControlBarPanel(new DefaultControlBar());
		colors = new ArrayList<>();
		
		sm.addSetting(new SettingObject("colorspanel.colors", null, defaultColors)); //register setting if not already registered //$NON-NLS-1$
		colors = new LinkedList<>(Arrays.asList((Color[]) sm.getSettingObject("colorspanel.colors").getValue())); //$NON-NLS-1$
		
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout(0, 0));
		
		ColorPicker.paletteColors = colors.toArray(new Color[colors.size()]);
		
		ColorPicker colorPicker =  new ColorPicker(colors.size() > 0 ? colors.get(0) : Color.RED);
		colorPicker.setMaxPaletteItems(30);
		colorPicker.addColorListener(colorChangeListener);
		add(colorPicker, BorderLayout.CENTER);
	}
	
	
	private ColorListener colorChangeListener = new ColorListener() {
		@Override
		public void onColorChanged(Color color) {
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(color, Main.getLedNum()));
		}
	};
	
	
	@Override
	public void onEnd(MenuPanel newPanel) {
		sm.getSettingObject("colorspanel.colors").setValue(ColorPicker.paletteColors); //$NON-NLS-1$
		super.onEnd(newPanel);
	}

}
