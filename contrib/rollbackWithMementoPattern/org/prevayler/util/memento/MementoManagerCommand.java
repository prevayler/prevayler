// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.util.memento;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.prevayler.TransactionWithQuery;

/**
 * This transaction manages the mementos and restores them in case of a failure.
 *
 * @author Johan Stuyts
 * @version 2.0
 */
public class MementoManagerCommand implements TransactionWithQuery, MementoCollector {

	/**
	 * Create a memento manager transaction.
	 * 
	 * @param transaction The actual transaction to execute.
	 */
	public MementoManagerCommand(MementoTransaction transaction) {
		this.transaction = transaction;
	}

	/**
	 * Executes this transaction on the received system. See org.prevayler.demos for examples.
	 * The returned object has to be Serializable in preparation for future versions of
	 * Prevayler that will provide fault-tolerance through system replicas.
	 * 
	 * This method executes the actual transaction and restores the mementos if the execution fails.
	 * 
	 * @param system The prevalent system on which to execute the transaction.
	 * @return The object returned by the execution of this transaction. Most commands simply return null.
	 */
	public Object executeAndQuery(Object prevalentSystem, Date timestamp) throws Exception {
		mementos = new HashMap();
		try {
			return transaction.execute(this, prevalentSystem);
		} catch (Exception e) {
			// Something went wrong. Restore the mementos.
			Iterator iterator;

			iterator = mementos.values().iterator();
			while (iterator.hasNext()) {
				Memento memento = (Memento) iterator.next();
				memento.restore();
			}

			throw e;
		} finally {
			mementos = null;
		}
	}

	/**
	 * Add a memento to the memento collection. A memento will only be added if a memento
	 * with the same owner does not exist.
	 * 
	 * @param memento The memento to add.
	 */
	public void addMemento(Memento memento) {
		if (mementos.get(memento.getOwner()) == null) {
			mementos.put(memento, memento);
		}
	}

	private MementoTransaction transaction;

	private transient Map mementos;

}
