package de.lars.remotelightcore.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

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

}
