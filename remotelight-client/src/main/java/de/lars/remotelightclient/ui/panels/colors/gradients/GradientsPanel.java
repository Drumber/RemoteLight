package de.lars.remotelightclient.ui.panels.colors.gradients;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.colors.ColorsSubPanel;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.utils.color.palette.AbstractPalette;
import de.lars.remotelightcore.utils.color.palette.ColorGradient;
import de.lars.remotelightcore.utils.color.palette.EvenGradientPalette;
import de.lars.remotelightcore.utils.color.palette.GradientPalette;
import de.lars.remotelightcore.utils.color.palette.PaletteData;
import de.lars.remotelightcore.utils.color.palette.Palettes;

public class GradientsPanel extends ColorsSubPanel {
	private static final long serialVersionUID = 5830817532795394338L;
	
	private GradientsList gradientsList;
	private GradientEditPanel gradientEditPanel;
	private GradientListHolder gradientsListHolder;
	
	public GradientsPanel() {
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout());
		
		gradientsList = new GradientsList(Palettes.getPaletteDataItems());
		gradientsList.setPreferredSize(new Dimension(200, 0));
		gradientsList.getJListComponent().addListSelectionListener(onGradientListSelection);
		
		gradientsListHolder = new GradientListHolder();
		gradientsListHolder.add(gradientsList, BorderLayout.CENTER);
		add(gradientsListHolder, BorderLayout.WEST);
		
		gradientEditPanel = new GradientEditPanel();
		gradientEditPanel.setVisible(false);
		add(gradientEditPanel, BorderLayout.CENTER);
	}

	private ListSelectionListener onGradientListSelection = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting()) {
				PaletteData selectedPalette = gradientsList.getJListComponent().getSelectedValue();
				setSelectedPalette(selectedPalette);
				// TODO: only for testing, remove later
				if(selectedPalette.getPalette() instanceof ColorGradient) {
					Main.getInstance().getCore().getColorManager().showGradient(selectedPalette.getPalette());
				}
			}
		}
	};
	
	/**
	 * Sets the palette that should be displayed in the {@link GradientEditPanel}.
	 * @param palette		PaletteData that should be edited
	 */
	public void setSelectedPalette(PaletteData palette) {
		gradientEditPanel.setPalette(palette);
		gradientEditPanel.updateValues();
		gradientEditPanel.setVisible(true);
		gradientsListHolder.btnRemove.setEnabled(true);
	}
	
	
	public void addGradient(boolean simpleGradient) {
		AbstractPalette palette = (simpleGradient ? EvenGradientPalette.createDefault() : GradientPalette.createDefault());
		PaletteData newPalette = new PaletteData("New " + (simpleGradient ? "Simple Gradient" : "Gradient"), palette);
		setSelectedPalette(newPalette);
	}
	
	public void removeSelectedGradient() {
		// TODO: remove palette from JList and from Palettes class
	}
	
	
	/**
	 * JPanel that holds the palette JList and two buttons:
	 * <li>Add-Button (with a popup menu) for creating new palettes
	 * <li>Remove-Button for removing current selected palette
	 */
	private class GradientListHolder extends JPanel {
		private static final long serialVersionUID = -4371032649976677203L;
		private final JButton btnRemove;
		
		public GradientListHolder() {
			setLayout(new BorderLayout());
			setBackground(Style.panelBackground);
			
			JPanel panelWrapper = new JPanel();
			panelWrapper.setLayout(new GridLayout(1, 2));
			panelWrapper.setBackground(getBackground());
			add(panelWrapper, BorderLayout.SOUTH);
			
			JButton btnAdd = new JButton("Add");
			UiUtils.configureButton(btnAdd);
			btnAdd.addActionListener(l -> addGradient(true));
			panelWrapper.add(btnAdd);
			
			btnRemove = new JButton("Remove");
			UiUtils.configureButton(btnRemove);
			btnRemove.setEnabled(false);
			btnRemove.addActionListener(l -> removeSelectedGradient());
			panelWrapper.add(btnRemove);
			
			JPopupMenu popupMenu = new JPopupMenu();
			popupMenu.setBackground(Style.panelAccentBackground);
			add(popupMenu);
			
			JMenuItem mItemSimpleGradient = new JMenuItem("Simple Gradient");
			UiUtils.configureMenuItem(mItemSimpleGradient);
			mItemSimpleGradient.addActionListener(l -> addGradient(true));
			popupMenu.add(mItemSimpleGradient);
			
			JMenuItem mItemGradient = new JMenuItem("Gradient");
			UiUtils.configureMenuItem(mItemGradient);
			popupMenu.add(mItemGradient);
			
			btnAdd.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					// show popup menu on top of the button on hover
					int popupHeight = popupMenu.getPreferredSize().height;
					popupMenu.show(e.getComponent(), 0, -popupHeight);
				};
				
				public void mouseClicked(MouseEvent e) {
					// default add EventGradientPalette on button click
					addGradient(false);
				};
			});
			
			MouseAdapter popupCloseMouseAdapter = new MouseAdapter() {
				public void mouseExited(MouseEvent e) {
					// hide popup menu when leaving button or popup
					if((e.getComponent() instanceof JButton && e.getY() > 1) ||
							(e.getComponent() instanceof JPopupMenu && e.getY() < 1)) {
						popupMenu.setVisible(false);
					}
				};
			};
			
			btnAdd.addMouseListener(popupCloseMouseAdapter);
			popupMenu.addMouseListener(popupCloseMouseAdapter);
		}
	}
	
}
