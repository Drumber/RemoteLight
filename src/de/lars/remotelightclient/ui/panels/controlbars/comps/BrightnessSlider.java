package de.lars.remotelightclient.ui.panels.controlbars.comps;

import javax.swing.JPanel;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.UiUtils;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class BrightnessSlider extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4537780229514767590L;
	private String textBrightness = "Brightness";

	/**
	 * Create the panel.
	 */
	public BrightnessSlider(Color c) {
		setBackground(c);
		setLayout(new GridLayout(2, 0, 0, 0));
		
		JLabel lblBrightness = new JLabel(textBrightness);
		lblBrightness.setForeground(Style.textColor);
		add(lblBrightness);
		
		JSlider slider = new JSlider();
		slider.setValue((int) Main.getInstance().getSettingsManager().getSettingObject("out.brightness").getValue());
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Main.getInstance().getOutputManager().setBrightness(slider.getValue());
				Main.getInstance().getSettingsManager().getSettingObject("out.brightness").setValue(slider.getValue());
				lblBrightness.setText(textBrightness + " " + slider.getValue() + "%");
			}
		});
		slider.setBackground(c);
		slider.setForeground(Style.accent);
		slider.setFocusable(false);
		slider.setMajorTickSpacing(0);
		slider.setPaintTicks(true);
		slider.setPaintTrack(true);
		lblBrightness.setText(textBrightness + " " + slider.getValue() + "%");
		UiUtils.addSliderMouseWheelListener(slider);
		add(slider);
	}

}
