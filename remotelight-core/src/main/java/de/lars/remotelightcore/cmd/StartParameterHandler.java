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
