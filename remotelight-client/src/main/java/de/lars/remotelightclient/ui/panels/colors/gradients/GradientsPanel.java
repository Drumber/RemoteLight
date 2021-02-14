package de.lars.remotelightclient.ui.panels.colors.gradients;

import java.awt.BorderLayout;
import java.awt.Dimension;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.colors.ColorsSubPanel;
import de.lars.remotelightcore.utils.color.palette.Palettes;

public class GradientsPanel extends ColorsSubPanel {
	private static final long serialVersionUID = 5830817532795394338L;
	
	public GradientsPanel() {
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout());
		
		GradientsList gradientsList = new GradientsList(Palettes.getPaletteDataItems());
		gradientsList.setPreferredSize(new Dimension(200, 0));
		add(gradientsList, BorderLayout.WEST);
	}

}
