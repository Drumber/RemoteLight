package de.lars.remotelightclient.ui.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
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
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.event.Listener;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingString;

public class ConsoleFrame extends BasicFrame {
	private static final long serialVersionUID = 5793847343481919833L;
	
	private JPanel panelContent, panelActions, panelSettings;
	private JTextPane textPane;
	private JTextField fieldCmd;
	private JButton btnSend, btnSettings;
	private JCheckBox checkAutoScroll;

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
		
		{ // panelContent[SOUTH] > panelActions
			panelActions = new JPanel();
			panelActions.setLayout(new GridBagLayout());
			
			// command filed
			fieldCmd = new JTextField();
			// send button
			btnSend = new JButton("Send");
			
			// settings button
			btnSettings = new JButton();
			btnSettings.setIcon(Style.getFontIcon(MenuIcon.SETTINGS, 12));
			btnSettings.setToolTipText("Console Settings");
			btnSettings.addActionListener(e -> {
				// toggle setting panel
				panelSettings.setVisible(!panelSettings.isVisible());
			});
			
			// configure gridbag layout
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 0.5;
			panelActions.add(fieldCmd, c);
			
			c.fill = GridBagConstraints.NONE;
			c.gridx = 1;
			c.gridy = 0;
			c.weightx = 0.0;
			panelActions.add(btnSend, c);
			
			c.fill = GridBagConstraints.NONE;
			c.gridx = 2;
			c.gridy = 0;
			c.weightx = 0.0;
			panelActions.add(btnSettings, c);
			
			panelContent.add(panelActions, BorderLayout.SOUTH);
		}
		
		{ // panelContent[EAST] > panelSettings
			panelSettings = new JPanel();
			panelSettings.setLayout(new GridBagLayout());
			panelSettings.setVisible(false);
			
			// always on top checkbox
			JCheckBox checkAlwayTop = new JCheckBox();
			checkAlwayTop.setSelected(isAlwayTop());
			checkAlwayTop.addActionListener(e -> setAlwaysTop(checkAlwayTop.isSelected()));
			
			// auto scroll checkbox
			SettingBoolean settingAutoScroll = sm.addSetting(new SettingBoolean("console.autoscroll", "Auto scroll", SettingCategory.Intern, null, true));
			checkAutoScroll = new JCheckBox();
			checkAutoScroll.setSelected(settingAutoScroll.getValue());
			checkAutoScroll.addActionListener(e -> settingAutoScroll.setValue(checkAutoScroll.isSelected()));
			
			// font combobox
			SettingString settingFont = sm.addSetting(new SettingString("console.font", "Console font", SettingCategory.Intern, null, "SansSerif"));
			String[] fontNames = UiUtils.getAvailableFonts();
			JComboBox<String> comboFonts = new JComboBox<String>(fontNames);
			// select saved font
			int fontIndex = Arrays.asList(fontNames).indexOf(settingFont.getValue());
			if(fontIndex == -1) fontIndex = 0;
			comboFonts.setSelectedIndex(fontIndex);
			comboFonts.setToolTipText("Console font");
			comboFonts.setPreferredSize(new Dimension(100, comboFonts.getPreferredSize().height));
			comboFonts.addActionListener(e -> {
				settingFont.setValue(fontNames[comboFonts.getSelectedIndex()]);
				// TODO update font
			});
			
			// font size spinner
			SettingInt settingFontSize = sm.addSetting(new SettingInt("console.fontsize", "Console font size", SettingCategory.Intern, null, 11, 6, 30, 1));
			JSpinner spinnerSize = new JSpinner(new SpinnerNumberModel(settingFontSize.getValue(), settingFontSize.getMin(), settingFontSize.getMax(), settingFontSize.getStepsize()));
			spinnerSize.setToolTipText("Font size");
			spinnerSize.addChangeListener(e -> {
				settingFontSize.setValue((int) spinnerSize.getValue());
				// TODO update font size
			});
			
			// configure gridbag layout
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(10, 5, 0, 5);
			
			// always on top checbox
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.LINE_START;
			panelSettings.add(new JLabel("Always on top"), c);
			c.anchor = GridBagConstraints.LINE_END;
			c.gridx = 1;
			panelSettings.add(checkAlwayTop, c);
			
			// auto scroll checkbox
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.LINE_START;
			panelSettings.add(new JLabel("Auto scroll"), c);
			c.anchor = GridBagConstraints.LINE_END;
			c.gridx = 1;
			panelSettings.add(checkAutoScroll, c);
			
			// font combobox
			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.LINE_START;
			panelSettings.add(new JLabel("Font"), c);
			c.anchor = GridBagConstraints.LINE_END;
			c.gridx = 1;
			panelSettings.add(comboFonts, c);
			
			// font size spinner
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.LINE_START;
			panelSettings.add(new JLabel("Font size"), c);
			c.anchor = GridBagConstraints.LINE_END;
			c.gridx = 1;
			panelSettings.add(spinnerSize, c);
			
			// spacer
			c.gridx = 0;
			c.gridy = 4;
			c.weighty = 1.0;
			JPanel spacer = new JPanel();
			spacer.setBackground(null);
			panelSettings.add(spacer, c);
			
			panelContent.add(panelSettings, BorderLayout.EAST);
		}
		
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
	
	
	/** triggered on new console message */
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
			aset = sc.addAttribute(aset, StyleConstants.FontFamily, "SansSerif");
			
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
		panelActions.setBackground(getBackground());
		UiUtils.configureButton(btnSend);
		UiUtils.configureButton(btnSettings);
	}

}
