// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler;

/**
 * Implementations of this interface can provide transparent persistence and replication to all business objects in a prevalent system. All transactions to the system must be represented as Transaction objects and must be executed using Prevayler.execute(Transaction).
 * See the demo applications in org.prevayler.demos for examples.
 */
public interface Prevayler {

	/**
	 * Returns the underlying prevalent system.
	 */
	public Object prevalentSystem();

	/**
	 * Executes the given transaction on the underlying prevalentSystem(). All implementations of this interface must synchronize on the prevalentSystem() to execute the transaction. It is therefore guaranteed that only one transaction is executed at a time. This means the prevalentSystem() does not have to worry about concurrency issues among transactions.
	 * Implementations of this interface can log the given transaction for crash or shutdown recovery, for example, or execute it remotely on replicas of the underlying prevalent system.
	 */
	public void execute(Transaction transaction);

}
