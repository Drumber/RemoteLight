package de.lars.remotelightcore.cmd;

import java.util.Scanner;

import de.lars.remotelightcore.cmd.exceptions.CommandException;

public class ConsoleReader {
	
	private CommandParser cmdParser;
	
	public ConsoleReader(CommandParser commandParser) {
		cmdParser = commandParser;
		start();
	}
	
	private void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner scanner = new Scanner(System.in);
				while(scanner.hasNextLine()) {
					String line = scanner.nextLine();
					try {
						cmdParser.parse(line.split(" "));
					} catch (CommandException e) {
						System.err.println(e.getMessage());
					}
				}
				scanner.close();
			}
		}, "Console Input Reader").start();
	}

}
