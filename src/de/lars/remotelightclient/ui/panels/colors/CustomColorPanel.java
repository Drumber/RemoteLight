package de.lars.remotelightclient.ui.panels.colors;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.PixelColorUtils;

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
	private static ArrayList<CustomColorPanel> allPanels = new ArrayList<>();
	private static CustomColorPanel selectedPanel = null;
	private static Dimension size = new Dimension(80, 80);
	private static int selSizeFactor = 10;
	

	/**
	 * Create the panel.
	 */
	public CustomColorPanel(Color color) {
		addMouseListener(this.mouseListener);
		setBackground(color);
		setPreferredSize(size);
		setMaximumSize(size);
		
		setLayout(new BorderLayout());
		allPanels.add(this);
	}
	
	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			for(CustomColorPanel panel : allPanels) {
				panel.setPreferredSize(size);
				panel.setMaximumSize(size);
			}
			Dimension selSize = new Dimension(size.width + selSizeFactor, size.height + selSizeFactor);
			setPreferredSize(selSize);
			setMaximumSize(selSize);
			selectedPanel = CustomColorPanel.this;
			updateUI();
			
			Color c = getBackground();
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(c, Main.getLedNum()));
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
	
	public static void reset() {
		allPanels.clear();
		selectedPanel = null;
	}
	
	public static void setPanelSize(Dimension size) {
		CustomColorPanel.size = size;
	}
	
	public static Dimension getPanelSize() {
		return size;
	}

}
