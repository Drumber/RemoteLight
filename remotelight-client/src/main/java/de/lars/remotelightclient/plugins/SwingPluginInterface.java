package de.lars.remotelightclient.plugins;

import javax.swing.JFrame;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightplugins.plugininterface.DefaultPluginInterface;

/**
 * Interface for communication between plugins and
 * the RemoteLight Swing application.
 */
public final class SwingPluginInterface extends DefaultPluginInterface {

	private final Main main;
	
	/**
	 * Create a new plugin interface for the RemoteLight swing application
	 * 
	 * @param main	the applications main class
	 */
	public SwingPluginInterface(final Main main) {
		super(main.getCore(), main.getPluginManager());
		this.main = main;
	}
	
	/**
	 * Get the main class of the swing application.
	 * 
	 * @return	main class instance
	 */
	public Main getMainClass() {
		return main;
	}
	
	/**
	 * Get the main frame of the swing application.
	 * 
	 * @return	the main frame (extends {@link JFrame})
	 */
	public MainFrame getMainFrame() {
		return main.getMainFrame();
	}

}
