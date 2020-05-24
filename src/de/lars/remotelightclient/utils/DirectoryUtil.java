/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient.utils;

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
	public final static String LOG_DIR_NAME = "logs";
	public final static String LUA_DIR_NAME = "lua_scripts";
	/** default: <code>/resourcen/</code> */
	public static String RESOURCES_CLASSPATH = "/resourcen/";
	
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
