package de.lars.remotelightclient.ui.comps.dialogs;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.*;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightcore.utils.ExceptionHandler;

public class ErrorDialog {
	
	public static void showErrorDialog(Throwable e) {
		showErrorDialog(e, null);
	}
	
	public static void showErrorDialog(Throwable e, String title) {
		showErrorDialog(e, title, true);
	}
	
	public static void showErrorDialog(Throwable e, String title, boolean lineWrap) {
		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		
		JLabel header = new JLabel(String.format("A %s Error occured", e.getClass().getCanonicalName()));
		header.setHorizontalAlignment(JLabel.LEFT);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setFont(Style.getFontRegualar(12));
		root.add(header);
		
		root.add(Box.createRigidArea(new Dimension(0, 20)));
		
		JTextArea text = new JTextArea(ExceptionHandler.getStackTrace(e));
		text.setLineWrap(lineWrap);
		text.setCaretPosition(0);
		text.setEditable(false);
		
		JScrollPane scroll = new JScrollPane(text);
		scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
		scroll.setSize(new Dimension(200, 150));
		scroll.setPreferredSize(new Dimension(200, 150));
		root.add(scroll);
		
		JOptionPane.showMessageDialog(null, root, (title != null ? title : "Exception"), JOptionPane.ERROR_MESSAGE);
	}

}
