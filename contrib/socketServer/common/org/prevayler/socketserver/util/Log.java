package org.prevayler.socketserver.util;
/**
 * Generic application message logging.  Since all messages go through here, 
 * it's easy to redirect the messages somewhere other than the default 
 * location (such as log4j, for example) by changing just this code.
 * 
 * @author DaveO
 */
public class Log {

	/**
	 * Log an application error
	 * @param e The exception object
	 * @param message The message to log
	 */
    public static void error(Exception e, String message) {
        System.err.println(message);
        e.printStackTrace();
    }

	/**
	 * Method message. Log a message
	 * @param message The message to log
	 */
	public static void message(String message) {
		System.out.println(message);
	}

    /**
     * Method message. Log a debug message
     * @param message The message to log
     */
    public static void debug(String message) {
        System.out.println(message);
    }

}

