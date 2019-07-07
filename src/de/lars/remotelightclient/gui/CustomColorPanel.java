package de.lars.remotelightclient.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class CustomColorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4871730974494095833L;
	public final static Color[] DEFAULT_COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.ORANGE, Color.WHITE};
	private static ArrayList<CustomColorPanel> allPanels = new ArrayList<>();
	private static CustomColorPanel selectedPanel = null;
	

	/**
	 * Create the panel.
	 */
	public CustomColorPanel(Color color) {
		addMouseListener(this.mouseListener);
		setBackground(color);
		setPreferredSize(new Dimension(40, 40));
		setMaximumSize(new Dimension(40, 40));
		
		setLayout(new BorderLayout());
		allPanels.add(this);
	}
	
	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			for(CustomColorPanel panel : allPanels) {
				panel.setBorder(null);
			}
			setBorder(new LineBorder(new Color(0, 0, 0), 2, false));
			selectedPanel = CustomColorPanel.this;
		}
	};
	
	public static CustomColorPanel getSelectedPanel() {
		return selectedPanel;
	}
	
	public static void removePanel(CustomColorPanel panel) {
		if(selectedPanel == panel) {
			selectedPanel = null;
		}
		allPanels.remove(panel);
		panel = null;
	}
	
	public static Color[] getAllBackgroundColors() {
		Color[] colors = new Color[allPanels.size()];
		int count = 0;
		for(CustomColorPanel panel : allPanels) {
			colors[count] = panel.getBackground();
			count++;
		}
		return colors;
	}

}
