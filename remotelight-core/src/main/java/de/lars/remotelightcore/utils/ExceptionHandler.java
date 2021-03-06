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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.tinylog.Logger;

import de.lars.remotelightcore.RemoteLightCore;

/**
 * Used for fatal errors or other exceptions that should be handled
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
	
	private static List<ExceptionEvent> listeners = new ArrayList<>();
	/** use tinylog to print out exception */
	public static boolean useLogger = true;
	
	public interface ExceptionEvent {
		void onException(Throwable e);
	}
	
	public static void handle(Throwable e) {
		handle(null, e);
	}
	
	public static void handle(Thread t, Throwable e) {
		if(t != null)
			printOut(t, e);
		else
			printOut(e);
		for(ExceptionEvent event : listeners) {
			if(event != null)
				event.onException(e);
		}
	}
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		handle(t, e);
	}
	
	
	public static void registerListener(ExceptionEvent l) {
		listeners.add(l);
	}
	
	public static void unregisterListener(ExceptionEvent l) {
		if(listeners.contains(l))
			listeners.remove(l);
	}
	
	private static void printOut(Throwable e) {
		if(useLogger)
			Logger.error(e);
		else
			e.printStackTrace();
	}
	
	private static void printOut(Thread t, Throwable e) {
		if(useLogger)
			Logger.error(e, "Excepion thrown from thread: " + t.toString());
		else {
			System.err.println("Exception thrown from thread: " + t.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * Convert StackTrace to String
	 * @param e Throwable
	 * @return StackTrace as String
	 */
	public static String getStackTrace(Throwable e) {
		// credit: https://stackoverflow.com/a/1149712/12821118
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	/**
	 * Create the error report text including
	 * <li>RemoteLight version
	 * <li>Commit ID (optional)
	 * <li>Java version
	 * <li>Operating system
	 * <li>Stack trace
	 * <li>Date and time
	 * @param e			error object
	 * @param commitId	commit id of the build or {@code null}
	 */
	public static String getReportText(Throwable e, String commitId) {
		String issueText = String.format(""
				+ "This issue was automatically created by the RemoteLight ExceptionHandler.\n"
				+ "RemoteLightCore version: %s\n"
				+ (commitId != null ? "Commit ID: " + commitId + "\n" : "")
				+ "Java version: %s (%s)\n"
				+ "OS: %s\n"
				+ "StackTrace:\n```\n%s\n```"
				+ "\n*%s*",
				RemoteLightCore.VERSION,
				System.getProperty("java.version"), System.getProperty("java.vendor"),
				System.getProperty("os.name"),
				getStackTrace(e),
				DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.US).format(new Date()));
		return issueText;
	}
	
	/**
	 * Generate a GitHub issue URL that automatically creates an issue including the
	 * error report from {@link ExceptionHandler#getReportText}
	 * @param e			error object
	 * @param commitId	commit id of the build or {@code null}
	 * @throws UnsupportedEncodingException
	 */
	public static String getGitHubIssueURL(Throwable e, String commitId) throws UnsupportedEncodingException {
		String issueText = getReportText(e, commitId);
		
		String exceptionTitle = URLEncoder.encode(e.getClass().getCanonicalName(), "UTF-8");
		String exceptionBody = URLEncoder.encode(issueText, "UTF-8");
		
		return RemoteLightCore.GITHUB
				+ "/issues/new?labels=bug&assignees=Drumber&title=" + exceptionTitle
				+ "&body=" + exceptionBody;
	}

}
