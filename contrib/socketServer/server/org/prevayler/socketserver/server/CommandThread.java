package org.prevayler.socketserver.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.prevayler.socketserver.transactions.*;
import org.prevayler.socketserver.transactions.IDBTransaction;
import org.prevayler.socketserver.transactions.ThrownException;

import org.prevayler.implementation.SnapshotPrevayler;
import org.prevayler.util.TransactionWithQuery;

/**
 * Forwards commands to Prevayler from a single client for its entire session.
 * 
 * @author DaveO
 */
public class CommandThread extends Thread {
    private static long id=0;

    private SnapshotPrevayler prevayler;
    private Socket socket;
    private long myId;

    /**
     * Server socket thread constructor
     */    
    public CommandThread(SnapshotPrevayler p, Socket s) {
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
            	((IDBTransaction)t).setSenderID(new Long(myId));
                Serializable result;
                TransactionWithQuery transaction = (TransactionWithQuery) t;
                try {
                    result = (Serializable) transaction.executeUsing(prevayler);
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

