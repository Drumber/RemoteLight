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

import static de.lars.remotelightcore.cmd.CMD.CLOSE;
import static de.lars.remotelightcore.cmd.CMD.LIST;
import static de.lars.remotelightcore.cmd.CMD.START;
import static de.lars.remotelightcore.cmd.CMD.STOP;

import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightcore.EffectManager;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.cmd.exceptions.CommandException;

public class CommandParser {
	
	private RemoteLightCore remoteLightCore;
	private boolean outputEnabled;	// enable output messages
	
	public CommandParser(RemoteLightCore mainInstance) {
		this.remoteLightCore = mainInstance;
	}
	
	public void parse(String[] args) throws CommandException {
		if(args == null || args.length == 0)
			return;
		if(args[0].equalsIgnoreCase(START.toString())) {
			if(checkArgsLength(args, 3)) {
				EffectManager em = getEffectManager(args[1]);
				if(em == null)
					throw new CommandException("Invalid effect manager '" + args[1] + "'.");
				
				boolean success = remoteLightCore.getEffectManagerHelper().startEffect(em, args[2]);
				print((success ? "Successfully enabled " : "Could not enable or find ") + args[2]);
			}
		} else if(args[0].equalsIgnoreCase(STOP.toString())) {
			if(checkArgsLength(args, 2)) {
				if(args[1].equalsIgnoreCase("all")) {
					remoteLightCore.getEffectManagerHelper().stopAll();
					print("Successfully stopped all active managers.");
				} else {
					EffectManager em = getEffectManager(args[1]);
					if(em == null)
						throw new CommandException("Invalid effect manager '" + args[1] + "'.");
					
					boolean success = false;
					if(em.isActive()) {
						em.stop();
						success = true;
					}
					print((success ? "Successfully stopped " : "The effect manager was not active: ") + args[1]);
				}
			}
		} else if(args[0].equalsIgnoreCase(LIST.toString())) {
			if(args.length == 1) {
				// list all managers
				List<String> names = new ArrayList<>();
				for(EffectManager em : remoteLightCore.getEffectManagerHelper().getAllManagers())
					names.add(em.getName());
				print("All effect managers: " + String.join(", ", names));
			} else if(args.length == 2) {
				// list all effects
				EffectManager em = getEffectManager(args[1]);
				if(em == null)
					throw new CommandException("Invalid effect manager '" + args[1] + "'.");
				
				List<String> names = remoteLightCore.getEffectManagerHelper().getAllEffects(em);
				if(names == null)
					throw new CommandException("The given effect manager has no effects or is not supported.");
				print("All effects of " + args[1] + ": " + String.join(", ", names));
			}
		} else if(args[0].equalsIgnoreCase(CLOSE.toString())) {
			remoteLightCore.close(true);
		} else {
			// invalid command
			List<String> cmds = new ArrayList<>();
			for(CMD cmd : CMD.values())
				cmds.add(cmd.toString());
			print("Supported commands: " + String.join(", ", cmds));
		}
	}
	
	
	public void print(String text) {
		if(isOutputEnabled())
			System.out.println("[CMD] " + text);
	}
	
	/**
	 * Enable output messages
	 * @param enabled Enable or disable
	 */
	public void setOutputEnabled(boolean enabled) {
		outputEnabled = enabled;
	}
	
	public boolean isOutputEnabled() {
		return outputEnabled;
	}
	
	
	/* ====================	*
	 *	Helper Methods		*
	 * ====================	*/
	
	private boolean checkArgsLength(Object[] args, int expected) throws CommandException {
		if(args.length < expected) {
			throw new CommandException(args.length, expected);
		}
		return true;
	}
	
	/**
	 * Get effect manager
	 * @param text name of the manager
	 * @return effect manager or null if invalid name
	 */
	private EffectManager getEffectManager(String text) {
		for(EffectManager em : remoteLightCore.getEffectManagerHelper().getAllManagers()) {
			if(em.getName().equalsIgnoreCase(text))
				return em;
		}
		return null;
	}

}
