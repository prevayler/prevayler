package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.implementation.clock.MachineClock;
import org.prevayler.implementation.log.TransactionLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RollbackPrevayler extends SnapshotPrevayler {

    private Object foodTaster;

    public RollbackPrevayler(Object prevalentSystem, String prevaylerBase) throws ClassNotFoundException, IOException {
        super(prevalentSystem, new SnapshotManager(prevaylerBase), new TransactionLogger(prevaylerBase, new MachineClock()));

        // restore a foodTaster
        doRollback();
    }

    public RollbackPrevayler(Object initialSystem, SnapshotManager snapshotManager, TransactionPublisher transactionPublisher) throws ClassNotFoundException, IOException {
        super(initialSystem, snapshotManager, transactionPublisher);

        // restore a foodTaster
        doRollback();
    }


    /** Any incomplete transactions will not be visible in the system returned from this method.
     */
    public Object prevalentSystem() {
        return king();
    }

    private Object king() {
        return _prevalentSystem;
    }


    public void execute(Transaction transaction) {
        try {
System.out.println("The RollbackPrevayler isn't ready to be used.");//The correct timestamp must be passed below, instead of null.
            transaction.executeOn(foodTaster, null);
            _publisher.publish(transaction);
        } catch (RuntimeException rx) {
            doRollback();
            throw rx;
        } catch (Error error) {
            doRollback();
            throw error;
        }
    }


    private void doRollback() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            _snapshotManager.writeSnapshot(king(), out);
            foodTaster = _snapshotManager.readSnapshot(new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception e) {
             throw new RuntimeException("Could not rollback.");
        }
    }

}
