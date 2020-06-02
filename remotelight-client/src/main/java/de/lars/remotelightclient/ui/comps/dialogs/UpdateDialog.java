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

package de.lars.remotelightclient.ui.comps.dialogs;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.utils.UpdateChecker;

public class UpdateDialog {
	
	public static int showUpdateDialog(UpdateChecker checker) {
		
		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		
		JLabel headline = new JLabel("Update available!");
		headline.setFont(Style.getFontRegualar(14));
		
		JLabel text = new JLabel();
		text.setFont(Style.getFontRegualar(12));
		text.setText("<html>There is a new version of RemoteLight available.<br>" + 
				"Current: " + Main.VERSION + "<br>" +
				"New: " + checker.getNewTag() + "</html>");
		
		root.add(headline);
		root.add(text);
		
		int option = JOptionPane.showOptionDialog(null, root, "Update Notification",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
				new String[] {"Download", "Close"}, "Download");
		
		if(option == 0) { // when user clicks Download, open Browser
			openDownloadSite(checker.getNewUrl());
		}
		return option;
	}
	
	public static void openDownloadSite(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (URISyntaxException | IOException ex) {
			Logger.warn(ex, "Could not open download page: " + url);
			Main.getInstance().showNotification(NotificationType.ERROR, "Could not open " + url);
		}
	}

}
