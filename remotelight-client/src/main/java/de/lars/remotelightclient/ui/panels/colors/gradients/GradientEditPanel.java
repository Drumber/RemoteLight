package de.lars.remotelightclient.ui.panels.colors.gradients;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import de.lars.colorpicker.ColorPicker;
import de.lars.colorpicker.listener.ColorListener;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightcore.utils.color.palette.PaletteData;

public class GradientEditPanel extends JPanel {
	private static final long serialVersionUID = 7638664136509067917L;
	
	private GradientBar gradientBar;
	private PaletteData palette;
	private JTextField fieldName;
	
	public GradientEditPanel() {
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout());
		
		JPanel panelHeader = new JPanel();
		panelHeader.setBackground(Style.panelBackground);
		panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
		add(panelHeader, BorderLayout.NORTH);
		
		fieldName = new JTextField();
		fieldName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		fieldName.setPreferredSize(new Dimension(0, 40));
		fieldName.setBackground(Style.panelDarkBackground);
		fieldName.setForeground(Style.textColor);
		fieldName.setCaretColor(Style.accent);
		fieldName.setFont(Style.getFontRegualar(16));
		fieldName.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, Style.textColorDarker), 
				BorderFactory.createEmptyBorder(5, 10, 0, 10)));
		panelHeader.add(fieldName);
		
		gradientBar = new GradientBar(null);
		gradientBar.setMinimumSize(new Dimension(100, 20));
		gradientBar.setPreferredSize(new Dimension(0, 30));
		gradientBar.setShowMarkers(true);
		gradientBar.setCornerRadius(5);
		gradientBar.setPaddingHorizontal(5);
		panelHeader.add(Box.createVerticalStrut(20)); // add spacer
		panelHeader.add(gradientBar);
		
		JPanel panelSetup = new JPanel();
		panelSetup.setBackground(Style.panelBackground);
		panelSetup.setLayout(new GridBagLayout());
		
		TScrollPane scrollPane = new TScrollPane(panelSetup);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		add(scrollPane, BorderLayout.CENTER);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipady = 200;
		
		ColorPicker colorPicker =  new ColorPicker(Color.RED, 0, true, true, false, false);
		colorPicker.addColorListener(colorChangeListener);
		colorPicker.setPreferredSize(new Dimension(100, 100));
		panelSetup.add(colorPicker, gbc);
		
	}
	
	private ColorListener colorChangeListener = new ColorListener() {
		@Override
		public void onColorChanged(Color color) {
			// TODO
		}
	};
	
	public void updateValues() {
		if(palette != null) {
			fieldName.setText(palette.getName());
			gradientBar.setColorPalette(palette.getPalette());
			gradientBar.resetMarkerSelection();
			gradientBar.repaint();
		}
	}

	public PaletteData getPalette() {
		return palette;
	}

	public void setPalette(PaletteData palette) {
		this.palette = palette;
	}

}
