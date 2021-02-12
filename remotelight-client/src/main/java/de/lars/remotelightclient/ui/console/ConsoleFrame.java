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

package de.lars.remotelightclient.ui.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.events.ConsoleOutEvent;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.CustomTextPane;
import de.lars.remotelightclient.ui.components.frames.BasicFrame;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.cmd.exceptions.CommandException;
import de.lars.remotelightcore.event.Listener;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingString;

public class ConsoleFrame extends BasicFrame {
	private static final long serialVersionUID = 5793847343481919833L;
	
	private JPanel panelContent, panelActions, panelSettings;
	private CustomTextPane textPane;
	private JTextField fieldCmd;
	private JButton btnSend, btnSettings;
	private JCheckBox checkAutoScroll, checkShowInput;
	
	protected String font = "SansSerif";
	protected int fontSize = 11;
	
	public ConsoleFrame() {
		super("console", Main.getInstance().getSettingsManager());
		setMinimumSize(new Dimension(300, 200));
		setFullClose(false);
		setTitle("Console");
		
		textPane = new CustomTextPane();
		textPane.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, getHeight()));
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
			fieldCmd.addActionListener(consoleFieldListener);
			
			// send button
			btnSend = new JButton("Send");
			btnSend.addActionListener(consoleFieldListener);
			
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
			checkAutoScroll.setSelected(settingAutoScroll.get());
			checkAutoScroll.addActionListener(e -> settingAutoScroll.setValue(checkAutoScroll.isSelected()));
			
			// show console input checkbox
			SettingBoolean settingShowInput = sm.addSetting(new SettingBoolean("console.showinput", "Show console input", SettingCategory.Intern, null, true));
			checkShowInput = new JCheckBox();
			checkShowInput.setSelected(settingShowInput.get());
			checkShowInput.addActionListener(e -> settingShowInput.setValue(checkShowInput.isSelected()));
			
			// font combobox
			SettingString settingFont = sm.addSetting(new SettingString("console.font", "Console font", SettingCategory.Intern, null, "SansSerif"));
			String[] fontNames = UiUtils.getAvailableFonts();
			JComboBox<String> comboFonts = new JComboBox<String>(fontNames);
			// select saved font
			int fontIndex = Arrays.asList(fontNames).indexOf(settingFont.get());
			if(fontIndex == -1) fontIndex = 0;
			comboFonts.setSelectedIndex(fontIndex);
			comboFonts.setToolTipText("Console font");
			comboFonts.setPreferredSize(new Dimension(120, comboFonts.getPreferredSize().height));
			comboFonts.addActionListener(e -> {
				if(comboFonts.getSelectedIndex() < fontNames.length) {
					font = fontNames[comboFonts.getSelectedIndex()];
					settingFont.setValue(font);
					updateOnlyFont();
				}
			});
			
			// font size spinner
			SettingInt settingFontSize = sm.addSetting(new SettingInt("console.fontsize", "Console font size", SettingCategory.Intern, null, 11, 6, 30, 1));
			JSpinner spinnerSize = new JSpinner(new SpinnerNumberModel(settingFontSize.get().intValue(), settingFontSize.getMin(), settingFontSize.getMax(), settingFontSize.getStepsize()));
			spinnerSize.setToolTipText("Font size");
			spinnerSize.addChangeListener(e -> {
				fontSize = (int) spinnerSize.getValue();
				settingFontSize.setValue(fontSize);
				updateOnlyFont();
			});
			
			// set font from saved settings
			if(comboFonts.getSelectedIndex() < fontNames.length)
				font = fontNames[comboFonts.getSelectedIndex()];
			fontSize = (int) spinnerSize.getValue();
			
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
			
			// show console input checkbox
			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.LINE_START;
			panelSettings.add(new JLabel("Show input"), c);
			c.anchor = GridBagConstraints.LINE_END;
			c.gridx = 1;
			panelSettings.add(checkShowInput, c);
			
			// font combobox
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.LINE_START;
			panelSettings.add(new JLabel("Font"), c);
			c.anchor = GridBagConstraints.LINE_END;
			c.gridx = 1;
			panelSettings.add(comboFonts, c);
			
			// font size spinner
			c.gridx = 0;
			c.gridy = 4;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.LINE_START;
			panelSettings.add(new JLabel("Font size"), c);
			c.anchor = GridBagConstraints.LINE_END;
			c.gridx = 1;
			panelSettings.add(spinnerSize, c);
			
			// spacer
			c.gridx = 0;
			c.gridy = 5;
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
	
	
	/**
	 * Show all console messages store id {@link CustomOutputStream#getStringBuilder()}.
	 */
	public void showConsoleHistory() {
		Scanner scanner = new Scanner(CustomOutputStream.getStringBuilder().toString());
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			int redIndex = line.indexOf(CustomOutputStream.ANSI_RED);
			boolean isError = redIndex != -1;
			writeConsoleEntry(line + '\n', isError);
		}
	}
	
	/**
	 * Append a new console message.
	 * 
	 * @param msg	the message to write
	 * @param error	whether this is an error message or not
	 */
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
			aset = sc.addAttribute(aset, StyleConstants.FontFamily, font);
			aset = sc.addAttribute(aset, StyleConstants.FontSize, fontSize);
			
			int prevCaretPos = textPane.getCaretPosition();
			textPane.setCaretPosition(textPane.getDocument().getLength());
			textPane.setCharacterAttributes(aset, false);
			textPane.replaceSelectionUnchecked(s);
			if(!checkAutoScroll.isSelected()) {
				textPane.setCaretPosition(prevCaretPos);
			}
		});
	}
	
	
	/** Triggered when the send button or Enter button is pressed */
	ActionListener consoleFieldListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String msg = fieldCmd.getText();
			fieldCmd.setText("");
			if(checkShowInput.isSelected())
				writeConsoleEntry(" > " + msg + '\n', false);
			executeConsoleCommand(msg);
		}
	};
	
	/**
	 * Adds a console command to the system input stream.
	 * 
	 * @param cmd	the command to execute
	 */
	protected void executeConsoleCommand(String cmd) {
		// store old system input stream
		InputStream consoleIn = System.in;
		// set custom input stream
		ByteArrayInputStream bais = new ByteArrayInputStream(cmd.getBytes());
		System.setIn(bais);
		
		// also execute command to intern cmd system
		try {
			Main.getInstance().getCore().getCommandParser().parse(cmd.split(" "));
		} catch (CommandException e) {
			System.err.println(e.getMessage());
		}
		
		// restore old console input stream
		System.setIn(consoleIn);
		bais = null;
	}
	
	
	/**
	 * Removes the complete console text, add it back with
	 * new style attributes and update all backgrounds.
	 */
	protected void updateStyle() {
		// clear text pane
		textPane.setText("");
		// show console content
		showConsoleHistory();
		
		// update background color
		setBackgrounds();
		panelContent.updateUI();
	}
	
	/**
	 * Update only font type and size.
	 */
	protected void updateOnlyFont() {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.FontFamily, font);
		aset = sc.addAttribute(aset, StyleConstants.FontSize, fontSize);
		
		textPane.selectAll();
		textPane.setCharacterAttributes(aset, false);
	}
	
	/**
	 * Update all background colors.
	 */
	protected void setBackgrounds() {
		setBackground(Style.panelBackground);
		panelContent.setBackground(getBackground());
		textPane.setBackground(getBackground());
		panelActions.setBackground(getBackground());
		fieldCmd.setBackground(getBackground());
		fieldCmd.setForeground(Style.textColor);
		UiUtils.configureButton(btnSend);
		UiUtils.configureButton(btnSettings);
	}

}
