package org.prevayler.socketserver.server;

import java.io.IOException;

import org.prevayler.implementation.SnapshotPrevayler;
import org.prevayler.socketserver.util.Log;

/**
 * @author DaveO
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SnapshotThread extends Thread {
    private SnapshotPrevayler prevayler;
    
    public SnapshotThread(SnapshotPrevayler persistenceEngine) {
        prevayler = persistenceEngine;
    }
    
    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        super.run();

        Log.message("A system snapshot will be taken every 24h...");
        try {
            while (true) {
                Thread.sleep(1000 * 60 * 60 * 24);
                prevayler.takeSnapshot();
                Log.message("Snapshot taken at " + new java.util.Date() + "...");
            }
        } catch (IOException e) {
        	Log.error(e, "Fatal exception while taking snapshot");
        	System.exit(1);
        } catch (InterruptedException e) {
        	Log.message("Snapshot thread interrupted; thread shutting down");
        }
    }
}

