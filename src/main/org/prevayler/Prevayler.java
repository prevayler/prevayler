// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler;

import java.io.IOException;

/**
 * Implementations of this interface can provide transparent persistence and
 * replication to all Business Objects in a Prevalent System. ALL operations
 * that alter the observable state of the Prevalent System must be implemented
 * as Transaction or TransactionWithQuery objects and must be executed using the
 * Prevayler.execute(...) methods. See the demo applications in
 * org.prevayler.demos for examples.
 * 
 * @see PrevaylerFactory
 */
public interface Prevayler<T> {

    /**
     * Returns the Object which holds direct or indirect references to all other
     * Business Objects in the system.
     */
    public T prevalentSystem();

    /**
     * Returns the Clock used to determine the execution time of all Transaction
     * and Queries executed using this Prevayler. This Clock is useful only to
     * Communication Objects and must NOT be used by Transactions, Queries or
     * Business Objects, since that would make them become non-deterministic.
     * Instead, Transactions, Queries and Business Objects must use the
     * executionTime parameter which is passed on their execution.
     */
    public Clock clock();

    /**
     * Executes the given Transaction on the prevalentSystem(). ALL operations
     * that alter the observable state of the prevalentSystem() must be
     * implemented as Transaction or TransactionWithQuery objects and must be
     * executed using the Prevayler.execute() methods. This method synchronizes
     * on the prevalentSystem() to execute the Transaction. It is therefore
     * guaranteed that only one Transaction is executed at a time. This means
     * the prevalentSystem() does not have to worry about concurrency issues
     * among Transactions. Implementations of this interface can log the given
     * Transaction for crash or shutdown recovery, for example, or execute it
     * remotely on replicas of the prevalentSystem() for fault-tolerance and
     * load-balancing purposes. Such a "replicating" implementation is planned
     * for Prevayler release 2.1.
     * 
     * @see PrevaylerFactory
     */
    public void execute(Transaction<? super T> transaction);

    /**
     * Executes the given sensitiveQuery on the prevalentSystem(). A
     * sensitiveQuery is a Query that would be affected by the concurrent
     * execution of a Transaction or other sensitiveQuery. This method
     * synchronizes on the prevalentSystem() to execute the sensitiveQuery. It
     * is therefore guaranteed that no other Transaction or sensitiveQuery is
     * executed at the same time. <br>
     * Robust Queries (queries that do not affect other operations and that are
     * not affected by them) can be executed directly as plain old method calls
     * on the prevalentSystem() without the need of being implemented as Query
     * objects. Examples of Robust Queries are queries that read the value of a
     * single field or historical queries such as: "What was this account's
     * balance at mid-night?".
     * 
     * @return The result returned by the execution of the sensitiveQuery on the
     *         prevalentSystem().
     * @throws Exception
     *             The Exception thrown by the execution of the sensitiveQuery
     *             on the prevalentSystem().
     */
    public <R, E extends Exception> R execute(Query<? super T, R, E> sensitiveQuery) throws E;

    /**
     * Executes the given transactionWithQuery on the prevalentSystem().
     * Implementations of this interface can log the given transaction for crash
     * or shutdown recovery, for example, or execute it remotely on replicas of
     * the prevalentSystem() for fault-tolerance and load-balancing purposes.
     * Such a "replicating" implementation is planned for Prevayler release 2.1.
     * 
     * @return The result returned by the execution of the transactionWithQuery
     *         on the prevalentSystem().
     * @throws Exception
     *             The Exception thrown by the execution of the sensitiveQuery
     *             on the prevalentSystem().
     * @see PrevaylerFactory
     */
    public <R, E extends Exception> R execute(TransactionWithQuery<? super T, R, E> transactionWithQuery) throws E;

    /**
     * The same as execute(TransactionWithQuery) except no Exception is thrown.
     * 
     * @return The result returned by the execution of the
     *         sureTransactionWithQuery on the prevalentSystem().
     */
    public <R> R execute(SureTransactionWithQuery<? super T, R> sureTransactionWithQuery);

    /**
     * Produces a complete serialized image of the underlying PrevalentSystem.
     * This will accelerate future system startups. Taking a snapshot once a day
     * is enough for most applications. This method synchronizes on the
     * prevalentSystem() in order to take the snapshot. This means that
     * transaction execution will be blocked while the snapshot is taken.
     * 
     * @throws IOException
     *             if there is trouble writing to the snapshot file.
     */
    public void takeSnapshot() throws IOException;

    /**
     * Closes any files or other system resources opened by this Prevayler.
     * 
     * @throws IOException
     *             if there is trouble closing a file or some other system
     *             resource.
     */
    public void close() throws IOException;

}
