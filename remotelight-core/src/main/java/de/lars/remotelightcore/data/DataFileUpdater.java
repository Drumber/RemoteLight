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

package de.lars.remotelightcore.data;

import java.io.*;
import java.util.HashMap;

import org.tinylog.Logger;

public class DataFileUpdater extends ObjectInputStream {
	
	private String oldPackagePrefix = "de.lars.remotelightclient";
	private String newPackagePrefix = "de.lars.remotelightcore";
	
	private File file;

	/**
	 * Enables backward compatibility
	 * <p>
	 * Updates old package names to new package names
	 */
	public DataFileUpdater(File file) throws IOException {
		super(new BufferedInputStream(new FileInputStream(file)));
		this.file = file;
	}
	
	public void setOldPackagePrefix(String oldPackagePrefix) {
		this.oldPackagePrefix = oldPackagePrefix;
	}

	public void setNewPackagePrefix(String newPackagePrefix) {
		this.newPackagePrefix = newPackagePrefix;
	}

	
	@SuppressWarnings("unchecked")
	public void updateData() throws ClassNotFoundException, IOException {
		// try to read data map
		HashMap<String, Object> storageMap = (HashMap<String, Object>) this.readObject();
		this.close();
		
		// and save it to file
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		oos.writeObject(storageMap);
		oos.flush();
		oos.close();
		
		Logger.info("Successfully updated data file.");
	}
	
	@Override
	protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
		// adopted from https://stackoverflow.com/a/5305751/12821118
		ObjectStreamClass desc = super.readClassDescriptor();
		
	    if (desc.getName().contains(oldPackagePrefix)) {
	    	String newClassName = desc.getName().replace(oldPackagePrefix, newPackagePrefix);
	    	Logger.debug("Try updating " + desc.getName() + " to " + newClassName);
	    	try {
	    		
	    		Class<?> cls = Class.forName(newClassName);
		        return ObjectStreamClass.lookup(cls);
		        
	    	} catch (LinkageError | ClassNotFoundException e) {
	    		// class not found
	    		Logger.error(e, "Could not update class.");
			}
	    }
	    return desc;
	}

}
