package de.lars.remotelightclient.ui.panels.colors;

import java.awt.BorderLayout;
import java.awt.Color;

import de.lars.colorpicker.ColorPicker;
import de.lars.colorpicker.listener.ColorListener;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.utils.ColorTool;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingObject;

public class ColorPickerPanel extends ColorsSubPanel {
	private static final long serialVersionUID = -2251690971830742756L;

	private Color[] defaultColors = {Color.ORANGE, Color.RED, Color.MAGENTA, Color.GREEN, Color.BLUE, Color.CYAN, Color.WHITE, Color.BLACK};
	private Color[] colors;
	private SettingsManager sm = Main.getInstance().getSettingsManager();
	
	public ColorPickerPanel() {
		sm.addSetting(new SettingObject("colorspanel.colors", null, defaultColors));
		colors = (Color[]) sm.getSettingObject("colorspanel.colors").get();
		
		setLayout(new BorderLayout(0, 0));
		
		ColorPicker.paletteColors = colors;
		
		ColorPicker colorPicker =  new ColorPicker(colors.length > 0 ? colors[0] : Color.RED);
		colorPicker.setMaxPaletteItems(30);
		colorPicker.addColorListener(colorChangeListener);
		add(colorPicker, BorderLayout.CENTER);
	}
	
	private ColorListener colorChangeListener = new ColorListener() {
		@Override
		public void onColorChanged(Color color) {
			Main.getInstance().getCore().getColorManager().showColor(ColorTool.convert(color));
		}
	};
	
	
	@Override
	public void onEnd() {
		sm.getSettingObject("colorspanel.colors").setValue(ColorPicker.paletteColors); //$NON-NLS-1$
	}
	
}
