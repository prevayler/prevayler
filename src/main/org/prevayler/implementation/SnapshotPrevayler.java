// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import java.io.*;

import org.prevayler.*;
import org.prevayler.implementation.log.TransactionLogger;


/**
 * Provides transparent persistence and replication for native Java business objects.
 * Provides Atomicity, Isolation and Durability for Consistent Transactions executed via the execute(Transaction) method.
 * Enables regular Java object queries to run 3 to 4 orders of magnitude faster than using a database via JDBC.
 * Take a look at the demo applications included with the Prevayler distribution for examples.<br>
 * <br>
 * A SnapshotPrevayler can also be configured for running demos or automated business test scripts only in RAM, orders of magnitude faster than with persistence turned on. See the constructors.
 */
public class SnapshotPrevayler implements Prevayler {

    protected final Object _prevalentSystem;
    long _systemVersion = 0;

    protected final SnapshotManager _snapshotManager;

    protected final TransactionPublisher _publisher;
    private final TransactionSubscriber _subscriber = subscriber();
    private boolean _ignoreExceptions;


    /** Creates a SnapshotPrevayler that will use the current directory to read and write its snapshot files.
     * @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
     */
    public SnapshotPrevayler(Object newPrevalentSystem) throws IOException, ClassNotFoundException {
        this(newPrevalentSystem, "PrevalenceBase");
    }

    /** @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
     * @param prevalenceBase The directory where the snapshot files and transactionLog files will be read and written.
     */
    public SnapshotPrevayler(Object newPrevalentSystem, String prevalenceBase) throws IOException, ClassNotFoundException {
        this(newPrevalentSystem, new SnapshotManager(prevalenceBase), new TransactionLogger(prevalenceBase));
    }

    /** @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
     * @param snapshotManager The SnapshotManager that will be used for reading and writing snapshot files.
     * @param transactionPublisher The TransactionPublisher that will be used for publishing transactions executed with this SnapshotPrevayler.
     */
    public SnapshotPrevayler(Object newPrevalentSystem, SnapshotManager snapshotManager, TransactionPublisher transactionPublisher) throws IOException, ClassNotFoundException {
        _snapshotManager = snapshotManager;

        _systemVersion = _snapshotManager.latestVersion();
        _prevalentSystem = _snapshotManager.readSnapshot(newPrevalentSystem, _systemVersion);

        _publisher = transactionPublisher;
        
        // ignore exceptions during startup 
        _ignoreExceptions = true;
        _publisher.addSubscriber(_subscriber, _systemVersion + 1);
        _ignoreExceptions = false;
    }


    /** Returns the underlying prevalent system.
     */
    public Object prevalentSystem() {
        return _prevalentSystem;
    }


    /** Produces a complete serialized image of the underlying PrevalentSystem.
     * This will accelerate future system startups. Taking a snapshot once a day is enough for most applications.
     * Subsequent calls to execute(Transaction) will be Publisherd until the snapshot is taken.
     * @throws IOException if there is trouble writing to the snapshot file.
     */
    public void takeSnapshot() throws IOException {
        synchronized (_subscriber) {
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
            public synchronized void receive(Transaction transaction) {
                _systemVersion++;
                if (_ignoreExceptions) {
                    try {
                        transaction.executeOn(_prevalentSystem);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } else {
                    transaction.executeOn(_prevalentSystem);
                }
            }
        };
    }

}
