package org.prevayler.implementation;

import org.prevayler.Prevayler;
import org.prevayler.Transaction;
import org.prevayler.implementation.log.TransactionLogger;

import java.io.IOException;

public class RollbackPrevayler implements Prevayler {

    private Object foodTaster;
    private Object king;

    private SnapshotManager snapshotManager;
    private TransactionPublisher transactionPublisher;
    private long systemVersion;
    private long lastSnapshot;
    private TransactionSubscriber subscriber;


    public RollbackPrevayler(Object prevalentSystem, String prevaylerBase) throws ClassNotFoundException, IOException {
        this(prevalentSystem, new SnapshotManager(prevaylerBase), new TransactionLogger(prevaylerBase));
    }


    public RollbackPrevayler(Object initialSystem, SnapshotManager snapshotManager, TransactionPublisher transactionPublisher) throws ClassNotFoundException, IOException {
        this.snapshotManager = snapshotManager;
        this.transactionPublisher = transactionPublisher;

        systemVersion = snapshotManager.latestVersion();
        king = snapshotManager.readSnapshot(initialSystem, systemVersion);
        if (systemVersion == 0) systemVersion = 1;

        // restore a king
        subscriber = subscriber();
        transactionPublisher.addSubscriber(subscriber, systemVersion);

        // use this king as the foodTaster
        foodTaster = king;

        // restore a new king as a snapshot of the foodTaster (ie use snapshot as a replacement to clone())
        takeSnapshot();
        king = snapshotManager.readSnapshot(null, lastSnapshot);
    }


    public void takeSnapshot() throws IOException {
        synchronized (subscriber) {
            lastSnapshot = systemVersion;
            snapshotManager.writeSnapshot(foodTaster, lastSnapshot);
        }
    }


    private TransactionSubscriber subscriber() {
        return new TransactionSubscriber() {
            public synchronized void receive(Transaction transaction) {
                systemVersion++;

                transaction.executeOn(king);
            }
        };
    }


    /** Any incomplete transactions will not be visible in the system returned from this method.
     */
    public Object prevalentSystem() {
        return king;
    }


    public void execute(Transaction transaction) {

        try {
            transaction.executeOn(foodTaster);
            transactionPublisher.publish(transaction);
        } catch (RuntimeException rx) {
            doRollback();
            throw rx;
        }
    }


    private void doRollback() {
        foodTaster = king;
        try {
            king = snapshotManager.readSnapshot(king, lastSnapshot);
            if (systemVersion != lastSnapshot) {
                systemVersion = lastSnapshot;
                transactionPublisher.addSubscriber(subscriber, lastSnapshot);
            }
        } catch (Exception e) {
             throw new RuntimeException("Could not rollback.");
        }
    }

}
