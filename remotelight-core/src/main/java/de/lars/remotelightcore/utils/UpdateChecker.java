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

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.apache.maven.artifact.versioning.ComparableVersion;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class UpdateChecker {
	
	public final String GITHUB_URL = "https://api.github.com/repos/Drumber/RemoteLight/releases";
	private String curTag;
	private String newTag;
	private String newUrl;
	private boolean newVersionAvailable;
	private boolean preRelease;
	
	public UpdateChecker(String currentVersionTag) {
		this.curTag = currentVersionTag;
		check();
	}
	
	private void check() {
		try {
			URL requestUrl = new URL(GITHUB_URL);
			Scanner scanner = new Scanner(requestUrl.openStream());
			String response = scanner.useDelimiter("\\Z").next();
			JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
			JsonObject jsonObj = jsonArray.get(0).getAsJsonObject();
			
			newTag = jsonObj.get("tag_name").getAsString();
			newUrl = jsonObj.get("html_url").getAsString();
			preRelease = jsonObj.get("prerelease").getAsBoolean();
			
			String curVersionNumber = getVersionNumber(curTag);
			String newVersionNumber = getVersionNumber(newTag);
			newVersionAvailable = compareVersionNumber(curVersionNumber, newVersionNumber);
			
			scanner.close();
		} catch (IOException e) {
		}
	}
	
	private String getVersionNumber(String tag) {
		for(int i = 0; i < tag.length(); i++) {
			if(Character.isDigit(tag.charAt(i))) {
				return tag.substring(i);
			}
		}
		return null;
	}
	
	/**
	 * Compares two version tags.
	 * @param current	the current version
	 * @param update	the target version to compare
	 * @return			true if {@code update} is newer than {@code current}
	 */
	public static boolean compareVersionNumber(String current, String update) {
		ComparableVersion versionA = new ComparableVersion(current);
		ComparableVersion versionB = new ComparableVersion(update);
		return versionB.compareTo(versionA) > 0;
	}

	public String getNewTag() {
		return newTag;
	}

	public String getNewUrl() {
		return newUrl;
	}

	public boolean isNewVersionAvailable() {
		return newVersionAvailable;
	}

	public boolean isPreRelease() {
		return preRelease;
	}

}
