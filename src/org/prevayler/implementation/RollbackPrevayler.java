package org.prevayler.implementation;

import org.prevayler.Prevayler;
import org.prevayler.Transaction;
import org.prevayler.implementation.log.TransactionLogger;

import java.io.IOException;

public class RollbackPrevayler implements Prevayler {

    private Object master;
    private Object slave;

    private SnapshotManager snapshotManager;
    private TransactionPublisher transactionPublisher;
    private long systemVersion;
    private long lastSnapshot;
    private TransactionSubscriber subscriber;

    public RollbackPrevayler(Object prevalentSystem, String prevaylerBase)
            throws ClassNotFoundException, IOException
    {
        this(prevalentSystem, new SnapshotManager(prevaylerBase), new TransactionLogger(prevaylerBase));
    }

    public RollbackPrevayler(Object initialSystem, SnapshotManager snapshotManager, TransactionPublisher transactionPublisher)
            throws ClassNotFoundException, IOException
    {
        this.snapshotManager = snapshotManager;
        this.transactionPublisher = transactionPublisher;

        systemVersion = snapshotManager.latestVersion();

        // restore a slave
        subscriber = subscriber();
        slave = snapshotManager.readSnapshot(initialSystem, systemVersion);

        if (systemVersion == 0) systemVersion = 1;

        transactionPublisher.addSubscriber(subscriber, systemVersion);

        // use this slave as the master
        master = slave;

        // restore a new slave as a snapshot of the master (ie use snapshot as a replacement to clone())
        takeSnapshot();
        slave = snapshotManager.readSnapshot(null, lastSnapshot);
    }

    public void takeSnapshot() throws IOException {
        synchronized (subscriber) {
            lastSnapshot = systemVersion;
            snapshotManager.writeSnapshot(master, lastSnapshot);
        }
    }

    private TransactionSubscriber subscriber() {
        return new TransactionSubscriber() {
            public synchronized void receive(Transaction transaction) {
                systemVersion++;

                transaction.executeOn(slave);
            }
        };
    }

    /** Any incomplete transactions will not be visible in the system returned from this method.
     */
    public Object prevalentSystem() {
        return slave;
    }

    public void execute(Transaction transaction) throws IOException {
        Exception error = null;
        boolean doCommit;

        try {
            transaction.executeOn(master);
            doCommit = true;
        } catch (Exception e) {
            doCommit = false;
            error = e;
        }

        if (transaction instanceof RollbackTransaction) {
            RollbackTransaction rollbackTransaction = (RollbackTransaction) transaction;
            if (rollbackTransaction.isRollbackOnly()) {
                doCommit = false;
            }
        }

        if (doCommit) {
            transactionPublisher.publish(transaction);
        } else {
            doRollback();
            throw new TransactionRolledbackException(error);
        }
    }

    private void doRollback() {
        master = slave;
        try {
            slave = snapshotManager.readSnapshot(slave, lastSnapshot);
            if (systemVersion != lastSnapshot) {
                systemVersion = lastSnapshot;
                transactionPublisher.addSubscriber(subscriber, lastSnapshot);
            }
        } catch (Exception e) {
             throw new RuntimeException("Could not rollback.", e);
        }
    }
}
