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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.prevayler.socketserver.util.Log;

/**
 * This object allows model objects to notify clients of changes
 * @author DaveO
 */
public class Notification extends Thread {

    // Static part of class here----------------------
    private static LinkedList clients = new LinkedList();
    
    /**
     * Use a hash of hashes to keep track of what clients have what messages enabled  
     * <ul>
     * <li>The first hash indexes by connection ID and contains another hash.
     * <li>The second hash indexes by message string and contains a count of clients 
     *      interested in that message
     * </ul>
     * 
     * We do this because on most platforms it is much more expensive to unnecessarily
     * enable a bunch of threads than to look stuff up in a couple of fairly large hashes.
     */
    private static HashMap enabledCallbacks = new HashMap();

	/**
	 * Submit a callback message to be sent
	 * @param message The message to be sent
	 * @param obj An object (usually a model object) containing information about what happened.
	 */
    public static synchronized void submit(Long senderID, String message, Object obj) {
        NotificationThread client;
        
        Iterator i = clients.iterator();
        while (i.hasNext()) {
            client = (NotificationThread)i.next();
            if (client.isAlive()) {
                if (enabledCallbacks.containsKey(client.getSocketId())) {
                    HashMap callbacks = (HashMap) enabledCallbacks.get(client.getSocketId());
                    if (callbacks.containsKey(message)) {
        	            client.submit(senderID, message, obj);
                    }
                }
            } else {
                // If the thread has died (the client disconnected), remove it
                i.remove();

                // ...and remove any callbacks registered for it
                enabledCallbacks.remove(client.getSocketId());
            }
        }
    }

    /**
     * Method registerCallback.  Called internally from the CommandThread server.
     * @param myId The id of the client requesting to receive the callback
     * @param message The message the client wants to receive
     */
    public static void registerCallback(long myId, String message) {
        Long id = new Long(myId);
        HashMap callbacks;

        // Get the hash of enabled callbacks for this connection ID
        if (enabledCallbacks.containsKey(id)) {
            callbacks = (HashMap) enabledCallbacks.get(id);
        } else {
            callbacks = new HashMap();
            enabledCallbacks.put(id, callbacks);
        }
        
        // If this message isn't already registered, register it
        if (!callbacks.containsKey(message)) {
            callbacks.put(message, new Integer(1));
        } else {
            Integer numInterested = (Integer) callbacks.get(message);
            callbacks.put(message, new Integer(numInterested.intValue()+1));
        }
    }
    
	/**
	 * Method unregisterCallback.  Called internally from the CommandThread server.
	 * @param myId The connection id requesting disinterest
	 * @param message The message in which the connection is no longer interested
	 */
    public static void unregisterCallback(long myId, String message) {
        Long id = new Long(myId);
        HashMap callbacks = null;

        // Get the hash of enabled callbacks for this connection ID
        if (enabledCallbacks.containsKey(id)) {
            callbacks = (HashMap) enabledCallbacks.get(id);
        }
        
        // Decrement the count of interested parties in this callback or remove it if nobody is interested
        if (callbacks != null) {
            if (callbacks.containsKey(message)) {
                Integer numInterested = (Integer) callbacks.get(message);
                
                // If nobody is interested, remove the callback
                if (numInterested.intValue() == 1) {
                    callbacks.remove(message);
                } 
                // Otherwise, decrement the number of interested parties
                else {
                    callbacks.put(message, new Integer(numInterested.intValue()-1));
                }
            }
        }
    }


    // Non-static part of class here------------------

    private int port;

	/**
	 * Constructor Notification.
	 * @param p Port number on which to listen
	 */
	public Notification(int p) {
        port = p;
	}


    public void run() {
        ServerSocket ss = null;
        boolean listening = true;
        
        // Listen for a client connection and dispatch a thread to handle it
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
        	Log.error(e, "Couldn't open notification server port");
            System.exit(-1);
        }
        
        while (listening) {
	        try {
                NotificationThread client = new NotificationThread(ss.accept());
                clients.addLast(client);
                client.start();
	        } catch (Exception e) {
	        	Log.error(e, "Notification server error");
	        }
        }
		try {
			ss.close();
		} catch (IOException e) {
        	Log.error(e, "Error closing notification server socket");
		}
    }


}

