package de.lars.remotelightplugincompat;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.JSpinner;

import de.lars.remotelightclient.utils.ui.UiUtils;

/**
 * Compatibility class for {@link UiUtils} before commit #ff537c41d3f651c59ca619337fff5fc8771c0c25.
 */
public class UiUtilsCompat {
	
	private static boolean isThemingDisabled() {
		return !UiUtils.isThemingEnabled();
	}

	public static void configureButton(JButton btn) {
		configureButton(btn, true);
	}

	public static void configureButton(JButton btn, boolean hoverListener) {
		if (isThemingDisabled()) {
			btn.setContentAreaFilled(true);
			btn.setFocusable(false);
			return;
		}
		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setFocusable(true);
		btn.setOpaque(true);
		btn.setBackground(StyleCompat.buttonBackground());
		btn.setForeground(StyleCompat.textColor());
		if (hoverListener)
			btn.addMouseListener(buttonHoverListener);
	}

	public static void configureButtonWithBorder(JButton btn, Color border) {
		btn.setBorderPainted(true);
		btn.setBorder(BorderFactory.createLineBorder(border));
		if (isThemingDisabled())
			return;
		btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.setOpaque(true);
		btn.setBackground(StyleCompat.buttonBackground());
		btn.setForeground(StyleCompat.textColor());
		btn.addMouseListener(buttonHoverListener);
	}

	private static MouseAdapter buttonHoverListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			if (isThemingDisabled())
				return;
			JButton btn = (JButton) e.getSource();
			if (btn.isEnabled()) {
				btn.setBackground(StyleCompat.hoverBackground());
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (isThemingDisabled())
				return;
			JButton btn = (JButton) e.getSource();
			btn.setBackground(StyleCompat.buttonBackground());
		}
	};

	public static void configureSpinner(JSpinner spinner) {
		// set width (columns)
		JComponent editor = spinner.getEditor();
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) editor).getTextField();
		jftf.setColumns(4);
		spinner.setEditor(editor);
	}

	public static void configureMenuItem(JMenuItem item) {
		item.setBackground(StyleCompat.panelAccentBackground());
		item.setContentAreaFilled(false);
		item.setForeground(StyleCompat.textColor());
		item.setOpaque(UiUtils.isThemingEnabled());
	}

	public static void addHoverColor(JComponent comp, Color main, Color hover) {
		comp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				((JComponent) e.getSource()).setBackground(hover);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				((JComponent) e.getSource()).setBackground(main);
			}
		});
	}

	public static void configureSlider(JSlider slider) {
		if (isThemingDisabled())
			return;
		Color background = StyleCompat.panelBackground();
		if (slider.getParent() != null) {
			background = slider.getParent().getBackground();
		}
		slider.setBackground(background);
		slider.setForeground(StyleCompat.accent());
	}

	public static void addSliderMouseWheelListener(JSlider slider) {
		slider.addMouseWheelListener(sliderWheelListener);
	}

	private static MouseWheelListener sliderWheelListener = new MouseWheelListener() {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			JSlider slider = (JSlider) e.getSource();
			int notches = e.getWheelRotation();
			if (notches < 0) {
				slider.setValue(slider.getValue() + 1);
			} else if (notches > 0) {
				slider.setValue(slider.getValue() - 1);
			}
		}
	};

}
