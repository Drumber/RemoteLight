package de.lars.remotelightclient.ui.panels.colors.gradients;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightcore.utils.color.palette.PaletteData;

public class GradientsList extends JPanel {
	private static final long serialVersionUID = -5638567248623585698L;
	
	private JList<PaletteData> list;
	
	public GradientsList(List<PaletteData> listPalettes) {
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout());
		
		DefaultListModel<PaletteData> listModel = new DefaultListModel<PaletteData>();
		listPalettes.forEach(p -> listModel.addElement(p));
		
		list = new JList<PaletteData>(listModel);
		list.setCellRenderer(new GradientListElement());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectionBackground(Style.accent);
		list.setSelectionForeground(Style.textColor);
		list.setForeground(Style.textColor);
		list.setBackground(Style.panelDarkBackground);
		
		TScrollPane scrollPane = new TScrollPane(list);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public JList<PaletteData> getJListComponent() {
		return list;
	}
	
	
	/**
	 * Gradient list element holding the name and preview of the gradient.
	 */
	private class GradientListElement extends JPanel implements ListCellRenderer<PaletteData> {
		private static final long serialVersionUID = -5393936404236766628L;
		
		private JLabel lblTitle;
		private GradientBar gradientBar;
		
		public GradientListElement() {
			setLayout(new GridBagLayout());
			setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.PAGE_START;
			gbc.weightx = 0.5;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.ipady = 5;
			
			lblTitle = new JLabel("", SwingConstants.LEFT);
			add(lblTitle, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.ipady = 5;
			
			gradientBar = new GradientBar(null);
			gradientBar.setCornerRadius(5);
			add(gradientBar, gbc);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends PaletteData> list, PaletteData value, int index, boolean isSelected,
				boolean cellHasFocus) {
			
			if(isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else if(cellHasFocus) {
				setBackground(Style.hoverBackground);
				setForeground(Style.textColor);
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			lblTitle.setText(value.getName());
			lblTitle.setForeground(getForeground());
			
			gradientBar.setColorPalette(value.getPalette());
			gradientBar.repaint();
			
			return this;
		}
		
	}

}
