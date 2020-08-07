package de.lars.remotelightclient.ui.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.events.ConsoleOutEvent;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.frames.BasicFrame;
import de.lars.remotelightcore.event.Listener;

public class ConsoleFrame extends BasicFrame {
	private static final long serialVersionUID = 5793847343481919833L;
	
	private JPanel panelContent;
	private JTextPane textPane;

	//TODO add options: auto scroll, always on top, text size/font
	
	public ConsoleFrame() {
		super("console", Main.getInstance().getSettingsManager());
		setMinimumSize(new Dimension(300, 200));
		
		textPane = new JTextPane();
		AbstractDocument doc = (AbstractDocument) textPane.getDocument();
		doc.setDocumentFilter(new ConsoleDocumentFilter());
		
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(4, getHeight()));
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		panelContent = new JPanel();
		panelContent.setLayout(new BorderLayout());
		panelContent.add(scrollPane, BorderLayout.CENTER);
		setContentPane(panelContent);
		
		// set background color
		setBackgrounds();
		
		// register console event listener
		Main.getInstance().getCore().getEventHandler().register(ConsoleOutEvent.class, consoleOutListener);
		// show console history
		showConsoleHistory();
		
		// add property change listener to detect theme changes
		textPane.addPropertyChangeListener("UI", e -> {
			updateStyle();
		});
	}
	
	
	private Listener<ConsoleOutEvent> consoleOutListener = new Listener<ConsoleOutEvent>() {
		@Override
		public void onEvent(ConsoleOutEvent event) {
			writeConsoleEntry(event.getContent(), event.isErrorOut());
		}
	};
	
	
	public void showConsoleHistory() {
		Scanner scanner = new Scanner(CustomOutputStream.getStringBuilder().toString());
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			int redIndex = line.indexOf(CustomOutputStream.ANSI_RED);
			boolean isError = redIndex != -1;
			writeConsoleEntry(line + '\n', isError);
		}
	}
	
	
	protected void writeConsoleEntry(String msg, boolean error) {
		SwingUtilities.invokeLater(() -> {
			String s = msg;
			if(error) {
				// replace ANSI red marker
				s = s.replace(CustomOutputStream.ANSI_RED, "");
			}
			
			Color c = error ? Color.RED : Style.textColor;
			StyleContext sc = StyleContext.getDefaultStyleContext();
			AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
			aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Sans Serif");
			
			textPane.setCaretPosition(textPane.getDocument().getLength());
			textPane.setCharacterAttributes(aset, false);
			textPane.replaceSelection(s);
		});
	}
	
	protected void updateStyle() {
		// clear text pane
		textPane.setText("");
		// show console content
		showConsoleHistory();
		
		// update background color
		setBackgrounds();
		panelContent.updateUI();
	}
	
	protected void setBackgrounds() {
		setBackground(Style.panelBackground);
		panelContent.setBackground(getBackground());
		textPane.setBackground(getBackground());
	}

}
