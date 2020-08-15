package de.lars.remotelightclient.ui.menu;

import java.util.List;

import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightclient.ui.panels.about.AboutPanel;
import de.lars.remotelightclient.ui.panels.animations.AnimationsPanel;
import de.lars.remotelightclient.ui.panels.colors.ColorsPanel;
import de.lars.remotelightclient.ui.panels.musicsync.MusicSyncPanel;
import de.lars.remotelightclient.ui.panels.output.OutputPanel;
import de.lars.remotelightclient.ui.panels.scenes.ScenesPanel;
import de.lars.remotelightclient.ui.panels.screencolor.ScreenColorPanel;
import de.lars.remotelightclient.ui.panels.scripts.ScriptsPanel;
import de.lars.remotelightclient.ui.panels.settings.SettingsPanel;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanel;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.settings.SettingsManager;

public class DefaultMenuPanelFactory implements MenuPanelFactory {
	
	private MainFrame frame;
	private SettingsManager sm;

	/**
	 * Create a new menu panel factory for all default RemoteLight menu panels.
	 * 
	 * @param mainFrame		the main frame instance (needed by some menu panels)
	 * @param sm			the settings manager instance (also needed by some panels)
	 */
	public DefaultMenuPanelFactory(MainFrame mainFrame, SettingsManager sm) {
		this.frame = mainFrame;
		this.sm = sm;
	}

	@Override
	public MenuPanel getMenuPanel(String name) {
		switch(name)
		{
		case "settings":
			return new SettingsPanel(frame, sm);
		case "output":
			return new OutputPanel(frame);
		case "colors":
			return new ColorsPanel();
		case "animations":
			return new AnimationsPanel();
		case "scenes":
			return new ScenesPanel();
		case "musicsync":
			return new MusicSyncPanel();
		case "screencolor":
			return new ScreenColorPanel();
		case "scripts":
			return new ScriptsPanel();
		case "about":
			return new AboutPanel();
		case "tools":
			return new ToolsPanel();
			
		default:
			return null;
		}
	}
	
	
	/**
	 * Register all default menu items.
	 * 
	 * @param frame		the MainFrame instance to register with
	 */
	public static void addMenuItems(MainFrame frame) {
		List<MenuItem> s = frame.getMenuItems();
		s.add(new MenuItem("output", i18n.getString("Basic.Output"), "Basic.Output", MenuIcon.OUTPUTS));
		s.add(new MenuItem("colors", i18n.getString("Basic.Colors"), "Basic.Colors", MenuIcon.COLOR_PALETTE));
		s.add(new MenuItem("animations", i18n.getString("Basic.Animations"), "Basic.Animations", MenuIcon.ANIMATION));
		s.add(new MenuItem("scenes", i18n.getString("Basic.Scenes"), "Basic.Scenes", MenuIcon.SCENE));
		s.add(new MenuItem("musicsync", i18n.getString("Basic.MusicSync"), "Basic.MusicSync", MenuIcon.MUSICSYNC));
		s.add(new MenuItem("screencolor", i18n.getString("Basic.ScreenColor"), "Basic.ScreenColor", MenuIcon.SCREENCOLOR));
		s.add(new MenuItem("scripts", i18n.getString("Basic.Scripts"), "Basic.Scripts", MenuIcon.SCRIPT));
		s.add(new MenuItem("tools", i18n.getString("Basic.Tools"), "Basic.Tools", MenuIcon.ERROR));
		s.add(new MenuItem("settings", i18n.getString("Basic.Settings"), "Basic.Settings", MenuIcon.SETTINGS));
		s.add(new MenuItem("about", i18n.getString("Basic.About"), "Basic.About", MenuIcon.ABOUT));
	}

}
