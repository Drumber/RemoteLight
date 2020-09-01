package de.lars.remotelightclient.ui.components.dialogs;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.tinylog.Logger;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.utils.DirectoryUtil;

public class NoFileAccessDialog {
	
	public static void showDialog(String errorMsg) {
		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		
		JLabel header = new JLabel("RemoteLight has no file access.");
		header.setHorizontalAlignment(JLabel.LEFT);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setFont(Style.getFontRegualar(12));
		root.add(header);
		
		if(errorMsg != null) {
			JLabel lblErr = new JLabel(errorMsg);
			lblErr.setFont(Style.getFontRegualar(12));
			lblErr.setAlignmentX(Component.LEFT_ALIGNMENT);
			root.add(Box.createVerticalStrut(5));
			root.add(lblErr);
		}
		
		root.add(Box.createVerticalStrut(15));
		
		String contentText = String.format("RemoteLight cannot access the '%s' directory. "
				+ "This means that your settings and saved outputs cannot be saved or loaded.\n"
				+ "Please make sure that the directory can be accessed. This may be restricted by folder permissions or anti virus programs.",
				DirectoryUtil.getDataStoragePath());
		
		JTextArea text = new JTextArea(contentText);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setCaretPosition(0);
		text.setEditable(false);
		
		JScrollPane scroll = new JScrollPane(text);
		scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		scroll.setBorder(null);
		scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
		scroll.setSize(new Dimension(400, 150));
		scroll.setPreferredSize(new Dimension(400, 150));
		root.add(scroll);
		
		String[] options = {"Close", "Open folder", "Create GitHub issue"};
		
		int selOption = JOptionPane.showOptionDialog(null, root, "No File Access",
				JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE,
				null, options, options[0]);
		
		if(selOption == 1) {
			File dir = new File(DirectoryUtil.getDataStoragePath());
			if(!dir.exists()) {
				dir = new File(System.getProperty("user.home"));
			}
			try {
				Desktop.getDesktop().open(dir);
			} catch (IOException e1) {
				Logger.error(e1, "Could not open plugins directory.");
				JOptionPane.showMessageDialog(null, "Could not open directory '" + dir.getAbsolutePath() + "'!", "File Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if(selOption == 2) {
			try {
				String issuePage = RemoteLightCore.GITHUB + "/issues";
				Desktop.getDesktop().browse(new URI(issuePage));
			} catch (IOException | URISyntaxException e1) {
				System.err.println("Could not open issue page: " + e1.getMessage());
			}
		}
	}

}
