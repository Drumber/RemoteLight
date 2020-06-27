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

package de.lars.remotelightcore.utils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.tinylog.Logger;

public class DirectoryUtil {
	
	private static String rootPath = System.getProperty("user.home");
	public static String DATA_DIR_NAME = ".RemoteLight";
	public final static String DATA_FILE_NAME = "data.dat";
	public final static String FILE_STORAGE_NAME = "data.json";
	public final static String LOG_DIR_NAME = "logs";
	public final static String LUA_DIR_NAME = "lua_scripts";
	/** default: <code>/resources/</code> */
	public static String RESOURCES_CLASSPATH = "/resources/";
	
	/**
	 * Sets the root directory path of all stored files <br/>
	 * Default: <code>user.home</code>
	 * @param path Path as String
	 */
	public static void setRootPath(String path) {
		rootPath = path;
	}
	
	/**
	 * Get the root directory path <br/>
	 * Default: <code>user.home</code>
	 * @return Path as String
	 */
	public static String getRootPath() {
		return rootPath;
	}
	
	/**
	 * 
	 * @return Main directory of RemoteLight
	 */
	public static String getDataStoragePath() {
		return (rootPath + File.separator  + DATA_DIR_NAME + File.separator);
	}
	
	public static String getLogsPath() {
		return (getDataStoragePath() + LOG_DIR_NAME + File.separator);
	}
	
	public static String getLuaPath() {
		return (getDataStoragePath() + LUA_DIR_NAME + File.separator);
	}
	
	/**
	 * 
	 * Deletes all log files older than defined days
	 */
	public static void deleteOldLogs(int days) {
		File dir = new File(DirectoryUtil.getLogsPath());
		dir.mkdir();
		for(File log : dir.listFiles()) {
			long diff = new Date().getTime() - log.lastModified();

			if (diff > days * 24 * 60 * 60 * 1000) {
			    log.delete();
			}
		}
	}
	
	/**
	 * 
	 * @param logfile Log file you want to copy
	 * @param newName Name of the new log file
	 */
	public static void copyAndRenameLog(File logfile, String newName) {
		try {
			Files.copy(logfile.toPath(), new File(getLogsPath() + newName).toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Logger.error(e, "Could not copy log file! (" + logfile.getAbsolutePath() + " -> " + getLogsPath() + newName + ")");
		}
	}
	
	/**
	 * Get the name of the file without extension
	 */
	public static String getFileName(File file) {
		int index = file.getName().lastIndexOf('.');
		if(index < 0) {
			return file.getName();
		}
		return file.getName().substring(0, index);
	}
	
	/**
	 * Copy the contents of a folder from Jar
	 * @param folderName Name of the folder you want to copy
	 * @param destFolder Destination folder
	 * @param replace Delete file if exists
	 * @throws IOException 
	 */
	public static void copyFolderFromJar(String folderName, File destFolder, boolean replace) throws IOException {
		// adapted from https://github.com/wysohn/TriggerReactor/blob/master/core/src/main/java/io/github/wysohn/triggerreactor/tools/JarUtil.java
        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }
        if(folderName.length() > 0 && folderName.charAt(0) == '/') {
        	folderName = folderName.substring(1); // remove '/' at start
        }
		String path = DirectoryUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File fullPath = null;
		try {
			if(!path.startsWith("file")) {
				path = "file://" + path;
			}
			fullPath = new File(new URI(path));
		} catch(URISyntaxException e) {
			Logger.error(e);
		}
		ZipInputStream zis = new ZipInputStream(new FileInputStream(fullPath));
		ZipEntry entry;
		while((entry = zis.getNextEntry()) != null) {
			if(!entry.getName().startsWith(folderName + '/')) {
				continue;
			}
			
			String fileName = entry.getName();
			File filePath = new File(destFolder + File.separator + fileName);
			File file = new File(destFolder + File.separator + filePath.getName());
			if(!file.getName().contains(".")) {
				continue;
			}
			if (!replace && file.exists()) {
				continue;
			}
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);

			int len;
			byte[] buffer = new byte[1024];
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
		}
		zis.closeEntry();
		zis.close();
	}
	
	
	public static void copyZipFromJar(String zipFolder, File destination) throws IOException {
		InputStream is = DirectoryUtil.class.getResourceAsStream(zipFolder);
		ZipInputStream zis = new ZipInputStream(is);
		byte[] buffer = new byte[1024];
		
		destination.mkdirs();
		
		ZipEntry entry;
		while((entry = zis.getNextEntry()) != null) {
			File entryFile = new File(destination, entry.getName());
			
			String entryDirPath = destination.getCanonicalPath();
			String entryFilePath = entryFile.getCanonicalPath();
			
			if(!entryFilePath.startsWith(entryDirPath + File.separator))
				throw new IOException("Zip entry is outside of target directory: " + entry.getName());
			
			if(entry.isDirectory()) {
				entryFile.mkdirs();
				continue;
			}
			
			FileOutputStream fos = new FileOutputStream(entryFile);
			int length;
			while((length = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, length);
			}
			fos.close();
		}
		zis.closeEntry();
		zis.close();
	}
	

}
