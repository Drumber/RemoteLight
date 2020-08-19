package de.lars.remotelightclient.ui.panels.tools.entrypanels;

import java.awt.EventQueue;

import javax.swing.JFrame;

import de.lars.remotelightclient.ui.console.ConsoleFrame;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanelEntry;

public class ConsoleEntryPanel extends ToolsPanelEntry {
	
	private ConsoleFrame console;

	@Override
	public String getName() {
		return "Console";
	}
	
	@Override
	public void onClick() {
		EventQueue.invokeLater(() -> {
			if(console == null) {
				console = new ConsoleFrame();
			} else if(!console.isVisible()) {
				console.setVisible(true);
			}
			if(console.getState() == JFrame.ICONIFIED) {
				console.setState(JFrame.NORMAL);
			}
			if (console.isVisible() && console.isDisplayable()) {
				console.requestFocus();
			}
		});
	}

}
