package de.lars.remotelightcore.settings.types;

import java.util.Arrays;

import de.lars.remotelightcore.colors.palette.Palettes;
import de.lars.remotelightcore.colors.palette.model.AbstractPalette;
import de.lars.remotelightcore.colors.palette.model.PaletteData;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;

public class SettingGradient extends SettingSelection {
	private static final long serialVersionUID = 7237931367256370494L;

	public SettingGradient(String id, String name, SettingCategory category, String description, String selected,
			Model model) {
		super(id, name, category, description, null, selected, model);
	}

	public SettingGradient(String id, String name, String description, String selected) {
		super(id, name, SettingCategory.Intern, description, null, selected, Model.ComboBox);
		if(selected == null) {
			set(Palettes.getNames().size() > 0 ? getGradientNames()[0] : null);
		}
	}

	public String[] getValues() {
		return getGradientNames();
	}

	/**
	 * Not supported by this subclass.
	 */
	@Override
	public void setValues(String[] values) {
	}

	@Override
	public String get() {
		return getSelected();
	}

	public String getSelected() {
		String selected = super.getSelected();
		if (!Palettes.getNames().contains(selected)) {
			selected = getFirstGradient();
			setSelected(selected);
		}
		return selected;
	}

	public int getSelectedIndex() {
		return Arrays.asList(getGradientNames()).indexOf(getSelected());
	}
	
	public PaletteData getPaletteData() {
		return Palettes.getPalette(getSelected());
	}
	
	public AbstractPalette getPalette() {
		PaletteData data = getPaletteData();
		if(data != null) {
			return data.getPalette();
		}
		return null;
	}
	
	public static String getFirstGradient() {
		return Palettes.getNames().size() > 0 ? getGradientNames()[0] : null;
	}

	public static String[] getGradientNames() {
		return Palettes.getNames().toArray(new String[0]);
	}

}
