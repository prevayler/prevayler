package org.prevayler.socketserver.client;

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

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.LinkedList;

import org.prevayler.socketserver.util.Log;

/**
 * This thread receives update notifications from the remote model and propogates
 * them to the correct client object.
 * 
 * @author DaveO
 */
public class CallbackThread extends Thread {
    private Socket socket;
    private LinkedList callbacks = new LinkedList();

	/**
	 * Constructor CallbackThread.
	 * @param host
	 * @param port
	 */
	public CallbackThread(String host, int port, Long connectionID) throws IOException {
        // Initialize and open the connection
        setDaemon(true);
        socket = new Socket(host, port);
        
        // Send the server our connection ID
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(connectionID);
	}

    private class CallbackNode {
        public CallbackNode(String m, IModelCallback c) {
            message = m;
            callback = c;
            freed=false;
        }
        public String message;
        public IModelCallback callback;
        public boolean freed;
    }
    
	/**
	 * Method registerCallback.
	 * @param message The message or "" for all messages
	 * @param callback The callback object
     * @return A handle to the callback you just created
	 */
	public synchronized Object registerCallback(String message, IModelCallback callback) {
        CallbackNode c = new CallbackNode(message, callback);
        callbacks.addLast(c);
        return c;
	}
    
    /**
     * Express disinterest in previously interesting callbacks
     * @param The handle to the callback that you got when you created it
     */
    public synchronized void unregisterCallback(Object callback) {
        Iterator i = callbacks.iterator();
        while (i.hasNext()) {
            if ((CallbackNode)i.next() == (CallbackNode)callback) {
                i.remove();
            }
        }
        ((CallbackNode)callback).freed = true;
    }
    
	/**
	 * Method getCallbackMessage. Returns the message string for a callback object
	 * @param callback The callback object
	 * @return String The message this callback will send
	 */
    public String getCallbackMessage(Object callback) {
        return ((CallbackNode)callback).message;
    }
    
	/**
	 * Method checkCallbackFreed.  Makes sure a callback isn't already freed
	 * @param callback
	 * @throws CallbackAlreadyFreedException
	 */
    public void checkCallbackFreed(Object callback) throws CallbackAlreadyFreedException {
        CallbackNode c = (CallbackNode) callback;
        if (c.freed)
            throw new CallbackAlreadyFreedException("Callback has already been freed: " + c.message);
    }

    /*
     * We've got a message, now call all callbacks that need to hear about it
     */    
    private synchronized void callCallbacks(Long senderID, String message, Object obj) {
        Iterator i = callbacks.iterator();
        while (i.hasNext()) {
            CallbackNode c = (CallbackNode)i.next();
            if (c.message.equals(message) || c.message == "") {
                c.callback.happened(senderID, message, obj);
            }
        }
    }

    /**
     * Process callbacks
     */
    public void run() {
        while (true) {
	        try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                String message = (String)in.readObject();
                in = new ObjectInputStream(socket.getInputStream());
                Long senderID = (Long) in.readObject();
                Object obj = in.readObject();
                callCallbacks(senderID, message, obj);
	        } catch (Exception e) {
	            if (e instanceof EOFException) {
	                // Do nothing, this is normal behavior on connection close
	            	break;
	            } else if (e instanceof SocketException){
                    Log.debug("Server disconnected");
                    Log.debug(e.toString());
                    break;
                } else {
                    Log.error(e, "Unexpected exception");
	            }
	        }
        }
    }
}

