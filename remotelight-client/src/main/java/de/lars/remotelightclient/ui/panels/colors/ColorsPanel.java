/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightclient.ui.panels.colors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.lars.colorpicker.ColorPicker;
import de.lars.colorpicker.listener.ColorListener;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingObject;

public class ColorsPanel extends MenuPanel {
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
			Main.getInstance().getCore().getColorManager().showColor(color);
		}
	};
	
	
	@Override
	public void onEnd(MenuPanel newPanel) {
		sm.getSettingObject("colorspanel.colors").setValue(ColorPicker.paletteColors); //$NON-NLS-1$
		super.onEnd(newPanel);
	}
	
	@Override
	public String getName() {
		return i18n.getString("Basic.Colors");
	}

}
