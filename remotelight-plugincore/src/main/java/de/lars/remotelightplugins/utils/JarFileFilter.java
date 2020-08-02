package de.lars.remotelightplugins.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * Checks if file name ends with {@code .jar}
 */
public class JarFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		return pathname.getName().toLowerCase().endsWith(".jar");
	}

}
