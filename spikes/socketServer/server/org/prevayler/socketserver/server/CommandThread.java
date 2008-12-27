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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.prevayler.socketserver.transactions.*;
import org.prevayler.socketserver.transactions.IRemoteTransaction;
import org.prevayler.socketserver.transactions.ThrownException;

import org.prevayler.Prevayler;
import org.prevayler.TransactionWithQuery;

/**
 * Forwards commands to Prevayler from a single client for its entire session.
 * 
 * @author DaveO
 */
public class CommandThread extends Thread {
    private Prevayler prevayler;
    private Socket socket;
    private long myId;

    /**
     * Server socket thread constructor
     */    
    public CommandThread(Prevayler p, Socket s) {
        prevayler = p;
        socket = s;
        myId = Reaper.registerCommandThread();
    }

    /*
     * Request handling loop
     */
    private void handleRequests() throws Exception {
        boolean done = false;
        ObjectOutputStream o = null;
        ObjectInputStream i = null;

        // First send the connection ID back to the client
        o = new ObjectOutputStream(socket.getOutputStream());
        o.writeObject(new Long(myId));
        
        // Now read commands in a loop until the client is done.
        while (!done) {
            i = new ObjectInputStream(socket.getInputStream());
            Object t = i.readObject();
            if (t instanceof Disconnect) {
                socket.close();
                done = true;
                Reaper.reap(new Long(myId));
            } else if (t instanceof RegisterCallback) {
                Notification.registerCallback(myId, ((RegisterCallback)t).message );
            } else if (t instanceof UnregisterCallback) {
                Notification.unregisterCallback(myId, ((UnregisterCallback)t).message );
            } else {
            	((IRemoteTransaction)t).setSenderID(new Long(myId));
                Serializable result;
                TransactionWithQuery transaction = (TransactionWithQuery) t;
                try {
                    result = (Serializable) prevayler.execute(transaction);
                } catch (Exception e) {
                    result = new ThrownException(e);
                }
                
                o = new ObjectOutputStream(socket.getOutputStream());
                o.writeObject(result);
            }
        }
    }
    
    /*
     * Start a request handling loop and log exceptions
     */
    public void run() {
        try {
            handleRequests();
        } catch (Exception e) {
            Reaper.reap(new Long(myId));
            try {
                socket.close();
            } catch (Exception e2) {}
            e.printStackTrace();
        }
    }
}

