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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.prevayler.socketserver.server.RegisterCallback;
import org.prevayler.socketserver.server.UnregisterCallback;
import org.prevayler.socketserver.transactions.Disconnect;
import org.prevayler.socketserver.transactions.ThrownException;

import org.prevayler.Transaction;
import org.prevayler.socketserver.util.Log;

/**
 * Client connection class.  This class is the main interface between the
 * client and the PrevalentSystem running on the server.
 * 
 * The general philosophy of prevayler.socketServer is that the Transactions 
 * are really just another way to do remote method calls.  They can
 * pass any (Serializable) parameters you want and they can return any 
 * (Serializable) results that you want.  Also, any Exceptions that are thrown
 * on the server are automatically propogated back to the client and re-thrown
 * there.  The only constraint is the Prevalent System one: Make sure that all
 * "Transactions" are deterministic relative to your business objects.
 * 
 * @author DaveO
 */
public class Remote {
    /**
     * The (optional) global object store connection
     */
    public static Remote conn = null;
    
    /**
     * A convenience method to connect to an object store and initialize the 
     * global connection variable for the common case that there is only
     * 1 remote object store in the app.
     */
    public static void connect(String host, int port) throws IOException, ClassNotFoundException {
        conn = new Remote(host, port);
    }
    
    // Private members
    private Socket socket;
    private CallbackThread callbackThread;
    
    // I/O streams
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
	/**
	 * Open a connection to a remote Prevayler object store
	 * @param host
	 * @param port
	 * @throws IOException if it can't connect
	 */
    public Remote(String host, int port) throws IOException, ClassNotFoundException {
        // Connect to remote command server
        socket = new Socket(host, port);

        // Read the connection ID back from the server
        in = new ObjectInputStream(socket.getInputStream());
        connectionID = (Long) in.readObject();

        // Initialize the CallbackThread
        callbackThread = new CallbackThread(host, port+1, connectionID);
        callbackThread.start();
    }
    
    // Connection id
    private Long connectionID;

	/**
	 * Returns the connectionID.
	 * @return Long
	 */
	public Long getConnectionID() {
		return connectionID;
	}

	/**
	 * Execute a Prevayler transaction on the remote object store
	 * @param transaction
	 * @return Serializable
	 * @throws Exception
	 */
    public synchronized Serializable exec(Transaction transaction) throws Exception {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(transaction);
        in = new ObjectInputStream(socket.getInputStream());
        Object result = in.readObject();
        if (result instanceof ThrownException) {
            ThrownException thrownException = (ThrownException) result;
            throw thrownException.exception;
        }
        return (Serializable)result;
    }
    
    /**
     * Method exec_LogErr. Execute a Prevayler transaction; log exceptions
     * and include errorMsg in the log.
     * 
     * @param transaction The transaction to execute
     * @param errorMsg The error message to log if an error occurs
     * @return Serializable the result of running the transaction
     */
    public Serializable execl(Transaction transaction, String errorMsg) {
        Serializable result = null;
        try {
            result = exec(transaction);
        } catch (Exception e) {
            Log.error(e, errorMsg);
            result = null;
        }
        return result;
    }

	/**
     * FIXME: Needs an abstraction layer so it works with either Swing or SWT clients
     * 
	 * Method exec_LogErr_Dialog.  Execute a Prevayler transaction; log exceptions
     * and include errorMsg in the log.  If an exception occurs, also show errorMsg
     * using a MsgDialog.
     * 
	 * @param transaction The transaction to execute
	 * @param errorMsg The error message to display, if necessary
	 * @param shell The parent shell for the MessageDlg
	 * @return Serializable The result of running the remote transaction
	 */
//    public Serializable execld(Transaction transaction, String errorMsg) {
//        Serializable result = null;
//        try {
//            result = exec(transaction);
//        } catch (Exception e) {
//            Log.error(e, errorMsg);
//            result = null;
//        }
//        return result;
//    }

    /**
     * Register interest in callbacks with "message" message
     * @param message The message in which to register interest
     * @param callback The object to call when the event occurs
     */
    public Object registerCallback(String message, IModelCallback callback) throws IOException {
        // Tell the client to be prepared to receive the message
        Object result;
        result = callbackThread.registerCallback(message, callback);
        
        // If it's not a wildcard callback, tell the server it's okay to send the message
        if (!message.equals("")) {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new RegisterCallback(message));
        }
        return result;
    }
    
	/**
	 * Method unregisterCallback.  Express disinterest in a previously interesting callback
	 * @param callback The callback object received when registering the callback
	 */
    public void unregisterCallback(Object callback) throws IOException, CallbackAlreadyFreedException {
        callbackThread.checkCallbackFreed(callback);
        String message = callbackThread.getCallbackMessage(callback);

        // If we're not unregistering a "wildcard" callback...        
        if (!message.equals("")) {
            // ...tell the server to stop sending this message
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new UnregisterCallback(message));
        }
        
        // Tell the client not to expect to receive it any longer
        callbackThread.unregisterCallback(callback);
    }
    
    /**
     * Close the connection to the remote server
     * @throws Exception
     */
    public void close() throws Exception {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(new Disconnect());
        socket.close();
    }
    
}

