package org.prevayler.socketserver.example.transactions;

import org.prevayler.socketserver.transactions.RemoteTransaction;

/**
 * Return the entire current list of Todos.
 * 
 * @author djo
 */
public class ListTodos extends RemoteTransaction {

	/**
	 * @see org.prevayler.util.TransactionWithQuery#executeAndQuery(Object)
	 */
	protected Object executeAndQuery(Object prevalentSystem) throws Exception {
		return prevalentSystem;
	}

}
