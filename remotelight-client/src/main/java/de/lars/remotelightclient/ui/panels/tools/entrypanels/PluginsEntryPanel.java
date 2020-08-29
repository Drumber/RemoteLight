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

package de.lars.remotelightclient.ui.panels.tools.entrypanels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.ListElement;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanel;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanelEntry;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightplugins.Plugin;
import de.lars.remotelightplugins.PluginInfo;
import de.lars.remotelightplugins.PluginManager;
import de.lars.remotelightplugins.properties.DefaultProperties;

public class PluginsEntryPanel extends ToolsPanelEntry {

	private PluginManager pluginManager;
	
	public PluginsEntryPanel() {
		pluginManager = Main.getInstance().getPluginManager();
	}
	
	@Override
	public String getName() {
		return "Plugins";
	}
	
	@Override
	public JPanel getMenuPanel(ToolsPanel context) {
		return new PluginsPanel();
	}
	
	
	private class PluginsPanel extends JPanel {
		private static final long serialVersionUID = 8042375442922589571L;
		private JPanel root;
		private JPanel panelLoaded;
		private JPanel panelFailed;
		
		public PluginsPanel() {
			setLayout(new BorderLayout());
			setBackground(Style.panelBackground);
			setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			
			root = new JPanel();
			root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
			root.setBackground(getBackground());
			
			// show info message if plugin system is disabled
			if(!Main.getInstance().getSettingsManager().getSetting(SettingBoolean.class, "plugins.enable").getValue()
					&& pluginManager.getLoadedPlugins().size() == 0) {
				JLabel lblDisabledPl = new JLabel("Plugins are disabled. Go to settings and enable plugins if you want to use this feature.");
				lblDisabledPl.setForeground(Style.warn);
				lblDisabledPl.setAlignmentX(Component.LEFT_ALIGNMENT);
				root.add(lblDisabledPl);
				root.add(Box.createVerticalStrut(10));
			}
			
			JLabel lblLoaded = new JLabel("Loaded Plugins (" + pluginManager.getLoadedPlugins().size() + ")");
			lblLoaded.setForeground(Style.textColor);
			lblLoaded.setAlignmentX(Component.LEFT_ALIGNMENT);
			root.add(lblLoaded);
			root.add(Box.createVerticalStrut(5));
			
			panelLoaded = new JPanel();
			panelLoaded.setLayout(new BoxLayout(panelLoaded, BoxLayout.Y_AXIS));
			panelLoaded.setBackground(Style.panelBackground);
			panelLoaded.setAlignmentX(Component.LEFT_ALIGNMENT);
			root.add(panelLoaded);
			
			root.add(Box.createVerticalStrut(20));
			
			JLabel lblFailed = new JLabel("Plugins with Errors (" + pluginManager.getErrorPlugins().size() + ")");
			lblFailed.setForeground(Style.textColor);
			lblFailed.setAlignmentX(Component.LEFT_ALIGNMENT);
			root.add(lblFailed);
			root.add(Box.createVerticalStrut(5));
			
			panelFailed = new JPanel();
			panelFailed.setLayout(new BoxLayout(panelFailed, BoxLayout.Y_AXIS));
			panelFailed.setBackground(Style.panelBackground);
			panelFailed.setAlignmentX(Component.LEFT_ALIGNMENT);
			root.add(panelFailed);
			
			root.add(Box.createVerticalGlue());
			root.add(Box.createVerticalStrut(10));
			
			JButton btnPluginsFolder = new JButton("Open plugins folder");
			UiUtils.configureButton(btnPluginsFolder);
			btnPluginsFolder.addActionListener(e -> {
				try {
					Desktop.getDesktop().open(pluginManager.getPluginDirectory());
				} catch (IOException e1) {
					Logger.error(e1, "Could not open plugins directory.");
				}
			});
			root.add(btnPluginsFolder);
			
			initPluginPanels();
			add(root, BorderLayout.CENTER);
		}
		
		void initPluginPanels() {
			List<Plugin> loaded = pluginManager.getLoadedPlugins();
			Map<PluginInfo, String> error = pluginManager.getErrorPlugins();
			panelLoaded.removeAll();
			panelFailed.removeAll();
			
			for(Plugin pl : loaded) {
				PluginElement el = new PluginElement(pl);
				panelLoaded.add(el);
				panelLoaded.add(Box.createVerticalStrut(5));
			}
			
			for(Map.Entry<PluginInfo, String> errPl : error.entrySet()) {
				PluginElement el = new PluginElement(errPl.getKey(), errPl.getValue());
				panelFailed.add(el);
				panelLoaded.add(Box.createVerticalStrut(5));
			}
		}
		
		
		/**
		 * Plugin list element panel
		 */
		private class PluginElement extends ListElement {
			private static final long serialVersionUID = 4929823177112833763L;
			private final Plugin plugin;
			private final PluginInfo info;
			private final String errorMsg;
			
			public PluginElement(Plugin plugin, String errorMsg) {
				super();
				this.plugin = plugin;
				this.info = plugin.getPluginInfo();
				this.errorMsg = errorMsg;
				init();
			}
			
			public PluginElement(Plugin plugin) {
				this(plugin, null);
			}
			
			public PluginElement(PluginInfo info, String errorMsg) {
				super();
				this.plugin = null;
				this.info = info;
				this.errorMsg = errorMsg;
				init();
			}
			
			void init() {
				String plName = info.getValue(DefaultProperties.DISPLAYNAME);
				if(plName == null && info.getName() != null)
					plName = info.getName();
				else if(plName == null)
					plName = info.getFile().getName();
				JLabel lblName = new JLabel(plName);
				lblName.setForeground(Style.textColor);
				lblName.setAlignmentX(Component.LEFT_ALIGNMENT);
				add(lblName);
				add(Box.createHorizontalStrut(5));
				
				if(errorMsg == null && plugin != null) {
					String textVersionAuthor = info.getValue(DefaultProperties.VERSION)
							+ " by " + info.getValue(DefaultProperties.AUTHOR)
							+ (!plugin.isEnabled() ? " (disabled)" : "");
					
					JLabel lblVersionAuthor = new JLabel(textVersionAuthor);
					lblVersionAuthor.setForeground(Style.textColorDarker);
					add(lblVersionAuthor);
				} else {
					add(Box.createHorizontalStrut(5));
					add(new JLabel(Style.getFontIcon(MenuIcon.ERROR, 14, Style.error)));
					add(Box.createHorizontalStrut(2));
					
					JLabel lblError = new JLabel(errorMsg);
					lblError.setForeground(Style.error);
					add(lblError);
				}
			}
			
		} // ListElement end
		
	} // PluginsPanel end

}
