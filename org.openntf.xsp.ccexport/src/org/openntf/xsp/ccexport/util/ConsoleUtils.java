package org.openntf.xsp.ccexport.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * Usefull methods to work with Eclipse consoles
 * @author Lionel HERVIER
 */
public class ConsoleUtils {

	/**
	 * Date format. Beware of thread safety !
	 */
	private final static ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
		public DateFormat get() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	
	/**
	 * Return a console
	 * @param name the console name
	 * @return the console
	 */
	private static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for( int i = 0; i < existing.length; i++ ) {
			if( name.equals(existing[i].getName()) )
				return (MessageConsole) existing[i];
		}
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
	
	/**
	 * Send a message to a given console
	 * @param level the logging level
	 * @param message the message
	 * @param e Exception to log
	 */
	private static void message(String level, String message, Throwable e) {
		MessageConsole cons = findConsole("Cc Export Console");
		if( cons == null ) 
			return;
		
		MessageConsoleStream stream = cons.newMessageStream();
		stream.println(df.get().format(new Date()) + " - INFO - " + message);
		if( e != null ) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Utils.closeQuietly(pw);
			Utils.closeQuietly(sw);
			stream.println(sw.toString());
		}
	}
	
	/**
	 * Send an info to the console
	 * @param message the message
	 * @param e Exception to log
	 */
	public static void info(String message, Throwable e) {
		message("INFO", message, e);
	}
	
	/**
	 * Send an info to the console
	 * @param message the message
	 * @param e Exception to log
	 */
	public static void info(String message) {
		message("INFO", message, null);
	}
	
	/**
	 * Send an info to the console
	 * @param e Exception to log
	 */
	public static void info(Throwable e) {
		message("INFO", e.getMessage(), e);
	}
	
	/**
	 * Send an error to the console
	 * @param message the message
	 * @param e Exception to log
	 */
	public static void error(String message, Throwable e) {
		message("ERROR", message, e);
	}
	
	/**
	 * Send an error to the console
	 * @param message the message
	 */
	public static void error(String message) {
		message("ERROR", message, null);
	}
	
	/**
	 * Send an error to the console
	 * @param e Exception to log
	 */
	public static void error(Throwable e) {
		message("ERROR", e.getMessage(), e);
	}
}
