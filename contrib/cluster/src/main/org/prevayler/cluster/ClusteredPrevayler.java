package org.prevayler.cluster;

import java.io.IOException;
import java.io.File;

import org.prevayler.*;

public class ClusteredPrevayler implements Prevayler, ClusterListener {
    private PrevaylerFactory factory;
    private String oldPrevalenceBase;
    private String newPrevalenceBase;
    private Node node;
    private Prevayler prevayler;

    public ClusteredPrevayler(PrevaylerFactory factory, String oldPrevalenceBase, String newPrevalenceBase) throws InterruptedException {
        System.out.println("EXPERIMENTAL CODE! Spike for implementing a clustered homogenous (i.e. without a master) Prevayler using JavaGroups");
        System.out.println("For example, the clock is *not* properly synchronized across the cluster");
        System.out.println("This code will be thrown away when the real implementation is created");
        System.out.println("Prevalence base: " + oldPrevalenceBase + " and " + newPrevalenceBase);
        this.factory = factory;
        this.oldPrevalenceBase = oldPrevalenceBase;
        this.newPrevalenceBase = newPrevalenceBase;
        this.node = new Node(this);
    }

    public Object receive(Object message) {
        ClusteredTransaction transaction = (ClusteredTransaction) message;
        return transaction.executeOn(this);
    }

    public Object getState() {
        System.out.println("Distributing system " + prevalentSystem());
        return prevalentSystem();
    }

    public void setState(Object prevalentSystem) {
        if (prevalentSystem != null) {
            backupOldLocalTransactionLogs();
            factory.configurePrevalentSystem(prevalentSystem);
        }
        try {
            factory.configurePrevalenceBase(newPrevalenceBase);
            prevayler = factory.create();
            System.out.println("Setting new system " + prevayler.prevalentSystem());
            System.out.println("Taking snapshot");
            prevayler.takeSnapshot();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.toString());
        }
    }

    public Object prevalentSystem() {
        return prevayler.prevalentSystem();
    }

    public Clock clock() {
        return prevayler.clock();
    }

    public void execute(Transaction transaction) {
        ClusteredTransaction clusterTransaction = new ClusteredTransaction(transaction, clock().time());
        System.out.println("ClusterTransaction created = " + clusterTransaction);
        node.broadcast(clusterTransaction);
    }

    public Object execute(Query sensitiveQuery) throws Exception {
        return prevayler.execute(sensitiveQuery);
    }

    public Object execute(TransactionWithQuery transactionWithQuery) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void takeSnapshot() throws IOException {
        prevayler.takeSnapshot();
    }

    public void close() {
        node.shutdown();
    }

    private void backupOldLocalTransactionLogs() {
        System.out.println("Moving away old local transaction logs");
        File oldPrevalenceBaseFile = new File(oldPrevalenceBase);
        File[] files = oldPrevalenceBaseFile.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
        oldPrevalenceBaseFile.delete();

        new File(newPrevalenceBase).renameTo(oldPrevalenceBaseFile);
    }

    public void executeBroadcastedTransaction(Transaction transaction) {
        prevayler.execute(transaction);
    }
}
