package de.lars.remotelightclient.ui.panels.colors.gradients;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.colors.ColorsSubPanel;
import de.lars.remotelightcore.utils.color.palette.ColorGradient;
import de.lars.remotelightcore.utils.color.palette.PaletteData;
import de.lars.remotelightcore.utils.color.palette.Palettes;

public class GradientsPanel extends ColorsSubPanel {
	private static final long serialVersionUID = 5830817532795394338L;
	
	private GradientsList gradientsList;
	private GradientEditPanel gradientEditPanel;
	
	public GradientsPanel() {
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout());
		
		gradientsList = new GradientsList(Palettes.getPaletteDataItems());
		gradientsList.setPreferredSize(new Dimension(200, 0));
		gradientsList.getJListComponent().addListSelectionListener(onGradientListSelection);
		add(gradientsList, BorderLayout.WEST);
		
		gradientEditPanel = new GradientEditPanel();
		gradientEditPanel.setVisible(false);
		add(gradientEditPanel, BorderLayout.CENTER);
	}

	private ListSelectionListener onGradientListSelection = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting()) {
				PaletteData selectedPalette = gradientsList.getJListComponent().getSelectedValue();
				gradientEditPanel.setPalette(selectedPalette);
				gradientEditPanel.updateValues();
				gradientEditPanel.setVisible(true);
				// TODO: only for testing, remove later
				if(selectedPalette.getPalette() instanceof ColorGradient) {
					Main.getInstance().getCore().getColorManager().showGradient(selectedPalette.getPalette());
				}
			}
		}
	};
	
}
