package de.lars.remotelightclient.ui.panels.tools.entrypanels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
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
import de.lars.remotelightclient.ui.panels.tools.ToolsPanelEntry;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;
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
	public JPanel getMenuPanel() {
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
				ListElement el = new ListElement(pl.getPluginInfo());
				panelLoaded.add(el);
				panelLoaded.add(Box.createVerticalStrut(5));
			}
			
			for(Map.Entry<PluginInfo, String> errPl : error.entrySet()) {
				ListElement el = new ListElement(errPl.getKey(), errPl.getValue());
				panelFailed.add(el);
				panelLoaded.add(Box.createVerticalStrut(5));
			}
		}
		
		
		/**
		 * Plugin list element panel
		 */
		private class ListElement extends JPanel {
			private static final long serialVersionUID = 4929823177112833763L;
			private PluginInfo info;
			private String errorMsg;
			
			public ListElement(PluginInfo pluginInfo, String errorMsg) {
				this.info = pluginInfo;
				this.errorMsg = errorMsg;
				init();
			}
			
			public ListElement(PluginInfo pluginInfo) {
				this(pluginInfo, null);
			}
			
			void init() {
				setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
				Dimension size = new Dimension(Integer.MAX_VALUE, 50);
				setMaximumSize(size);
				setMinimumSize(new Dimension(30, size.height));
				setPreferredSize(getMinimumSize());
				setBackground(Style.buttonBackground);
				setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
				UiUtils.addHoverColor(this, Style.buttonBackground, Style.hoverBackground);

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
				
				if(errorMsg == null) {
					String textVersionAuthor = info.getValue(DefaultProperties.VERSION)
							+ " by " + info.getValue(DefaultProperties.AUTHOR);
					
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
			} // init end
			
		} // ListElement end
		
	} // PluginsPanel end

}
