// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler;

import java.io.IOException;


/** Implementations of this interface can provide transparent persistence and replication to all business objects in a prevalent system. ALL operations that alter the observable state of the prevalent system must be implemented as Transaction objects and must be executed using Prevayler.execute(Transaction).
 * See the demo applications in org.prevayler.demos for examples.
 */
public interface Prevayler {

	/** Returns the Object which holds direct or indirect references to all other business objects. 
	 */
	public Object prevalentSystem();

	/** Returns the Clock that tells the time as perceived by the business objects in a prevalent system.
	 * @return A Clock that is paused during the execute(Transaction) and execute(Query) methods.
	 */
	public Clock clock();

	/** Executes the given Transaction on the prevalentSystem(). ALL operations that alter the observable state of the prevalentSystem() must be implemented as Transaction objects and must be executed using this method. This method synchronizes on the prevalentSystem() to execute the Transaction. It is therefore guaranteed that only one Transaction is executed at a time. This means the prevalentSystem() does not have to worry about concurrency issues among Transactions.
	 * Implementations of this interface can log the given Transaction for crash or shutdown recovery, for example, or execute it remotely on replicas of the prevalentSystem().
	 */
	public void execute(Transaction transaction);

	/** Executes the given sensitive Query on the prevalentSystem(). A sensitive Query is a Query that can be affected by the execution of a Transaction Synchronizes on the prevalentSystem() to execute the Query. It is therefore guaranteed that only one transaction is executed at a time. This means the prevalentSystem() does not have to worry about concurrency issues among transactions.
	 * Implementations of this interface can log the given transaction for crash or shutdown recovery, for example, or execute it remotely on replicas of the prevalentSystem().
	 */
	public Object execute(Query sensitiveQuery) throws Exception;

	public Object execute(TransactionWithQuery transactionWithQuery) throws Exception;

	/** Produces a complete serialized image of the underlying PrevalentSystem.
	 * This will accelerate future system startups. Taking a snapshot once a day is enough for most applications.
	 * This method synchronizes on the prevalentSystem() in order to take the snapshot. This means that transaction execution will be blocked while the snapshot is taken.
	 * @throws IOException if there is trouble writing to the snapshot file.
	 */
	public void takeSnapshot() throws IOException;

}
