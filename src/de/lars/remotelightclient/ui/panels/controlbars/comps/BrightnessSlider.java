package de.lars.remotelightclient.ui.panels.controlbars.comps;

import javax.swing.JPanel;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.lang.i18n;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.UiUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

public class BrightnessSlider extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4537780229514767590L;
	private String textBrightness = i18n.getString("Baisc.Brightness"); //$NON-NLS-1$
	private Dimension size;

	/**
	 * Create the panel.
	 */
	public BrightnessSlider(Color c) {
		setBorder(new EmptyBorder(5, 0, 0, 0));
		setBackground(c);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		size = new Dimension(180, 30);
		
		JLabel lblBrightness = new JLabel(textBrightness);
		lblBrightness.setForeground(Style.textColor);
		lblBrightness.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(lblBrightness);
		
		JPanel bgrSlider = new JPanel();
		bgrSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
		bgrSlider.setBackground(c);
		bgrSlider.setPreferredSize(size);
		bgrSlider.setMaximumSize(size);
		add(bgrSlider);
		bgrSlider.setLayout(new BoxLayout(bgrSlider, BoxLayout.X_AXIS));
		
		JSlider slider = new JSlider();
		bgrSlider.add(slider);
		slider.setAlignmentX(Component.LEFT_ALIGNMENT);
		slider.setValue((int) Main.getInstance().getSettingsManager().getSettingObject("out.brightness").getValue()); //$NON-NLS-1$
		Main.getInstance().getOutputManager().setBrightness(slider.getValue());
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Main.getInstance().getOutputManager().setBrightness(slider.getValue());
				Main.getInstance().getSettingsManager().getSettingObject("out.brightness").setValue(slider.getValue()); //$NON-NLS-1$
				lblBrightness.setText(textBrightness + " " + slider.getValue() + "%"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
		slider.setBackground(c);
		slider.setForeground(Style.accent);
		slider.setFocusable(false);
		slider.setMajorTickSpacing(0);
		slider.setPaintTicks(true);
		slider.setPaintTrack(true);
		lblBrightness.setText(textBrightness + " " + slider.getValue() + "%"); //$NON-NLS-1$ //$NON-NLS-2$
		UiUtils.addSliderMouseWheelListener(slider);
	}

}
