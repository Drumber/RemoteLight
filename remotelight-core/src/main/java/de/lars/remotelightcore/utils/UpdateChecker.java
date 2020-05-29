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
