package org.prevayler.socketserver.server;

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

import java.util.HashMap;

/**
 * Class Reaper.<p>
 * Kills NotificationThreads when their associated command threads die.
 * @author DaveO
 */
public class Reaper {
    private static long id=0;        // For generating client IDs
    
    private static HashMap notificationThreads = new HashMap();

	/**
	 * Method getNextID.
	 * @return Long The next ID
	 */
    protected static long getNextID() {
        return id++;
    }

	/**
	 * Method registerCommandThread.<p>
     * Registers a command thread with the reaper and returns an ID
	 * @return Long The id that was generated
	 */
    public static long registerCommandThread() {
        long id = getNextID();
        return id;
    }

	/**
	 * Method registerNotificationThread.
     * Registers a notification thread with an associated command thread ID
     * with the reaper
	 * @param id The ID of the associated command thread
	 * @param thread The notification thread
	 */
    public static void registerNotificationThread(Long id, NotificationThread thread) {
        notificationThreads.put(id, thread);
    }

	/**
	 * Method commandThreadDied.
     * Tells the reaper to kill the associated notification thread because the
     * command thread died.
	 * @param id The ID of the command thread that died.
	 */
    public static void reap(Long id) {
        NotificationThread thread = (NotificationThread) notificationThreads.get(id);
        if (thread != null) {
        	if (thread.isAlive())
		        thread.interrupt();
        }
        notificationThreads.remove(id);
    }
}

