package de.lars.remotelightclient.api;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.utils.DirectoryUtil;

public class RemoteLightAPI extends Main {

	public RemoteLightAPI() {
		super(false);
	}
	
	
	//////////////////////////////
	// Directory things
	//////////////////////////////
	/**
	 * Sets the root directory path of all stored files <br/>
	 * Default: <code>user.home</code>
	 * @param path Path as String
	 */
	public static void setRootDirectory(String path) {
		DirectoryUtil.setRootPath(path);
	}
	
	/**
	 * Get the root directory path <br/>
	 * Default: <code>user.home</code>
	 * @return 
	 * @return Path as String
	 */
	public static String getRootDirectory() {
		return DirectoryUtil.getRootPath();
	}
	
	/**
	 * Sets the name of the root folder <br/>
	 * Default: <code>.RemoteLight</code>
	 * @param name
	 */
	public static void setRootName(String name) {
		DirectoryUtil.DATA_DIR_NAME = name;
	}
	
	/**
	 * Get the name of the root folder <br/>
	 * Default: <code>.RemoteLight</code>
	 * @return Name as String
	 */
	public static String getRootName() {
		return DirectoryUtil.DATA_DIR_NAME;
	}
	
}
