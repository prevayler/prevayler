package org.prevayler.socketserver.transactions;


/**
 * An interface to allow commands and callbacks to identify the client that
 * originally sent the command that initiated the callback.  This setter is
 * called automatically within CommandThread.  The client is responsible for
 * providing storage and any desired getter access method.
 * 
 * All Command objects that pass through the Prevayler server must implement
 * this interface.  An easy way to accomplish this is to have them inherit
 * from RemoteTransaction, which provides a default implementation of this interface.
 * 
 * @author DaveO
 */
public interface IRemoteTransaction {

	/**
	 * Sets the connectionID.
	 * @param connectionID The connectionID to set
	 */
	public void setSenderID(Long connectionID);

}

