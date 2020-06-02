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
	
	private boolean compareVersionNumber(String current, String update) {
		//convert current to double (e.g. 0.2.0.1 -> 0.201)
		double curD = versionNumberToDouble(current);
		//convert new to double (e.g. 0.2.0.4 -> 0.204)
		double newD = versionNumberToDouble(update);
		return curD < newD;
	}
	
	private double versionNumberToDouble(String versionNumber) {
		String curIn = versionNumber;
		String curOut = "";
		double curD = 0D;
		if(curIn.contains(".")) {
			curOut = curIn.substring(0, curIn.indexOf(".") + 1);
			curIn = curIn.substring(curIn.indexOf(".") + 1);
			
			while(curIn.contains(".")) {
				curOut += curIn.substring(0, curIn.indexOf("."));
				curIn = curIn.substring(curIn.indexOf(".") + 1);
			}
			for(int i = 0; i < curIn.length(); i++) {
				if(Character.isDigit(curIn.charAt(i))) {
					curOut += curIn.charAt(i);
				}
			}
			curD = Double.parseDouble(curOut);
		}
		return curD;
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
