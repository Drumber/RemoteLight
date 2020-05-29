package de.lars.remotelightclient.cmd;

import static de.lars.remotelightclient.cmd.CMD.CLOSE;
import static de.lars.remotelightclient.cmd.CMD.LIST;
import static de.lars.remotelightclient.cmd.CMD.START;
import static de.lars.remotelightclient.cmd.CMD.STOP;

import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightclient.EffectManager;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.cmd.exceptions.CommandException;

public class CommandParser {
	
	private Main main;
	private boolean outputEnabled;	// enable output messages
	
	public CommandParser(Main mainInstance) {
		this.main = mainInstance;
	}
	
	public void parse(String[] args) throws CommandException {
		if(args == null || args.length == 0)
			return;
		if(args[0].equalsIgnoreCase(START.toString())) {
			if(checkArgsLength(args, 3)) {
				EffectManager em = getEffectManager(args[1]);
				if(em == null)
					throw new CommandException("Invalid effect manager '" + args[1] + "'.");
				
				boolean success = main.getEffectManagerHelper().startEffect(em, args[2]);
				print((success ? "Successfully enabled " : "Could not enable or find ") + args[2]);
			}
		} else if(args[0].equalsIgnoreCase(STOP.toString())) {
			if(checkArgsLength(args, 2)) {
				if(args[1].equalsIgnoreCase("all")) {
					main.getEffectManagerHelper().stopAll();
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
				for(EffectManager em : main.getEffectManagerHelper().getAllManagers())
					names.add(em.getName());
				print("All effect managers: " + String.join(", ", names));
			} else if(args.length == 2) {
				// list all effects
				EffectManager em = getEffectManager(args[1]);
				if(em == null)
					throw new CommandException("Invalid effect manager '" + args[1] + "'.");
				
				List<String> names = main.getEffectManagerHelper().getAllEffects(em);
				if(names == null)
					throw new CommandException("The given effect manager has no effects or is not supported.");
				print("All effects of " + args[1] + ": " + String.join(", ", names));
			}
		} else if(args[0].equalsIgnoreCase(CLOSE.toString())) {
			main.close(true);
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
		for(EffectManager em : main.getEffectManagerHelper().getAllManagers()) {
			if(em.getName().equalsIgnoreCase(text))
				return em;
		}
		return null;
	}

}
