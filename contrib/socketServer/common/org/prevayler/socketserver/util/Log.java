package org.prevayler.socketserver.util;

/*
 * prevayler.socketServer, a socket-based server (and client library)
 * to help create client-server Prevayler applications
 * 
 * Copyright (C) 2003 Advanced Systems Concepts, Inc.
 * 
 * Written by David Orme <daveo@swtworkbench.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

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

