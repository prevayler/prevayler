package org.prevayler.socketserver.transactions;

import java.io.Serializable;

import org.prevayler.Transaction;
import org.prevayler.util.TransactionWithQuery;

/**
 * A default implementation of IDBTransaction.  Instead of implementing the interface
 * yourself, you can inherit from this class.  If you don't want a single-rooted
 * Transaction hierarchy, however, just implement the interface.
 * 
 * @author DaveO
 */
public abstract class DBTransaction extends TransactionWithQuery implements IDBTransaction {

    protected Long senderID;
    
	/**
	 * Sets the connectionID.
	 * @param connectionID The connectionID to set
	 */
	public void setSenderID(Long connectionID) {
		this.senderID = connectionID;
	}
}

