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
 * Méthodes pratiques pour logger des messages à la console
 * @author Lionel HERVIER
 *
 */
public class ConsoleUtils {

	/**
	 * Un formateur de date
	 */
	private final static ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
		public DateFormat get() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	
	/**
	 * Retourne une console
	 * @param name le nom de la console
	 * @return la console
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
	 * Envoi un message à la console
	 * @param level le niveau de logging
	 * @param message
	 * @param e Une exception à logger
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
	 * Envoi un message à la console
	 * @param message le message
	 * @param e Une exception
	 */
	public static void info(String message, Throwable e) {
		message("INFO", message, e);
	}
	
	/**
	 * Envoi un message à la console
	 * @param message le message
	 * @param e Une exception
	 */
	public static void info(String message) {
		message("INFO", message, null);
	}
	
	/**
	 * Envoi un message à la console
	 * @param e Une exception
	 */
	public static void info(Throwable e) {
		message("INFO", e.getMessage(), e);
	}
	
	/**
	 * Envoi une erreur à la console
	 * @param message le message
	 * @param e l'exception
	 */
	public static void error(String message, Throwable e) {
		message("ERROR", message, e);
	}
	
	/**
	 * Envoi une erreur à la console
	 * @param message le message
	 */
	public static void error(String message) {
		message("ERROR", message, null);
	}
	
	/**
	 * Envoi une erreur à la console
	 * @param e l'exception
	 */
	public static void error(Throwable e) {
		message("ERROR", e.getMessage(), e);
	}
}
