package org.prevayler.implementation;

import java.io.File;
import java.io.IOException;

import org.prevayler.Prevayler;
import org.prevayler.Transaction;
import org.prevayler.foundation.FileManager;
import org.prevayler.implementation.TransactionSubscriber;
import org.prevayler.implementation.log.TransactionLogger;

public class CheckpointPrevayler implements Prevayler {
    private Object prevalentSystem;
    private long systemVersion = 0;
    private CheckPointTransactionLogger publisher;
    private File directory;
    private TransactionSubscriber subscriber;


    public CheckpointPrevayler(Object newPrevalentSystem, String prevalenceBase) throws IOException, ClassNotFoundException {
        directory = FileManager.produceDirectory(prevalenceBase);
        systemVersion = latestVersion();
        prevalentSystem = newPrevalentSystem;

        publisher = new CheckPointTransactionLogger(prevalenceBase);
        subscriber = new TransactionSubscriber() {
            public synchronized void receive(Transaction transaction) {
                systemVersion++;
                transaction.executeOn(prevalentSystem);
            }
        };

        publisher.addSubscriber(subscriber, systemVersion + 1);
    }


    /** Returns the underlying prevalent system.
     */
    public Object prevalentSystem() {
        return prevalentSystem;
    }


    /** Publishes transaction and executes it on the underlying prevalentSystem(). If a Logger is used as the publisher (default), this method will only return after transaction has been written to disk.
     */
    public void execute(Transaction transaction) {
        publisher.publish(transaction);
    }


    private long latestVersion() throws IOException {
        String[] fileNames = directory.list();
        if (fileNames == null) {
            throw new IOException("Error reading file list from directory " + directory);
        }

        long result = 0;
        for (int i = 0; i < fileNames.length; i++) {
            long candidate = version(fileNames[i]);
            if (candidate > result) result = candidate;
        }
        return result;
    }

    private long version(String fileName) {
        if (!fileName.endsWith("." + suffix())) return -1;
        return Long.parseLong(fileName.substring(0, fileName.indexOf("." + suffix())));    // "00000.snapshot" becomes "00000".
    }

    private String suffix() {
        return "transactionLog";
    }

    public void checkpoint() {
        synchronized (subscriber) {
            publisher.startNewCheckpointFile();
        }
    }

    private class CheckPointTransactionLogger extends TransactionLogger {
        public CheckPointTransactionLogger(String prevalenceBase) throws ClassNotFoundException, IOException {
            super(prevalenceBase);
        }

        protected Transaction transactionFromLogEntry(Object entry) {
            if (entry instanceof Transaction) {
                return (Transaction) entry;
            }

            prevalentSystem = entry;
            return new Transaction() {
                public void executeOn(Object prevalentSystem) {
                }
            };
        }

        public void startNewCheckpointFile() {
            createNewOutputLog();
        }

        protected void createNewOutputLog() {
            super.createNewOutputLog();
            outputToLog(prevalentSystem);
        }
    }
}
