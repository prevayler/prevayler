package org.prevayler.socketserver.server;

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

