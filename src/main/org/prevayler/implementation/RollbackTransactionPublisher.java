package org.prevayler.implementation;

import org.prevayler.Transaction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RollbackTransactionPublisher implements TransactionPublisher {
    private SnapshotManager snapshotManager;
    private TransactionPublisher targetPublisher;
    private Object foodTaster;
    private Object king;

    public RollbackTransactionPublisher(SnapshotManager snapshotManager, TransactionPublisher targetPublisher)
            throws IOException, ClassNotFoundException
    {
        this.snapshotManager = snapshotManager;
        this.targetPublisher = targetPublisher;
    }

    public void addSubscriber(TransactionSubscriber subscriber, long initialTransaction) throws IOException, ClassNotFoundException {
        targetPublisher.addSubscriber(subscriber, initialTransaction);
    }

    public void publish(Transaction transaction) {
        try {
            transaction.executeOn(foodTaster);
            targetPublisher.publish(transaction);
        } catch (RuntimeException rx) {
            doRollback();
            throw rx;
        } catch (Error error) {
            doRollback();
            throw error;
        }
    }

    public void initKing(Object king) {
        this.king = king;
        doRollback();
    }

    private void doRollback() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            snapshotManager.writeSnapshot(king, out);
            foodTaster = snapshotManager.readSnapshot(new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception e) {
             throw new RuntimeException("Could not rollback.");
        }
    }
}
