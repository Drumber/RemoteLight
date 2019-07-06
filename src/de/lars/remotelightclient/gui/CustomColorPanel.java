package de.lars.remotelightclient.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CustomColorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4871730974494095833L;
	

	/**
	 * Create the panel.
	 */
	public CustomColorPanel(Color color) {
		addMouseListener(this.mouseListener);
		setBackground(color);
		setMaximumSize(new Dimension(50, 50));
		
		setLayout(new BorderLayout());

	}
	
	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			
		}
	};
	

}
