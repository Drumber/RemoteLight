package de.lars.remotelightclient.cmd.exceptions;

public class CommandException extends Exception {
	private static final long serialVersionUID = -8219703998084155448L;
	
	public CommandException() {
	}
	
	public CommandException(String message) {
		super(message);
	}
	
	public CommandException(Throwable cause) {
		super(cause);
	}
	
	public CommandException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CommandException(int argsLength, int expectedArgsLength) {
		super(String.format("Expected %d arguments, but got %d.", expectedArgsLength, argsLength));
	}

}
