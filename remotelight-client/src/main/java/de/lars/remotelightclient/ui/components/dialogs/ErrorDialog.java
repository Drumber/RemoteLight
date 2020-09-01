/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightclient.ui.components.dialogs;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightcore.utils.ExceptionHandler;

public class ErrorDialog {
	
	public static void showErrorDialog(Throwable e) {
		showErrorDialog(e, null);
	}
	
	public static void showErrorDialog(Throwable e, String title) {
		showErrorDialog(e, title, false);
	}
	
	public static void showErrorDialog(Throwable e, String title, boolean lineWrap) {
		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		
		JLabel header = new JLabel(String.format("A %s Error occured", e.getClass().getCanonicalName()));
		header.setHorizontalAlignment(JLabel.LEFT);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setFont(Style.getFontRegualar(12));
		root.add(header);
		
		root.add(Box.createVerticalStrut(20));
		
		String exceptionString = ExceptionHandler.getStackTrace(e);
		
		JTextArea text = new JTextArea(exceptionString);
		text.setLineWrap(lineWrap);
		text.setCaretPosition(0);
		text.setEditable(false);
		
		JScrollPane scroll = new JScrollPane(text);
		scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
		scroll.setSize(new Dimension(300, 150));
		scroll.setPreferredSize(new Dimension(300, 150));
		root.add(scroll);
		
		String[] options = {"Close", "Report"};
		
		//JOptionPane.showMessageDialog(null, root, (title != null ? title : "Exception"), JOptionPane.ERROR_MESSAGE);
		int selOption = JOptionPane.showOptionDialog(null, root, (title != null ? title : "Exception"),
				JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE,
				null, options, options[0]);
		
		if(selOption == 1) {
			try {
				
				String issueUrl = ExceptionHandler.getGitHubIssueURL(e);
				Desktop.getDesktop().browse(new URI(issueUrl));
				
			} catch (UnsupportedEncodingException e2) {
				System.err.println("Could not build query string: " + e2.getMessage());
			} catch (IOException | URISyntaxException e1) {
				System.err.println("Could not open issue url: " + e1.getMessage());
			}
		}
	}

}
