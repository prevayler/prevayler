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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Forwards commands to Prevayler from a single client for its entire session.
 * 
 * @author DaveO
 */
public class NotificationThread extends Thread {
    private Socket socket;

    /**
     * This connection's id
     */    
    private Long id;
    
    /**
     * Returns the socket id.
     * @return long
     */
    public Long getSocketId() {
        return id;
    }

	/**
	 * Constructor NotificationThread.
	 * @param socket
	 */
	public NotificationThread(Socket s) throws IOException, ClassNotFoundException {
        socket = s;
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        id = (Long) in.readObject();
        Reaper.registerNotificationThread(id, this);
	}
    
    /**
     * Here's where we queue up messages that need to be sent
     */
    private LinkedList messageQueue = new LinkedList();
    
    /**
     * Here's the message class that we store in the queue
     */
    private class Message {
        public Message(Long senderId, String m, Object o) {
        	this.senderId = senderId;
            message = m;
            obj = o;
        }
        public Long senderId;
        public String message;
        public Object obj;
    }

	/**
	 * Submit a message to be sent to the client
	 * @param message
	 */
	public synchronized void submit(Long senderId, String message, Object obj) {
        messageQueue.addLast(new Message(senderId, message, obj));
        notifyAll();
	}

    /*
     * Get a message from the message queue to send
     */
    private synchronized Message getMessage() {
        Message result;
        try {
            result = (Message) messageQueue.getFirst();
            messageQueue.removeFirst();
        } catch (NoSuchElementException e) {
            result = null;
        }
        return result;
    }


    /*
     * Is the message queue empty?  If so, wait for a message.
     */
    private synchronized void checkWait() throws Exception {
        if (messageQueue.isEmpty())
            wait();
    }

    /*
     * Request handling loop
     */
    private void handleNotifications() throws Exception {
        // This loop is broken when thread.interrupt() is called by the Reaper
        while (true) {
            checkWait();
            Message message = getMessage();
            ObjectOutputStream o = new ObjectOutputStream(socket.getOutputStream());
            o.writeObject(message.message);
            o = new ObjectOutputStream(socket.getOutputStream());
            o.writeObject(message.senderId);
            o.writeObject(message.obj);
        }
    }

    /*
     * Start a request handling loop and log exceptions
     */
    public void run() {
        try {
            handleNotifications();
        } catch (Exception e) {
            try {
                socket.close();
            } catch (Exception e2) {}
        }
    }
    
}

