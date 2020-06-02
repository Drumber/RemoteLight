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

package de.lars.remotelightcore.cmd;

public class StartParameterHandler {
	private static final String PARAMETER_PREFIX = "-";
	
	public boolean tray;
	public boolean autoConnect;
	public boolean updateChecker;
	
	public StartParameterHandler(String[] args) {
		if(args.length == 0)
			return;
		
		for(int i = 0; i < args.length; i++) {
			
			if(args[i].startsWith(PARAMETER_PREFIX)) {
				String arg = args[i].substring(1);
				
				if(arg.equalsIgnoreCase("tray") || arg.equalsIgnoreCase("t")) {
					tray = true;
				}
				else if(arg.equalsIgnoreCase("autoconnect") || arg.equalsIgnoreCase("ac")) {
					autoConnect = true;
				}
				else if(arg.equalsIgnoreCase("updatechecker") || arg.equalsIgnoreCase("uc")) {
					updateChecker = true;
				}
				else {
					System.out.println("Invalid parameter '" + args[i] + "'.");
				}
			}
			
		}
	}

}
