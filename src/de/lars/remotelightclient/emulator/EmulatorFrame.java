package de.lars.remotelightclient.emulator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.WrapLayout;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;

public class EmulatorFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2752076462014410311L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public EmulatorFrame() {
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panelToolbar = new JPanel();
		WrapLayout wlayoutTools = new WrapLayout(FlowLayout.LEFT);
		panelToolbar.setLayout(wlayoutTools);
		panelToolbar.setBackground(Style.panelDarkBackground);
		contentPane.add(panelToolbar, BorderLayout.NORTH);
		
		JButton btnEnable = new JButton("Enable");
		panelToolbar.add(btnEnable);
		
		JPanel bgrPixel = new JPanel();
		contentPane.add(bgrPixel, BorderLayout.CENTER);
		bgrPixel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		bgrPixel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel panelPixel = new JPanel();
		WrapLayout wlayout = new WrapLayout(FlowLayout.LEFT);
		panelPixel.setLayout(wlayout);
		panelPixel.setBackground(Style.panelBackground);
		scrollPane.setViewportView(panelPixel);
	}

}
