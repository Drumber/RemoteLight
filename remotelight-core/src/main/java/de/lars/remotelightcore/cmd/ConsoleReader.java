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
