// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import java.io.*;
import java.util.Date;

import org.prevayler.*;

/**
 * Provides transparent persistence for native Java business objects in a prevalent system.
 * Provides Atomicity, Isolation and Durability for Consistent Transactions executed via the execute(Transaction) method.
 * Enables regular Java object queries to run 3 to 4 orders of magnitude faster than using a database via JDBC.
 * Take a look at the demo applications included with the Prevayler distribution for examples.
 */
public class SnapshotPrevayler implements Prevayler {

    protected final Object _prevalentSystem;
    long _systemVersion = 0;

    protected final SnapshotManager _snapshotManager;

    protected final TransactionPublisher _publisher;
    private final TransactionSubscriber _subscriber = subscriber();
	private boolean _ignoreRuntimeExceptions;


    /** @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
     * @param snapshotManager The SnapshotManager that will be used for reading and writing snapshot files.
     * @param transactionPublisher The TransactionPublisher that will be used for publishing transactions executed with this SnapshotPrevayler.
     */
    public SnapshotPrevayler(Object newPrevalentSystem, SnapshotManager snapshotManager, TransactionPublisher transactionPublisher) throws IOException, ClassNotFoundException {
        _snapshotManager = snapshotManager;

        _systemVersion = _snapshotManager.latestVersion();
        _prevalentSystem = _snapshotManager.readSnapshot(newPrevalentSystem, _systemVersion);

        _publisher = transactionPublisher;
        
        _ignoreRuntimeExceptions = true;     //During old transaction recovery (rolling forward), RuntimeExceptions are ignored because they were already thrown and handled during the first transaction execution.
        _publisher.addSubscriber(_subscriber, _systemVersion + 1);
        _ignoreRuntimeExceptions = false;
    }


    /** Returns the underlying prevalent system.
     */
    public Object prevalentSystem() {
        return _prevalentSystem;
    }


    /** Produces a complete serialized image of the underlying PrevalentSystem.
     * This will accelerate future system startups. Taking a snapshot once a day is enough for most applications.
     * SnapshotPrevayler synchronizes on prevalentSystem() in order to take the snapshot. This means that transaction execution will be blocked while the snapshot is taken.
     * @throws IOException if there is trouble writing to the snapshot file.
     */
    public void takeSnapshot() throws IOException {
        synchronized (_prevalentSystem) {
            _snapshotManager.writeSnapshot(_prevalentSystem, _systemVersion);
        }
    }


    /** Publishes transaction and executes it on the underlying prevalentSystem(). If a Logger is used as the publisher (default), this method will only return after transaction has been written to disk.
     */
    public void execute(Transaction transaction) {
        _publisher.publish(transaction);
    }


    private TransactionSubscriber subscriber() {
        return new TransactionSubscriber() {

            public void receive(Transaction transaction, Date timestamp) {
            	synchronized (_prevalentSystem) {
	                _systemVersion++;
                    try {
                        transaction.executeOn(_prevalentSystem, timestamp);
                    } catch (RuntimeException rx) {
                    	if (!_ignoreRuntimeExceptions) throw rx;
                        rx.printStackTrace();
                    }
	            }
            }

        };
    }

}
