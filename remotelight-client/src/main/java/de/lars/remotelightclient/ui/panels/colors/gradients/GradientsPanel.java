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

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.colors.ColorsSubPanel;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.colors.palette.Palettes;
import de.lars.remotelightcore.colors.palette.model.AbstractPalette;
import de.lars.remotelightcore.colors.palette.model.ColorGradient;
import de.lars.remotelightcore.colors.palette.model.EvenGradientPalette;
import de.lars.remotelightcore.colors.palette.model.GradientPalette;
import de.lars.remotelightcore.colors.palette.model.PaletteData;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.utils.ArrayUtil;

public class GradientsPanel extends ColorsSubPanel {
	private static final long serialVersionUID = 5830817532795394338L;
	
	private GradientsList gradientsList;
	private GradientEditPanel gradientEditPanel;
	private GradientListHolder gradientsListHolder;
	
	public GradientsPanel() {
		setLayout(new BorderLayout());
		
		gradientsList = new GradientsList(Palettes.getAll());
		gradientsList.setPreferredSize(new Dimension(200, 0));
		gradientsList.getJListComponent().addListSelectionListener(onGradientListSelection);
		
		gradientsListHolder = new GradientListHolder();
		gradientsListHolder.add(gradientsList, BorderLayout.CENTER);
		add(gradientsListHolder, BorderLayout.WEST);
		
		gradientEditPanel = new GradientEditPanel();
		gradientEditPanel.setVisible(false);
		gradientEditPanel.setPaletteChangeListener(onPaletteChange);
		add(gradientEditPanel, BorderLayout.CENTER);
	}

	private ListSelectionListener onGradientListSelection = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting() && gradientsList.getJListComponent().getSelectedIndex() != -1) {
				PaletteData selectedPalette = gradientsList.getJListComponent().getSelectedValue();
				setSelectedPalette(selectedPalette);
			}
		}
	};
	
	private PaletteChangeListener onPaletteChange = new PaletteChangeListener() {
		@Override
		public void onPaletteChange(PaletteData eventPalette, boolean nameOnly) {
			int selectedIndex = gradientsList.getJListComponent().getSelectedIndex();
			if(selectedIndex != -1) {
				gradientsList.updateSingleListElement(selectedIndex, eventPalette);
			}
			if(!nameOnly && eventPalette.getPalette() instanceof ColorGradient) {
				Main.getInstance().getCore().getColorManager().showGradient(eventPalette.getPalette());
			}
		}
	};
	
	/**
	 * Sets the palette that should be displayed in the {@link GradientEditPanel}.
	 * @param palette		PaletteData that should be edited or {@code null} if
	 * 						palette edit panel should be hidden.
	 */
	public void setSelectedPalette(PaletteData palette) {
		gradientEditPanel.setPalette(palette);
		gradientEditPanel.updateValues();
		boolean notNull = palette != null;
		gradientEditPanel.setVisible(notNull);
		gradientsListHolder.btnRemove.setEnabled(notNull);
	}
	
	
	public void addGradient(boolean simpleGradient) {
		Logger.debug("Add gradient: simple? " + simpleGradient);
		AbstractPalette palette = (simpleGradient ? EvenGradientPalette.createDefault() : GradientPalette.createDefault());
		String name = ArrayUtil.generateUniqueString(Palettes.getNames(), ("New " + (simpleGradient ? "Simple Gradient" : "Gradient")));
		PaletteData newPalette = new PaletteData(name, palette);
		Palettes.addPalette(newPalette);
		
		// update JList
		gradientsList.updateListElements(Palettes.getAll());
		// select element
		gradientsList.getJListComponent().setSelectedValue(newPalette, true);
	}
	
	public void removeSelectedGradient() {
		if(Palettes.getAll().size() <= 1) {
			Main.getInstance().showNotification(NotificationType.IMPORTANT, "Palette List", "Palette cannot be removed. There must be at least 1 palette.");
			return;
		}
		
		PaletteData selectedPalette = gradientsList.getJListComponent().getSelectedValue();
		if(selectedPalette != null) {
			boolean removed = Palettes.removePalette(selectedPalette);
			if(removed) {
				setSelectedPalette(null);
				// update JList
				gradientsList.updateListElements(Palettes.getAll());
			}
		}
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
			
			JPanel panelWrapper = new JPanel();
			panelWrapper.setLayout(new GridLayout(1, 2));
			panelWrapper.setBackground(getBackground());
			add(panelWrapper, BorderLayout.SOUTH);
			
			JButton btnAdd = new JButton("Add");
			panelWrapper.add(btnAdd);
			
			btnRemove = new JButton("Remove");
			btnRemove.setEnabled(false);
			btnRemove.addActionListener(l -> removeSelectedGradient());
			panelWrapper.add(btnRemove);
			
			JPopupMenu popupMenu = new JPopupMenu();
			UiUtils.bindBackground(popupMenu, Style.panelAccentBackground());
			add(popupMenu);
			
			JMenuItem mItemSimpleGradient = new JMenuItem("Simple Gradient");
			mItemSimpleGradient.addActionListener(l -> addGradient(true));
			popupMenu.add(mItemSimpleGradient);
			
			JMenuItem mItemGradient = new JMenuItem("Gradient");
			mItemGradient.addActionListener(l -> addGradient(false));
			popupMenu.add(mItemGradient);
			
			btnAdd.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					// show popup menu above the button on hover
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
