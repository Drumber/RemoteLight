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

package de.lars.remotelightclient.ui.panels.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;

import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingBooleanPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingColorPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingDoublePanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingIntPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingSelectionPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingStringPanel;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingDouble;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingString;

public class SettingsPanel extends MenuPanel {
	private static final long serialVersionUID = 3954953325346082615L;
	
	private SettingsManager sm;

	/**
	 * Create the panel.
	 */
	public SettingsPanel(MainFrame mainFrame , SettingsManager sm) {
		this.sm = sm;
		mainFrame.showControlBar(false);
		setLayout(new BorderLayout(0, 0));
		
		JPanel main = new JPanel();
		main.setAlignmentX(Component.LEFT_ALIGNMENT);
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		addSettingEntries(main);
		
		TScrollPane scrollPane = new TScrollPane(main);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);
		
		this.updateUI();
	}
	
	private void openSubPanel(JPanel panel) {
		this.removeAll();
		this.add(panel, BorderLayout.CENTER);
		this.revalidate();
		this.repaint();
	}
	
	private void addSettingEntries(JPanel parent) {
		appendSettingSection(parent, createRedirectPanel("UI Appearance", "Theme, Language and Tray Icon", e -> openSubPanel(new ThemeSettingsPanel())));
		appendSettingSection(parent, createSettingsPanel(new String[] {"out.delay", "out.autoconnect", "manager.lastactive.enabled", "out.effects.disableleds"}, "General"));
		appendSettingSection(parent, createSettingsPanel(new String[] {"data.autosave","data.autosave.interval"}, "Auto Save"));
		appendSettingSection(parent, createSettingsPanel(new String[] {"main.checkupdates", "main.checkupdates.prerelease"}, "Update Checker"));
		appendSettingSection(parent, createSettingsPanel(new String[] {"plugins.enable"}, "Plugin Interface"));
		appendSettingSection(parent, createSettingsPanel(new String[] {"lua.advanced", "lua.instructions"},  "Lua Script Interface"));
		appendSettingSection(parent, createSettingsPanel(new String[] {"restapi.enable", "restapi.port"}, "REST API"));
		appendSettingSection(parent, createSettingsPanel(new String[] {"logs.deletedays"}, "Logs"));
	}
	
	private void appendSettingSection(JPanel parent, JPanel section) {
		parent.add(section);
		parent.add(Box.createVerticalStrut(10));
	}
	
	private JPanel createEntryPanel() {
		JPanel root = new JPanel();
		UiUtils.bindElevation(root, 10);
		root.setAlignmentX(Component.LEFT_ALIGNMENT);
		root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		root.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				Component title = UiUtils.findChildWithName(root, "title");
				if(title != null) {
					UiUtils.bindForeground((JComponent) title, Style.accent());
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {
				Component title = UiUtils.findChildWithName(root, "title");
				Component child = root.getComponentAt(e.getX(), e.getY()); // check if mouse is over a child component
				if(title != null && child == null) {
					title.setForeground(Style.textColor().get());
				}
			}
		});
		
		return root;
	}
	
	private JPanel createRedirectPanel(String title, String description, ActionListener listener) {
		JPanel root = createEntryPanel();
		root.setLayout(new BorderLayout());
		root.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				UiUtils.setElevation(root, 20);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				UiUtils.setElevation(root, 10);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if(listener != null) {
					listener.actionPerformed(new ActionEvent(e.getSource(), e.getID(), null));
				}
			}
		});
		
		JPanel content = new JPanel();
		content.setBackground(null);
		content.setLayout(new GridLayout(description != null ? 2 : 1, 1));
		root.add(content, BorderLayout.CENTER);
		
		JLabel lblTitle = new JLabel(title);
		UiUtils.bindFont(lblTitle, Style.getFontBold(14));
		lblTitle.setName("title");
		content.add(lblTitle);
		
		if(description != null) {
			JLabel lblDesc = new JLabel(description);
			content.add(lblDesc);
		}
		
		JLabel lblIcon = new JLabel(Style.getFontIcon(MenuIcon.BACK, 14, Style.textColor().get(), 180));
		root.add(lblIcon, BorderLayout.EAST);
		
		root.setMaximumSize(new Dimension(Integer.MAX_VALUE, root.getPreferredSize().height));
		return root;
	}
	
	private JPanel createSettingsPanel(Setting[] settings, String title) {
		JPanel root = createEntryPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		
		if(title != null) {
			JLabel lblTitle = new JLabel(title);
			lblTitle.setName("title");
			UiUtils.bindFont(lblTitle, Style.getFontBold(14));
			lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
			root.add(lblTitle);
			root.add(Box.createVerticalStrut(10));
		}
		
		for(Setting setting : settings) {
			SettingPanel spanel = this.getSettingPanel(setting);
			spanel.setBackground(null);
			spanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			spanel.setSettingChangedListener(sp -> sp.setValue());
			root.add(spanel);
			root.add(Box.createVerticalStrut(5));
		}
		
		return root;
	}
	
	private JPanel createSettingsPanel(String[] settingIDs, String title) {
		List<Setting> settings = new ArrayList<>();
		for(String id : settingIDs) {
			Setting s = sm.getSettingFromId(id);
			if(s != null) {
				settings.add(s);
			}
		}
		return createSettingsPanel(settings.toArray(new Setting[0]), title);
	}
	
	private SettingPanel getSettingPanel(Setting s) {
		if(s instanceof SettingString) {
			return new SettingStringPanel((SettingString) s);
		}
		if(s instanceof SettingBoolean) {
			return new SettingBooleanPanel((SettingBoolean) s);
		}
		if(s instanceof SettingColor) {
			return new SettingColorPanel((SettingColor) s);
		}
		if(s instanceof SettingDouble) {
			return new SettingDoublePanel((SettingDouble) s);
		}
		if(s instanceof SettingInt) {
			return new SettingIntPanel((SettingInt) s);
		}
		if(s instanceof SettingSelection) {
			return new SettingSelectionPanel((SettingSelection) s);
		}
		return null;
	}

	@Override
	public String getName() {
		return i18n.getString("Basic.Settings");
	}

}
