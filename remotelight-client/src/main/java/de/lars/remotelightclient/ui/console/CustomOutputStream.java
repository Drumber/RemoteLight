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

package de.lars.remotelightclient.ui.console;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.events.ConsoleOutEvent;
import de.lars.remotelightcore.event.EventHandler;

/**
 * Custom output stream used to capture system out streams.
 */
public class CustomOutputStream extends OutputStream {

	/** ANSI escape code for red */
	public static final String ANSI_RED = "\u001B[31m";
	
	private static CustomOutputStream outNormal;
	private static CustomOutputStream outError;
	/** static string builder holding the whole console data */
	private static StringBuilder stringBuilder;
	
	private final PrintStream outStream;
	private final boolean isErrorOut;
	/** buffer string builder holding online one line */
	private StringBuilder sbBuffer;
	
	
	/**
	 * Initialize both static custom output streams.
	 */
	public static void init() {
		stringBuilder = new StringBuilder();
		PrintStream console = System.out;
		PrintStream consoleErr = System.err;
		outNormal = new CustomOutputStream(console, false);
		outError = new CustomOutputStream(consoleErr, true);
		System.setOut(new PrintStream(outNormal));
		System.setErr(new PrintStream(outError));
	}
	
	/**
	 * Get the normal custom output stream.
	 * 
	 * @return	CustomOutputStream for normal messages
	 */
	public static CustomOutputStream getNormalOut() {
		return outNormal;
	}
	
	/**
	 * Get the error custom output stream.
	 * 
	 * @return	CustomOutputStream for errors messages
	 */
	public static CustomOutputStream getErrorOut() {
		return outError;
	}
	
	/**
	 * Get the main string builder holding the whole console log.
	 * 
	 * @return	StringBuilder holding the console data
	 */
	public static StringBuilder getStringBuilder() {
		return stringBuilder;
	}
	
	
	/**
	 * Create a new custom output stream.
	 * 
	 * @param frame				console frame instance
	 * @param outStream			the system output stream
	 * @param errorOutStream	whether the stream is a normal
	 * 							or error output stream
	 */
	public CustomOutputStream(PrintStream outStream, boolean errorOutStream) {
		sbBuffer = new StringBuilder();
		this.outStream = outStream;
		isErrorOut = errorOutStream;
	}
	
	@Override
	public void write(int b) throws IOException {
		// output to regular console output
		if(outStream != null) {
			outStream.write(b);
		}
		
		char c = (char) b;
		sbBuffer.append(c);
		
		// if new line is called, add to main string builder and fire event
		if(c == '\n') {
			// get the buffered line
			String line = sbBuffer.toString();
			
			// if the data is from error output, add red marker right before the line
			if(isErrorOut) {
				line = ANSI_RED + line;
			}
			
			// add line to main string builder
			stringBuilder.append(line);
			
			// call event
			EventHandler eh = getEventHandler();
			if(eh != null) {
				eh.call(new ConsoleOutEvent(isErrorOut, line));
				
			}
			
			// clear buffer
			sbBuffer.setLength(0);
		}
	}
	
	
	private EventHandler getEventHandler() {
		if(Main.getInstance().getCore() != null) {
			return Main.getInstance().getCore().getEventHandler();
		}
		return null;
	}

}
