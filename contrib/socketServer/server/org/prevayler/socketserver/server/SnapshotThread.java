package org.prevayler.socketserver.server;

/*
 * prevayler.socketServer, a socket-based server (and client library)
 * to help create client-server Prevayler applications
 * 
 * Copyright (C) 2003 Advanced Systems Concepts, Inc.
 * 
 * Written by David Orme <daveo@swtworkbench.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.io.IOException;

import org.prevayler.Prevayler;
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
    private Prevayler prevayler;
    
    public SnapshotThread(Prevayler persistenceEngine) {
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

